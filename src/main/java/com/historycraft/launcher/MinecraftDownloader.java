package com.historycraft.launcher;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MinecraftDownloader {

    private static final Logger log = LogManager.getLogger(MinecraftDownloader.class);

    public File tempMines = new File(Main.tempFolder, ".minecraft.zip");

    public void extract() throws Exception {
        if (Main.minecraftFolder.exists()) {
            log.info("Minecraft folder exists, skip extract client");
            return;
        }

        Main.progressionFrame.setProcessName("Extract files");
        Main.progressionFrame.reset();
        Main.progressionFrame.setMaximum(2);

        log.info("Extracting folders");
        ZipFile zipFile = new ZipFile(tempMines);
        zipFile.extractAll(Main.minecraftFolder.getAbsolutePath());

        Main.progressionFrame.incrementValue();
    }


    public void configureLauncher() throws Exception {
        File tlauncherConfigFolder = new File(Main.appDatFolder, ".tlauncher");
        if (tlauncherConfigFolder.exists()) {
            log.info("Launcher config exists");
            return;
        }
        tlauncherConfigFolder.mkdir();

        File tlauncherConfigFile = new File(tlauncherConfigFolder, "tlauncher-2.0.properties");

        BufferedReader br = Utils.getFileFromResources("tlauncher-2.0.properties");

        List<String> allLines = new ArrayList<>();

        String st;
        while ((st = br.readLine()) != null)
            allLines.add(st);

        //List<String> allLines = Files.readAllLines(tlauncherConfigFileNew.toPath(), StandardCharsets.UTF_8);

        Utils.MachineProfile machineProfile = Utils.machineProfile();

        String ram;
        if (machineProfile == Utils.MachineProfile.LOW) {
            ram = "-Xmx3500M";
        } else if (machineProfile == Utils.MachineProfile.MEDIUM) {
            ram = "-Xmx5G";
        } else {
            ram = "-Xmx6G";
        }

        List<String> newLines = new ArrayList<>();
        for (String lile : allLines) {
            lile = lile.replace("minecraftdir", Main.minecraftFolder.getAbsolutePath().replace("\\", "\\\\"));
            lile = lile.replace("-RAM",ram);
            newLines.add(lile);
        }
        Files.write(tlauncherConfigFile.toPath(), newLines);

        Main.progressionFrame.incrementValue();
    }


    public void download() throws Exception {
        if (Main.minecraftFolder.exists()) {
            log.info("Minecraft folder exists, skip download client");
            return;
        }

        Main.progressionFrame.setProcessName("Download minecraft");
        Main.progressionFrame.reset();

        log.info("Configuring minecraft client folder");

        if (!tempMines.exists()) {
            log.info("minecraft found in temp folder");
            tempMines.delete();
        }


        URL urlMines = new URL(Main.configLauncher.getUrlMinecraft());

        BufferedInputStream bis = new BufferedInputStream(urlMines.openStream());

        CustomProgressMonitorInputStream pmis = new CustomProgressMonitorInputStream(
                Main.progressionFrame,
                bis);

        Main.progressionFrame.setMaximum(Utils.getFileSize(urlMines));

        log.info("downloading minecraft");
        FileUtils.copyInputStreamToFile(pmis, tempMines);
        log.info("Download ended");
    }
}
