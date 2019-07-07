package com.historycraft.launcher;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Map;

public class Main {

    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String appDatFolder = System.getenv("APPDATA");
    public static File minecraftFolder = new File(Main.appDatFolder, ".minecraft");
   // public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/nfnzz8j56xoj9t0/.minecraft.zip";
    public static String[] folderToCheck = new String [] {"scripts", "mods", "config", "resourcepacks", "resources"};
    public static ConfigLauncher configLauncher;
    public static ProgressionFrame progressionFrame;
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("-------------starting the launcher--------------");
        if (args.length >= 2) {
            log.info("Doing server stuffs");
            File serverFolder = new File(args[1]);
            FileChecksum fServer = new FileChecksum(serverFolder);
            log.info("Generating checksum");
            fServer.saveJsonFile();
            log.info("Checksum successfully generated");
            return;
        }

        LocalConfig localConfig = null;

        try{
            File folderConfig = new File(appDatFolder, ".hclauncher");
            if (!folderConfig.exists()) {
                log.info("Folder {} not found, let's create a new one", folderConfig);
                folderConfig.mkdirs();
            }

            File configFile = new File(folderConfig, "hclauncher.json");
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();

            if (configFile.exists()) {
                log.info("configFile found");
                localConfig = gson.fromJson(new FileReader(configFile), LocalConfig.class);
                log.info("configFile loaded successfully");
            } else {
                log.info("configFile not found");
                localConfig = new LocalConfig();
                int dialogResult = JOptionPane.showConfirmDialog (null, "Do you want to use tlauncher?","Warning",JOptionPane.YES_NO_OPTION);
                localConfig.setUseTlauncher(dialogResult == JOptionPane.YES_OPTION);
                if (!localConfig.isUseTlauncher()){
                    log.info("Selected to use custom launcher");
                    FileDialog dialog = new FileDialog((Frame)null, "Select Minecraft launcher");
                    dialog.setMode(FileDialog.LOAD);
                    dialog.setVisible(true);
                    localConfig.setCustomLauncher(new File(dialog.getDirectory(), dialog.getFile()).getAbsolutePath());
                    dialog.dispose();
                    log.info("Custom launcher folder is {}", localConfig);
                }

                PrintWriter writer = new PrintWriter(configFile);
                writer.print(gson.toJson(localConfig, LocalConfig.class));
                writer.close();
                log.info("successfully wrote local config file");
            }
        } catch (Exception ex) {
            log.error("A error happened in local config file ", ex);
        }

        progressionFrame = new ProgressionFrame();
        progressionFrame.init();
        progressionFrame.setProcessName("Download configuration file");
        progressionFrame.reset();
        progressionFrame.setMaximum(2);
        progressionFrame.incrementValue();

        log.info("Downloading launcher-config.json");

        try{
            BufferedInputStream bis = new BufferedInputStream(Utils.getConnection("/launcher-config.json"));
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

        log.info("Doing client stuffs");

        File modsJson = new File(minecraftFolder, "mods.json");
        try{
            BufferedInputStream bis = new BufferedInputStream(Utils.getConnection("/mods.json"));
            FileUtils.copyInputStreamToFile(bis, modsJson);
        } catch (Exception ex) {
            Utils.registerException(ex);
        }

        if (configLauncher != null) {

            FileChecksum fileChecksum = new FileChecksum(minecraftFolder);

            Map<String, FileChecksum.FileDifference> map = FileChecksum.compareJsonFile(fileChecksum.loadJsonFile(), fileChecksum.generate());

            log.info("{} different files found", map.keySet().size());

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
                   // log.debug("File {} skipped", file);
                    continue;
                }
                File clientFile = new File(minecraftFolder, file);
                if (!map.get(file).equals(FileChecksum.FileDifference.ONLY_EXISTS_IN_CLIENT)) {
                    try{
                        progressionFrame.setProcessName("Downloading " + clientFile.getName());
                        log.info("Trying to download: {}", file);
                        BufferedInputStream bis = new BufferedInputStream(Utils.getConnection(UrlEscapers.urlFragmentEscaper().escape(file)));
                        FileUtils.copyInputStreamToFile(bis, clientFile);
                        log.info("File: {} updated ", file);
                    } catch (Exception ex) {
                        Utils.registerException(ex);
                    }
                } else {
                    if (clientFile.exists()) {
                        clientFile.delete();
                        log.info("File: {} deleted ", file);
                    }
                }
            }
        }

        progressionFrame.dispose();

        if (localConfig.isUseTlauncher())
            log.info("opening tlauncher");
            org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);

        if (localConfig.getCustomLauncher() != null && !"".equals(localConfig.getCustomLauncher())) {
            try {
                File file = new File(localConfig.getCustomLauncher());
                if (! file.exists()) {
                    throw new IllegalArgumentException("The file " + localConfig.getCustomLauncher() + " does not exist");
                }
                log.info("opening {}", file.getAbsolutePath());
                Process p = Runtime.getRuntime().exec(file.getAbsolutePath());
            } catch (Exception ex) {
                Utils.registerException(ex);
            }
        }
    }
}
