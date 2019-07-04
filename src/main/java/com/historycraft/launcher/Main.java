package com.historycraft.launcher;

import java.io.File;

public class Main {

    //TODO - find a easy http server to be able to download files from server

    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String appDatFolder = System.getenv("APPDATA");
    public static File minecraftFolder = new File(Main.appDatFolder, ".minecraft");
    public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/fpozew9aeootoy3/.minecraft.zip";
    public static String[] folderToCheck = new String [] {"scripts", "mods", "config", "resourcepacks", "resources"};

    public static void main(String[] args) {
        if (args.length >= 2) {
            // param: -server C:\Users\paulo\Desktop\serv-copiado\
            minecraftFolder = new File(args[1]);
            FileChecksum fileChecksum = new FileChecksum(minecraftFolder);
            fileChecksum.saveJsonFile();
            return;
        }
        new MinecraftDownloader().download();

        //TODO - verify client against server, compare file, and download necessary files

        org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);
    }
}
