package com.historycraft.launcher;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.util.Map;

public class Main {

    //TODO - find a easy http server to be able to download files from server

    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String appDatFolder = System.getenv("APPDATA");
    public static File minecraftFolder = new File(Main.appDatFolder, ".minecraft");
    public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/fpozew9aeootoy3/.minecraft.zip";
    public static String[] folderToCheck = new String [] {"scripts", "mods", "config", "resourcepacks", "resources"};
    public static String serverURL = "http://167.86.122.133:8080/";

    public static void main(String[] args) {
        if (args.length >= 2) {
            // param: -server C:\Users\paulo\Desktop\serv-copiado\
            File serverFolder = new File(args[1]);
            FileChecksum fserver = new FileChecksum(serverFolder);
            fserver.saveJsonFile();


            FileChecksum fclient = new FileChecksum(minecraftFolder);
            fclient.saveJsonFile();


            FileChecksum.compareJsonFile(fserver.loadJsonFile(), fclient.loadJsonFile());



//            try {
//                for(String folder: folderToCheck) {
//                    if ("mods".equals(folder)) continue;
//                    ZipFile zipFile = new ZipFile(new File(minecraftFolder, folder + ".zip"));
//                    zipFile.addFolder(new File(minecraftFolder, folder));
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
            return;
        }
        new MinecraftDownloader().download();

        //TODO - verify client against server, compare file, and download necessary files

        org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);
    }
}
