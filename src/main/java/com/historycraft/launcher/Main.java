package com.historycraft.launcher;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.util.Map;

public class Main {

    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String appDatFolder = System.getenv("APPDATA");
    public static File minecraftFolder = new File(Main.appDatFolder, ".minecraft");
    public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/nfnzz8j56xoj9t0/.minecraft.zip";
    public static String[] folderToCheck = new String [] {"scripts", "mods", "config", "resourcepacks", "resources"};
    public static String serverURL = "http://167.86.122.133:8080/";
  //  public static Logger log = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    public static void main(String[] args) {
        if (args.length >= 2) {
            // param: -server C:\Users\paulo\Desktop\serv-copiado\
            File serverFolder = new File(args[1]);
            FileChecksum fServer = new FileChecksum(serverFolder);
            fServer.saveJsonFile();
            return;
        }
        new MinecraftDownloader().download();

        File modsJson = new File(minecraftFolder, "mods.json");

        try{
            BufferedInputStream bis = new BufferedInputStream(new URL(Main.serverURL + "/mods.json").openStream());
            FileUtils.copyInputStreamToFile(bis, modsJson);
        } catch (Exception ex) {
            Utils.registerException(ex);
        }

        FileChecksum fileChecksum = new FileChecksum(minecraftFolder);
        Map<String, FileChecksum.FileDifference> map = FileChecksum.compareJsonFile(fileChecksum.loadJsonFile(), fileChecksum.generate());
        for(String file: map.keySet()) {
            System.out.println("File: " + file + " diff: " + map.get(file));
        }

        //TODO - verify client against server, compare file, and download necessary files

      //  org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);
    }
}
