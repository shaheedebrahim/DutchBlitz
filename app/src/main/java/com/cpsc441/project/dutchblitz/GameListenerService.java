package com.cpsc441.project.dutchblitz;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class GameListenerService extends IntentService {
    public GameListenerService() {
        super("GameListenerService");
    }

    protected void onHandleIntent(Intent intent) {
        try {
            Socket sock = sock = new Socket("192.168.56.1", 1235);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String line = "";

            while (!(line = in.readLine()).equals("end")) {
                String[] alt = line.split("-");
                String[] top = in.readLine().split("-");

                // TODO: Process message body and broadcast to client

                //sendBroadcast(INTENT DATA);
            }
        }
        catch (UnknownHostException e) {
            System.out.println("Could not connect to specified host.");
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
