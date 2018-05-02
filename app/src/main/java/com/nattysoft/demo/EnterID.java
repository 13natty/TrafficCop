package com.nattysoft.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EnterID extends AppCompatActivity {

    EditText idInput;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_id);
        setTitle("Enter ID Number");

        idInput = (EditText)findViewById(R.id.input_id_text);

        submit = (Button)findViewById(R.id.submit_id);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idInput.getText().length() > 0) {
                    Intent myIntent = new Intent(EnterID.this, Ticket.class);
                    myIntent.putExtra("idnumber", idInput.getText().toString()); //Optional parameters
                    EnterID.this.startActivity(myIntent);
                } else {
                    new AlertDialog.Builder(EnterID.this)
                            .setTitle("ID Number Not Entered")
                            .setMessage("Please enter ID number")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }
}
