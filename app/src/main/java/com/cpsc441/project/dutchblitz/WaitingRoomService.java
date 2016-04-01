package com.cpsc441.project.dutchblitz;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cpsc441.project.dutchblitz.Activities.WaitingRoomActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class WaitingRoomService extends IntentService {
    public WaitingRoomService() {
        super("WaitingRoomService");
    }

    protected void onHandleIntent(Intent intent) {
        Intent intenti = new Intent();
        intenti.setAction(WaitingRoomActivity.JOIN_ACTIVITY);
        sendBroadcast(intenti);
        Log.d("Test: ", "Should see this");
        try {
            Socket sock = new Socket("162.246.157.144", 1235);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String line = "";

            while (!(line = in.readLine()).equals("end")) {

                // TODO: Process message body and broadcast to client
                /*Intent intenti = new Intent();
                intenti.setAction(WaitingRoomActivity.JOIN_ACTIVITY);
                sendBroadcast(intenti);*/
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
