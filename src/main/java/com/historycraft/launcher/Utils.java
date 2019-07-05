package com.historycraft.launcher;

import sun.net.www.protocol.http.HttpURLConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

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

    public static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }
}
