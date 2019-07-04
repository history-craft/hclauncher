package com.historycraft.launcher;

import java.io.File;
import java.net.URL;

public class Utils {

    public static void registerException(Throwable throwable) {
        throwable.printStackTrace();
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
