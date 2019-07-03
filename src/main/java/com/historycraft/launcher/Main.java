package com.historycraft.launcher;

import java.io.File;
import java.net.URL;

public class Main {


    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String appDatFolder = System.getenv("APPDATA");
    public static File minecraftFolder = new File(Main.appDatFolder, ".minecraft");
    public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/fpozew9aeootoy3/.minecraft.zip";
    public static String[] folderToCheck = new String [] {"scripts", "mods", "config", "resourcepacks", "resources"};

    public static void main(String[] args) {
      //  new MinecraftDownloader().download();

        FileChecksum fileChecksum = new FileChecksum(minecraftFolder);
        fileChecksum.saveJsonFile();

        //   org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);
    }


    public static File getFileFromResources(String fileName) {

        ClassLoader classLoader = Main.class.getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
}
