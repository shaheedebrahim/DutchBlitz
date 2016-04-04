package com.cpsc441.project.dutchblitz;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cpsc441.project.dutchblitz.Activities.WaitingRoomActivity;
import com.cpsc441.project.dutchblitz.Fragments.ChatFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;


public class ChatService extends IntentService {

    public ChatService() {
        super("ChatService");
    }

    protected void onHandleIntent(Intent intent) {
        try {
            Log.d(":SLD:FLKSJD:LFKJ", ";alskdjf;laskdjf;laksdjf;laskdjf;laskdjf;laskdfj");
            BufferedReader in = new BufferedReader(new InputStreamReader(WaitingRoomActivity.sock.getInputStream()));

            while (in.readLine() != null) {

                Log.d("CHAT SERVICE: ", "MESSAGE RECEIVED");
                String message = in.readLine();
                String uname = in.readLine();

                Intent intenti = new Intent();

                intenti.setAction(ChatFragment.CHAT_ACTIVITY);
                intenti.putExtra("a", "chat");
                intenti.putExtra("username", uname);
                intenti.putExtra("message", message);
                sendBroadcast(intenti);
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

