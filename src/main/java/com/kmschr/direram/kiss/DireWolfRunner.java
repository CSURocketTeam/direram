package com.kmschr.direram.kiss;

import com.kmschr.direram.Client;

public class DireWolfRunner implements Runnable {

    Client client;
    String exePath;

    public DireWolfRunner() {
        this.client = Client.getInstance();
        client.println("> Loading direwolf.exe");
        try {
            BinLoader.getBinLoader().copyFile("direwolf.conf");
            this.exePath = BinLoader.getBinLoader().copyExe("direwolf.exe");
        } catch (Exception e) {
            client.println("> Error loading direwolf.exe");
            client.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Start Dire Wolf and make sure it gets killed when app closes
            client.println("> Starting Dire Wolf...");

            // Run completely detached since it breaks when ran as a child process
            // This works for Windows only
            Runtime.getRuntime().exec("cmd /c start " + exePath + " -c " + BinLoader.getBinLoader().getTempPath() + "\\" + "direwolf.conf");

            //final Process dw = new ProcessBuilder(exePath).start();
            //Runtime.getRuntime().addShutdownHook(new Thread(dw::destroy));

            client.println("> Started Dire Wolf");
            // Give Dire Wolf Time to spin up before launching KISS Util
            Thread.sleep(1000);
            new Thread(new KissRunner()).start();
        } catch (Exception e) {
            client.println("> Error in Dire Wolf runner");
            client.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
