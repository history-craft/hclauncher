package com.historycraft.launcher;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MinecraftDownloader {

    public File tempMines = new File(Main.tempFolder, ".minecraft.zip");

    public void extract() throws Exception {
        if (Main.minecraftFolder.exists()) {
            System.out.println("Minecraft folder exists, skip download client");
            return;
        }
        System.out.println("Extracting folders");
        ZipFile zipFile = new ZipFile(tempMines);
        zipFile.extractAll(Main.minecraftFolder.getAbsolutePath());
    }


    public void configureLauncher() throws Exception {
        File tlauncherConfigFolder = new File(Main.appDatFolder, ".tlauncher");
        if (tlauncherConfigFolder.exists()) {
            System.out.println("Launcher config exists");
            return;
        }
        tlauncherConfigFolder.mkdir();

        File tlauncherConfigFile = new File(tlauncherConfigFolder, "tlauncher-2.0.properties");

        File tlauncherConfigFileNew = Utils.getFileFromResources("tlauncher-2.0.properties");

        List<String> allLines = Files.readAllLines(tlauncherConfigFileNew.toPath(), StandardCharsets.UTF_8);

        List<String> newLines = new ArrayList<>();
        for (String lile : allLines) {
            lile = lile.replace("minecraftdir", Main.minecraftFolder.getAbsolutePath().replace("\\", "\\\\"));
            newLines.add(lile);
        }
        Files.write(tlauncherConfigFile.toPath(), newLines);
    }


    public void download() throws Exception {
        if (Main.minecraftFolder.exists()) {
            System.out.println("Minecraft folder exists, skip download client");
            return;
        }

        System.out.println("Configuring minecraft client folder");

        if (!tempMines.exists()) {
            System.out.println("minecraft found in temp folder");
            tempMines.delete();
        }


        URL urlMines = new URL(Main.configLauncher.getUrlMinecraft());

        BufferedInputStream bis = new BufferedInputStream(urlMines.openStream());

        CustomProgressMonitorInputStream pmis = new CustomProgressMonitorInputStream(
                Main.progressionFrame,
                bis);

        Main.progressionFrame.setMaximum(Utils.getFileSize(urlMines));

        System.out.println("downloading minecraft");
        FileUtils.copyInputStreamToFile(pmis, tempMines);
        System.out.println("Download ended");
    }
}
