package com.historycraft.launcher;

import com.sun.management.OperatingSystemMXBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class Utils {

    public static String serverURL = "http://167.86.122.133:56475";

    private static final Logger log = LogManager.getLogger(Utils.class);

    public enum MachineProfile {
        LOW,
        MEDIUM,
        HIGHT
    }

    public static void registerException(Throwable throwable) {
        log.error("A error happened {} ", throwable);
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

    public static InputStream getConnection(String file) {
        try {
//            java.net.CookieManager cm = new java.net.CookieManager();
//            java.net.CookieHandler.setDefault(cm);

            URL url = new URL (serverURL + file);
            String encoding = Base64.getEncoder().encodeToString("historycraft:-3Z2E9y3=2ynWJWDJcvQt^UCg_54e@j!Fzg$k6kRNhC6LTMM&Q".getBytes());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            return connection.getInputStream();
        } catch(Exception e) {
            log.error(e);
        }
        return null;
    }

}
