package com.cpsc441.project.dutchblitz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {

    EditText usernameText;
    EditText passwordText;


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

        new LoginTask(getApplicationContext()).execute(username, password);


    }

    public void createClicked(View view) {

        Intent intent = new Intent(this, PlayerHomeActivity.class);
        startActivity(intent);

    }
}
