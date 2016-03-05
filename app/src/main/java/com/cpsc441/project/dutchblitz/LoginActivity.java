package com.cpsc441.project.dutchblitz;

import android.app.Activity;
import android.os.Bundle;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void loginClicked() {

    }

    public void createClicked() {

    }

}
