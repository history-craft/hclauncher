package com.historycraft.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            while(true) {
                Socket socket = serverSocket.accept();

                FileInputStream fis = null;
                BufferedInputStream bis = null;
                OutputStream os = null;

                File myFile = new File ("asd");

                byte [] mybytearray  = new byte [(int)myFile.length()];

                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);

                bis.read(mybytearray,0,mybytearray.length);

                os = socket.getOutputStream();

                os.write(mybytearray,0,mybytearray.length);
                os.flush();

                System.out.println("Done.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
