package com.nattysoft.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by f3838284 on 2016/06/15.
 */
public class ScanFinger extends AppCompatActivity {
    ImageButton scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_finger);
        setTitle("Scan Finger");

        scan = (ImageButton) findViewById(R.id.finger_scanner);
        scan.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Intent myIntent = new Intent(ScanFinger.this, Ticket.class);
                ScanFinger.this.startActivity(myIntent);
                return false;
            }
        });
    }
}
