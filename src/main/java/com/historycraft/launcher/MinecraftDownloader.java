package com.historycraft.launcher;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MinecraftDownloader {

    public void download() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new URL(Main.urlMinecraft).openStream());


//            ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
//                    jFrame.getContentPane(),
//                    "downloading... ",
//                    bis);

            //pmis.getProgressMonitor().setMillisToPopup(10);

            File tempMines = new File(Main.tempFolder, ".minecraft.zip");
            if (tempMines.exists()) {
                //  tempMines.delete();
            } else {
                FileUtils.copyInputStreamToFile(bis, tempMines);
            }

            ZipFile zipFile = new ZipFile(tempMines);
            zipFile.extractAll(Main.appDatFolder);

            File tlauncherConfigFolder = new File(Main.appDatFolder, ".tlauncher");
            if(tlauncherConfigFolder.exists()){
                return;
            }
            tlauncherConfigFolder.mkdir();

            File tlauncherConfigFile = new File(tlauncherConfigFolder, "tlauncher-2.0.properties");

            File tlauncherConfigFileNew = Main.getFileFromResources("tlauncher-2.0.properties");

            List<String> allLines = Files.readAllLines(tlauncherConfigFileNew.toPath(), StandardCharsets.UTF_8);

            List<String> newLines = new ArrayList<>();
            for (String lile : allLines) {
                lile = lile.replace("minecraftdir",Main.minecraftFolder.getAbsolutePath().replace("\\","\\\\"));
                newLines.add(lile);
            }
            Files.write(tlauncherConfigFile.toPath(),newLines);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
