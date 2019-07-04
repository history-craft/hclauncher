package com.historycraft.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileChecksum {

    private final File folder;
    private final Gson gson;
    private final File modsFile;

    public FileChecksum(File folder) {
        this.folder = folder;
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        this.gson = gsonBuilder.create();
        this.modsFile = new File(this.folder, "mods.json");
    }

    public Map<String, String> generate() {
        Map<String, String> map = new HashMap<>();
        for (String folder : Main.folderToCheck) {
            map.putAll(verifyFolder(new File(this.folder, folder)));
        }
        return map;
    }

    public List<String> compare() {
        List<String> diff = new ArrayList<>();
        Map<String, String> generated = this.generate();
        Map<String, String> loaded = this.loadJsonFile();

        for (String file: loaded.keySet()) {
            String hash = loaded.get(file);
            String hash2 = generated.get(file);
            if (!hash.equals(hash2)){
                diff.add(file);
            }
        }
        return diff;
    }


    public Map<String, String> loadJsonFile() {
        try {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            return gson.fromJson(new FileReader(this.modsFile), type);
        } catch (Exception ex) {
            Utils.registerException(ex);
        }
        return null;
    }

    public void saveJsonFile() {
        try {
            if (modsFile.exists()) {
                modsFile.delete();
            }
            PrintWriter writer = new PrintWriter(modsFile);
            writer.print(gson.toJson(this.generate()));
            writer.close();
        } catch (Exception ex) {
            Utils.registerException(ex);
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
                    Utils.registerException(e);
                }
            }
        }
        return map;
    }

}
