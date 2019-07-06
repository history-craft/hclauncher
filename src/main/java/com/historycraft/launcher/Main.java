package com.historycraft.launcher;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import javax.print.attribute.standard.DialogTypeSelection;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public class Main {

    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String appDatFolder = System.getenv("APPDATA");
    public static File minecraftFolder = new File(Main.appDatFolder, ".minecraft");
   // public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/nfnzz8j56xoj9t0/.minecraft.zip";
    public static String[] folderToCheck = new String [] {"scripts", "mods", "config", "resourcepacks", "resources"};
    public static String serverURL = "http://167.86.122.133:8080/";
    public static ConfigLauncher configLauncher;
    public static ProgressionFrame progressionFrame;

    public static void main(String[] args) {

        if (args.length >= 2) {
            System.out.println("Doing server stuffs");
            File serverFolder = new File(args[1]);
            FileChecksum fServer = new FileChecksum(serverFolder);
            System.out.println("Generating checksum");
            fServer.saveJsonFile();
            System.out.println("Checksum successfully generated");
            return;
        }

        LocalConfig localConfig = null;

        try{
            File folderConfig = new File(appDatFolder, ".hclauncher");
            if (!folderConfig.exists()) {
                folderConfig.mkdirs();
            }
            File configFile = new File(folderConfig, "hclauncher.json");
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();

            if (configFile.exists()) {
                localConfig = gson.fromJson(new FileReader(configFile), LocalConfig.class);
            } else {
                localConfig = new LocalConfig();
                int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to use tlauncher?","Warning",JOptionPane.YES_NO_OPTION);
                localConfig.setUseTlauncher(dialogResult == JOptionPane.YES_OPTION);
                if (!localConfig.isUseTlauncher()){
                    FileDialog dialog = new FileDialog((Frame)null, "Select Minecraft launcher");
                    dialog.setMode(FileDialog.LOAD);
                    dialog.setVisible(true);
                    localConfig.setCustomLauncher(new File(dialog.getDirectory(), dialog.getFile()).getAbsolutePath());
                    dialog.dispose();
                }

                PrintWriter writer = new PrintWriter(configFile);
                writer.print(gson.toJson(localConfig, LocalConfig.class));
                writer.close();
            }
        } catch (Exception ex) {
            Utils.registerException(ex);
        }

        progressionFrame = new ProgressionFrame();
        progressionFrame.init();
        progressionFrame.setProcessName("Download configuration file");
        progressionFrame.reset();
        progressionFrame.setMaximum(2);
        progressionFrame.incrementValue();

        try{
            BufferedInputStream bis = new BufferedInputStream(new URL(Main.serverURL + "/launcher-config.json").openStream());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            configLauncher = gson.fromJson(new String(ByteStreams.toByteArray(bis), Charsets.UTF_8), ConfigLauncher.class);
        } catch (Exception ex) {
            Utils.registerException(ex);
        }

        progressionFrame.incrementValue();

        MinecraftDownloader downloader = new MinecraftDownloader();
        try {
            downloader.download();
            downloader.extract();
            if (localConfig.isUseTlauncher())
                downloader.configureLauncher();
        } catch (Exception ex) {
            Utils.registerException(ex);
            System.exit(1);
            return;
        }

        System.out.println("Doing client stuffs");

        File modsJson = new File(minecraftFolder, "mods.json");
        try{
            BufferedInputStream bis = new BufferedInputStream(new URL(Main.serverURL + "/mods.json").openStream());
            FileUtils.copyInputStreamToFile(bis, modsJson);
        } catch (Exception ex) {
            Utils.registerException(ex);
        }

        if (configLauncher != null) {

            FileChecksum fileChecksum = new FileChecksum(minecraftFolder);

            Map<String, FileChecksum.FileDifference> map = FileChecksum.compareJsonFile(fileChecksum.loadJsonFile(), fileChecksum.generate());

            progressionFrame.setProcessName("Download modpack files");
            Main.progressionFrame.reset();
            progressionFrame.setMaximum(map.keySet().size());

            for(String file: map.keySet()) {
                progressionFrame.incrementValue();
                boolean found = false;
                for (String ignored : configLauncher.getIgnoredFiles()){
                    if (file.contains(ignored)) {
                        found = true;
                        continue;
                    }
                }
                if (found) {
                    continue;
                }
                File clientFile = new File(minecraftFolder, file);
                if (!map.get(file).equals(FileChecksum.FileDifference.ONLY_EXISTS_IN_CLIENT)) {
                    try{
                        progressionFrame.setProcessName("Downloading " + clientFile.getName());
                        System.out.println("Trying to download: " + file);
                        BufferedInputStream bis = new BufferedInputStream(new URL(UrlEscapers.urlFragmentEscaper().escape(Main.serverURL + file)).openStream());
                        FileUtils.copyInputStreamToFile(bis, clientFile);
                        System.out.println("File: " + file + " updated ");
                    } catch (Exception ex) {
                        Utils.registerException(ex);
                    }
                } else {
                    if (clientFile.exists()) {
                        clientFile.delete();
                        System.out.println("File: " + file + " deleted ");
                    }
                }
            }
        }

        progressionFrame.dispose();

        if (localConfig.isUseTlauncher())
            org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);

        if (localConfig.getCustomLauncher() != null && !"".equals(localConfig.getCustomLauncher())) {
            try {
                File file = new File(localConfig.getCustomLauncher());
                if (! file.exists()) {
                    throw new IllegalArgumentException("The file " + localConfig.getCustomLauncher() + " does not exist");
                }
                Process p = Runtime.getRuntime().exec(file.getAbsolutePath());
            } catch (Exception ex) {
                Utils.registerException(ex);
            }
        }
    }
}
