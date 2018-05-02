package com.nattysoft.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LicenseResultActivity extends AppCompatActivity {

    private String Tag = LicenseResultActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_result);

        Intent intent = getIntent();
        String results = intent.getStringExtra("results");


    }
}
