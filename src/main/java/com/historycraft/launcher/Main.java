package com.historycraft.launcher;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main {


    public static String tempFolder = System.getProperty("java.io.tmpdir");
    public static String urlMinecraft = "https://dl.dropboxusercontent.com/s/fpozew9aeootoy3/.minecraft.zip";

    public static void main(String[] args) {
        GenerateMD5File generateMD5File = new GenerateMD5File();
       // generateMD5File.generate("C:\\Users\\paulo\\AppData\\Roaming\\.minecraft\\");

        //org.tlauncher.tlauncher.rmo.Bootstrapper.main(args);

        System.out.println(tempFolder);

        downloadMinecraft2();

    }


    private static void downloadMinecraft2() {
        try {
            BufferedInputStream bis = new BufferedInputStream(new URL(urlMinecraft).openStream());


            JFrame jFrame = new JFrame();
//            JButton button = new JButton("Click me!");
//            jFrame.getContentPane().add(button);

            SwingWorker worker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
                            jFrame.getContentPane(),
                            "downloading... ",
                            bis);

                    pmis.getProgressMonitor().setMillisToPopup(10);

                    FileUtils.copyInputStreamToFile(pmis, new File(tempFolder, ".minecraft.zip"));
                    return null;
                }
            };


            jFrame.setLayout(null);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
        //    jFrame.pack();
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            worker.execute();




        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public static void downloadMinecraft() {
        try{
            URL download=new URL(urlMinecraft);
            ReadableByteChannel rbc= Channels.newChannel(download.openStream());
            FileOutputStream fileOut = new FileOutputStream(new File(tempFolder, ".minecraft.zip"));
            fileOut.getChannel().transferFrom(rbc, 0, 1 << 24);
            fileOut.flush();
            fileOut.close();
            rbc.close();
        }catch(Exception e){ e.printStackTrace(); }
    }
}
