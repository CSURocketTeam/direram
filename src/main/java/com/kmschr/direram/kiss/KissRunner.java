package com.kmschr.direram.kiss;

import com.kmschr.direram.Client;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KissRunner implements Runnable {

    Client client;
    String exePath;

    public KissRunner() {
        this.client = Client.getInstance();
        client.println("> Loading kissutil.exe");
        try {
            this.exePath = BinLoader.getBinLoader().copyExe("kissutil.exe");
        } catch (IOException e) {
            client.println("> Error loading kissutil.exe");
            client.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        String packet;
        String err;

        try {
            client.println("> Starting KISS Util...");

            // Start KISS Util and make sure it stops running when app is closed
            final Process kiss = new ProcessBuilder(exePath).start();
            Runtime.getRuntime().addShutdownHook(new Thread(kiss::destroy));

            // Hook into KISS Util Console IO
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(kiss.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(kiss.getErrorStream()));
            client.println("> Started KISS Util");

            // Process console input for packets/errors
            while ((packet = stdInput.readLine()) != null)
                client.parsePacket(packet.substring(4));
            while ((err = stdError.readLine()) != null)
                client.println(err);

            client.println("> KISS Util Shutdown");

        } catch (IOException e) {
            Platform.runLater(() -> {
                client.println("> Error with KISS Util runner");
                client.println(e.getMessage());
            });
            e.printStackTrace();
        }
    }
}
