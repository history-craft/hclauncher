package com.historycraft.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class FileChecksum {

    private File folder;

    public FileChecksum(File folder) {
        this.folder = folder;
    }

    public Map<String, String> generate() {
        Map<String, String> map = new HashMap<>();
        for (String folder : Main.folderToCheck) {
            map.putAll(verifyFolder(new File(this.folder, folder)));
        }
        return map;
    }

    public void saveJsonFile() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        try {
            File modsFile = new File(this.folder, "mods.json");
            if (modsFile.exists()) {
                modsFile.delete();
            }
            PrintWriter writer = new PrintWriter(modsFile);
            writer.print(gson.toJson(this.generate()));
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, String> verifyFolder(File folder) {
        Map<String, String> map = new HashMap<>();
        for (File file: folder.listFiles()) {
            if (file.isDirectory()) {
                map.putAll(verifyFolder(file));
            } else {
                try (InputStream is = Files.newInputStream(file.toPath())) {
                    String fileName = file.getAbsolutePath().replace(this.folder.getAbsolutePath(),"");
                    map.put(fileName, DigestUtils.md5Hex(is));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

}
