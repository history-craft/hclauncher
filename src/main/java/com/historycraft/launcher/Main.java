package com.historycraft.launcher;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
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

        System.out.println(args);
        System.out.println(minecraftFolder);
        System.out.println(tempFolder);

        if (args.length >= 2) {
            System.out.println("Doing server stuffs");
            // param: -server C:\Users\paulo\Desktop\serv-copiado\
            File serverFolder = new File(args[1]);
            FileChecksum fServer = new FileChecksum(serverFolder);
            System.out.println("Generating checksum");
            fServer.saveJsonFile();
            System.out.println("Checksum successfully generated");
            return;
        }

        try{
            BufferedInputStream bis = new BufferedInputStream(new URL(Main.serverURL + "/launcher-config.json").openStream());
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            configLauncher = gson.fromJson(new String(ByteStreams.toByteArray(bis), Charsets.UTF_8), ConfigLauncher.class);
        } catch (Exception ex) {
            Utils.registerException(ex);
        }

        progressionFrame = new ProgressionFrame();
        progressionFrame.init();

        MinecraftDownloader downloader = new MinecraftDownloader();
        try {
            downloader.download();
            progressionFrame.setProcessName("Extract files");
            progressionFrame.setMaximum(2);
            downloader.extract();
            progressionFrame.incrementValue();
            downloader.configureLauncher();
            progressionFrame.incrementValue();
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

        FileChecksum fileChecksum = new FileChecksum(minecraftFolder);

        Map<String, FileChecksum.FileDifference> map = FileChecksum.compareJsonFile(fileChecksum.loadJsonFile(), fileChecksum.generate());

        progressionFrame.setProcessName("Download modpack files");
        progressionFrame.setMaximum(map.keySet().size());

        for(String file: map.keySet()) {
            progressionFrame.incrementValue();
            if (!map.get(file).equals(FileChecksum.FileDifference.ONLY_EXISTS_IN_CLIENT)) {
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
                try{
                    File clientFile = new File(minecraftFolder, file);
                    System.out.println("Trying to download: " + file);
                    BufferedInputStream bis = new BufferedInputStream(new URL(UrlEscapers.urlFragmentEscaper().escape(Main.serverURL + file)).openStream());
                    FileUtils.copyInputStreamToFile(bis, clientFile);
                    System.out.println("File: " + file + " updated ");
                } catch (Exception ex) {
                    Utils.registerException(ex);
                }
            }
        }
        progressionFrame.dispose();
        org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);
    }
}
