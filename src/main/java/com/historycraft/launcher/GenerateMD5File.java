package com.historycraft.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class GenerateMD5File {

    String minecraftFolder;

    public void generate(String minecraftFolder) {
        this.minecraftFolder = minecraftFolder;
        verifyFolder(new File(new File(minecraftFolder), "scripts"));
        verifyFolder(new File(new File(minecraftFolder), "mods"));
    }

    public void verifyFolder(File folder) {
        for (File file: folder.listFiles()) {
            if (file.isDirectory()) {
                verifyFolder(file);
            } else {
                try (InputStream is = Files.newInputStream(file.toPath())) {
                    String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
                    System.out.println("file: " + file.getAbsolutePath().replace(minecraftFolder, "") + " md5: " + md5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
