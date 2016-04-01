package com.cpsc441.project.dutchblitz.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cpsc441.project.dutchblitz.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.ReentrantLock;

public class LoginActivity extends Activity {

    public boolean success = false;
    EditText usernameText;
    EditText passwordText;

    public final ReentrantLock lock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        final CheckBox checkBoxValidity = (CheckBox) findViewById(R.id.checkBoxValidity);
        checkBoxValidity.setEnabled(false);

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = s.toString();
                if (message.length() > 5)
                    checkBoxValidity.setChecked(true);
                else
                    checkBoxValidity.setChecked(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void loginClicked(View view) {

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        new LoginTask(8).execute(username, password);

        lock.lock();
        try {
            if (success) {
                Intent intent = new Intent(this, PlayerHomeActivity.class);
                intent.putExtra("message", username);
                startActivity(intent);
            }
            else Toast.makeText(getApplicationContext(), "Incorrect credentials", Toast.LENGTH_LONG).show();
        }
        finally {
            lock.unlock();
        }
    }

    public void createClicked(View view) {

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        new LoginTask(11).execute(username, password);

        lock.lock();
        try {
            success = true;
            if (success) {
                Intent intent = new Intent(this, PlayerHomeActivity.class);
                intent.putExtra("message", username);
                startActivity(intent);
            }
            else Toast.makeText(getApplicationContext(), "Username taken", Toast.LENGTH_LONG).show();
        }
        finally {
            lock.unlock();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        final int PACKET_SIZE = 64;

        private Socket sock = null;
        private DataOutputStream out = null;
        private BufferedReader in = null;
        private int headVal;

        public LoginTask(int h) {
            lock.lock();
            headVal = h;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("init", "test");
            try {
                sock = new Socket(/*"162.246.157.144"*/"192.168.56.1", 1234);
                Log.d("init: ", sock.toString());
                out = new DataOutputStream(sock.getOutputStream());
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                Log.d("Init: ", "Success");
            }
            catch (UnknownHostException e) {
                System.out.println("Failed to create client socket.");
                e.printStackTrace();
            }
            catch (IOException e) {
                System.out.println("Socket creation caused error.");
                e.printStackTrace();
            }

            //String message = params[0] + " " + params[1], resp = "";
            long header = 0;
            header = header | headVal;
            header = header << 8;
            String body = params[0] + "\n" + params[1] + "\n", resp = "";
            header = header | body.length(); header = header << 16;

            try {
                // Send credentials to server - IP address is currently hard-coded
                out.writeBytes(String.valueOf(header) + "\n" + body);

                resp = in.readLine();
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
            try {
                sock.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (!res.equals("0")) {
                success = true;
                // TODO: Save user identifier somehow
            }

            lock.unlock();
        }
    }
}
