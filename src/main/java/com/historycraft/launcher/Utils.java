package com.historycraft.launcher;

import com.sun.management.OperatingSystemMXBean;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;

public class Utils {

    public enum MachineProfile {
        LOW,
        MEDIUM,
        HIGHT
    }

    public static void registerException(Throwable throwable) {
        throwable.printStackTrace();
    }

    public static BufferedReader getFileFromResources(String fileName) {
        return new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)));
//        ClassLoader classLoader = Main.class.getClassLoader();
//
//        URL resource = classLoader.getResource(fileName);
//        if (resource == null) {
//            throw new IllegalArgumentException("file is not found!");
//        } else {
//            return new File(resource.getFile());
//        }
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

    public static MachineProfile machineProfile() {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();

        if (bean.getTotalPhysicalMemorySize() <= 4294967296l) {
            return MachineProfile.LOW;
        } else if (bean.getTotalPhysicalMemorySize() <= 6442450944l){
            return MachineProfile.MEDIUM;
        }
        return MachineProfile.HIGHT;
    }

}
