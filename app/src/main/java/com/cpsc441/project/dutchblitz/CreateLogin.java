package com.cpsc441.project.dutchblitz;

import android.app.Activity;
import android.os.Bundle;

public class CreateLogin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_login);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
