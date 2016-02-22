package com.cpsc441.project.dutchblitz;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LoginTask extends AsyncTask<String, Void, String> {
    Context appCon = null;
    DatagramSocket sock = null;

    public LoginTask(Context con) {
        appCon = con;
    }

    @Override
    protected void onPreExecute() {
        try {
            sock = new DatagramSocket();
            Log.d("Init: ", "Success");
        }
        catch (SocketException e) {
            System.out.println("Failed to create client socket.");
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String message = params[0] + " " + params[1], resp = "";

        try {
            byte[] outBuff = message.getBytes();
            // Send credentials to server - IP address is currently hard-coded
            DatagramPacket pack = new DatagramPacket(outBuff, outBuff.length, InetAddress.getByName("192.168.1.109"), 9876);
            sock.send(pack);

            sock.receive(pack);
            resp = new String(pack.getData());
        }
        catch (UnknownHostException e) {
            System.out.println("Attempted to contact unknown host.");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("Failed to send packet.");
            e.printStackTrace();
        }

        Log.d("Android: ", "Login");
        return resp;
    }

    @Override
    protected void onPostExecute(String res) {
        Log.d("Android: ", "Exchange done");
        sock.close();
        Toast.makeText(appCon, res, Toast.LENGTH_LONG).show();
    }
}
