package com.cpsc441.project.dutchblitz;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.cpsc441.project.dutchblitz.Activities.GameScreenActivity;
import com.cpsc441.project.dutchblitz.Activities.WaitingRoomActivity;
import com.cpsc441.project.dutchblitz.Fragments.ChatFragment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class WaitingRoomService extends IntentService {

    public WaitingRoomService() {
        super("WaitingRoomService");
    }

    protected void onHandleIntent(Intent intent) {
        try {
            Log.d("WAITINGSERVICE: ", "INITIALIZED");
            if (WaitingRoomActivity.sock == null) {
                WaitingRoomActivity.sock = new Socket("162.246.157.144", 1234);
                Log.d("WAITINGSERVICE: ", String.format("New socket created %d", WaitingRoomActivity.sock.getLocalPort()));

            }
            Log.d("WAITING ROOM Server: ", "START");
            DataOutputStream out = new DataOutputStream(WaitingRoomActivity.sock.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(WaitingRoomActivity.sock.getInputStream()));
            String line = "", body = "request_names\n", idm = intent.getStringExtra("id"), start = intent.getStringExtra("start");

            if (start.equals("true")) {
                long header = 0;
                header = header | 12;
                header = header << 8;
                header = header | body.length();
                header = header << 16;
                header = header | Integer.parseInt(idm);

                try {
                    // Send credentials to server - IP address is currently hard-coded
                    out.writeBytes(String.valueOf(header) + "\n" + body);
                    String resp = "";
                    Log.d("WAITINGSERVICE: ", "STARTED");
                    while ((resp = in.readLine()) != null && !(resp).equals("0")) {
                        Intent intenti = new Intent();
                        intenti.setAction(WaitingRoomActivity.JOIN_ACTIVITY);
                        intenti.putExtra("username", resp);
                        Log.d("WAITINGSERVICE-ADD: ", resp);
                        sendBroadcast(intenti);
                    }
                    Log.d("WAITINGSERVICE: ", "FINISHED");
                } catch (UnknownHostException e) {
                    System.out.println("Attempted to contact unknown host.");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Failed to send packet.");
                    e.printStackTrace();
                }
            }

            while ((line = in.readLine()) != null && !(line).equals("end")) {
                // TODO: Process message body and broadcast to client
                if (line.equals("chat")) {
                    String message = in.readLine();
                    String uname = in.readLine();
                    Intent intenti = new Intent();
                    intenti.setAction(ChatFragment.CHAT_ACTIVITY);
                    intenti.putExtra("a", "chat");
                    intenti.putExtra("username", uname);
                    intenti.putExtra("message", message);
                    sendBroadcast(intenti);
                }
                else if (line.equals("update")) {
                    String[] update = in.readLine().split("");
                    Intent intenti = new Intent();
                    intenti.setAction(GameScreenActivity.UPDATE_ACTIVITY);
                    intenti.putExtra("colour", update[0]);
                    intenti.putExtra("value", update[1]);
                    intenti.putExtra("index", update[2]);
                    intenti.putExtra("identifier", update[3]);
                    sendBroadcast(intenti);
                }
                else if (line.equals("win")) {
                    Intent intenti = new Intent();
                    intenti.setAction(GameScreenActivity.WIN_ACTIVITY);

                }
                else {
                    Intent intenti = new Intent();
                    intenti.setAction(WaitingRoomActivity.JOIN_ACTIVITY);
                    intenti.putExtra("username", line);
                    sendBroadcast(intenti);
                }
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
