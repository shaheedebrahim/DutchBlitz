package com.cpsc441.project.dutchblitz;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameRoomTask extends AsyncTask<String, Void, String> {
    final int PACKET_SIZE = 64;

    private Context appCon = null;
    private Socket sock = null;
    private DataOutputStream out = null;
    private BufferedReader in = null;

    public GameRoomTask(Context con) {
        appCon = con;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("init", "test");
        try {
            sock = new Socket("162.246.157.144", 1234);
            Log.d("init: ", sock.toString());
            out = new DataOutputStream(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            Log.d("Init: ", "Success");
        } catch (UnknownHostException e) {
            System.out.println("Failed to create client socket.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Socket creation caused error.");
            e.printStackTrace();
        }

        //String message = params[0] + " " + params[1], resp = "";
        long header = 0;
        header = header | 8;
        header = header << 8;
        String body = params[0] + "\n", resp = "";
        header = header | body.length();
        header = header << 16;

        try {
            // Send credentials to server - IP address is currently hard-coded
            out.writeBytes(String.valueOf(header) + "\n" + body);

            resp = in.readLine();
        } catch (UnknownHostException e) {
            System.out.println("Attempted to contact unknown host.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to send packet.");
            e.printStackTrace();
        }

        Log.d("Android: ", "Login");
        return resp;
    }

    @Override
    protected void onPostExecute(String res) {
        Log.d("Android: ", "Exchange done");
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(appCon, res, Toast.LENGTH_LONG).show();
    }
}
