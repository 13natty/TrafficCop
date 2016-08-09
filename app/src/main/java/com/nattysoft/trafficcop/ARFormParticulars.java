package com.nattysoft.trafficcop;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ARFormParticulars extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    final int sdk = android.os.Build.VERSION.SDK_INT;
    int numDrivers = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arform_particulars);

        addParticularsOfDriver();

    }

    @SuppressLint("NewApi")
    private void addParticularsOfDriver() {

        TableLayout arTable = (TableLayout)findViewById(R.id.particulars_table);

        if(numDrivers>1){
            TableRow tr_title = new TableRow(this);
            tr_title.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView textview_title = new TextView(this);
            textview_title.setBackgroundColor(Color.LTGRAY);
            textview_title.setText("Particulars of Driver " + String.valueOf((char) ((numDrivers) + 64)));
            tr_title.addView(textview_title);

            textview_title.setGravity(Gravity.CENTER);
            tr_title.setGravity(Gravity.CENTER);

            arTable.addView(tr_title, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        }

        TableRow tr1 = new TableRow(this);
        tr1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textviewIdType = new TextView(this);
        textviewIdType.setText("ID type");
        tr1.addView(textviewIdType);


        TextView textviewId_slash = new TextView(this);
        textviewId_slash.setText(" / ");
        tr1.addView(textviewId_slash);

        TextView textviewId_number = new TextView(this);
        textviewId_number.setText("ID number");
        tr1.addView(textviewId_number);

        arTable.addView(tr1, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr2 = new TableRow(this);
        tr2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText editTextIdType = new EditText(this);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextIdType.setBackgroundDrawable( getResources().getDrawable(R.drawable.edittextstyle) );
            editTextIdType.setPadding(0,0,0,15);
        } else {
            editTextIdType.setBackground( getResources().getDrawable(R.drawable.edittextstyle));
            editTextIdType.setPadding(0,0,0,15);
        }
        tr2.addView(editTextIdType);


        TextView editTextId_slash = new TextView(this);
        editTextId_slash.setText(" / ");
        tr2.addView(editTextId_slash);

        EditText editTextId_number = new EditText(this);
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextId_number.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextId_number.setPadding(0, 0, 0, 15);
        } else {
            editTextId_number.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextId_number.setPadding(0, 0, 0, 15);
        }
        tr2.addView(editTextId_number);

        arTable.addView(tr2, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        //----------------------------------------------- age and country of origin ------------------------------------------------

        TableRow tr3 = new TableRow(this);
        tr3.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textviewCountryOfOrigin = new TextView(this);
        textviewCountryOfOrigin.setText("Country Of Origin");
        tr3.addView(textviewCountryOfOrigin);


        TextView textviewCountry_slash = new TextView(this);
        textviewCountry_slash.setText(" / ");
        tr3.addView(textviewCountry_slash);

        TextView textviewAge = new TextView(this);
        textviewAge.setText("Age");
        tr3.addView(textviewAge);

        arTable.addView(tr3, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr4 = new TableRow(this);
        tr4.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText editTextCountryOfOrig = new EditText(this);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextCountryOfOrig.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextCountryOfOrig.setPadding(0, 0, 0, 15);
        } else {
            editTextCountryOfOrig.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextCountryOfOrig.setPadding(0, 0, 0, 15);
        }
        tr4.addView(editTextCountryOfOrig);


        TextView editTextOrigin_slash = new TextView(this);
        editTextOrigin_slash.setText(" / ");
        tr4.addView(editTextOrigin_slash);

        EditText editTextAge = new EditText(this);
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextAge.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextAge.setPadding(0, 0, 0, 15);
        } else {
            editTextAge.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextAge.setPadding(0, 0, 0, 15);
        }
        tr4.addView(editTextAge);

        arTable.addView(tr4, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        //----------------------------------------------- Name / Surname ------------------------------------------------

        TableRow tr5 = new TableRow(this);
        tr5.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textviewFullName = new TextView(this);
        textviewFullName.setText("Full Name");
        tr5.addView(textviewFullName);


        TextView textviewFullName_slash = new TextView(this);
        textviewFullName_slash.setText(" / ");
        tr5.addView(textviewFullName_slash);

        TextView textviewSurname = new TextView(this);
        textviewSurname.setText("Surname");
        tr5.addView(textviewSurname);

        arTable.addView(tr5, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr6 = new TableRow(this);
        tr6.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText editTextFullName = new EditText(this);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextFullName.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextFullName.setPadding(0, 0, 0, 15);
        } else {
            editTextFullName.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextFullName.setPadding(0, 0, 0, 15);
        }
        tr6.addView(editTextFullName);


        TextView editTextFullName_slash = new TextView(this);
        editTextFullName_slash.setText(" / ");
        tr6.addView(editTextFullName_slash);

        EditText editTextSurname = new EditText(this);
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextSurname.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextSurname.setPadding(0, 0, 0, 15);
        } else {
            editTextSurname.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextSurname.setPadding(0, 0, 0, 15);
        }
        tr6.addView(editTextSurname);

        arTable.addView(tr6, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        //----------------------------------------------- initials / othernames ------------------------------------------------

        TableRow tr7 = new TableRow(this);
        tr7.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textviewInitials = new TextView(this);
        textviewInitials.setText("Initials other names");
        tr7.addView(textviewInitials);


        TextView textviewInitials_slash = new TextView(this);
        textviewInitials_slash.setText(" / ");
        tr7.addView(textviewInitials_slash);

        TextView textviewAddress = new TextView(this);
        textviewAddress.setText("Residential/Home Address");
        tr7.addView(textviewAddress);

        arTable.addView(tr7, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr8 = new TableRow(this);
        tr8.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText editTextInitials = new EditText(this);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextInitials.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextInitials.setPadding(0, 0, 0, 15);
        } else {
            editTextInitials.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextInitials.setPadding(0, 0, 0, 15);
        }
        tr8.addView(editTextInitials);


        TextView editTextInitials_slash = new TextView(this);
        editTextInitials_slash.setText(" / ");
        tr8.addView(editTextInitials_slash);

        EditText editTextAddress = new EditText(this);
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextAddress.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextAddress.setPadding(0, 0, 0, 15);
        } else {
            editTextAddress.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextAddress.setPadding(0, 0, 0, 15);
        }
        tr8.addView(editTextAddress);

        arTable.addView(tr8, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        //----------------------------------------------- Telephone / Work Contact  ------------------------------------------------

        TableRow tr9 = new TableRow(this);
        tr9.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textviewTelephone = new TextView(this);
        textviewTelephone.setText("Telephone Number");
        tr9.addView(textviewTelephone);


        TextView textviewTelephone_slash = new TextView(this);
        textviewTelephone_slash.setText(" / ");
        tr9.addView(textviewTelephone_slash);

        TextView textviewContactAddress = new TextView(this);
        textviewContactAddress.setText("Work/Contact Address");
        tr9.addView(textviewContactAddress);

        arTable.addView(tr9, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr10 = new TableRow(this);
        tr10.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText editTextTelephone = new EditText(this);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextTelephone.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextTelephone.setPadding(0, 0, 0, 15);
        } else {
            editTextTelephone.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextTelephone.setPadding(0, 0, 0, 15);
        }
        tr10.addView(editTextTelephone);


        TextView editTextTelephone_slash = new TextView(this);
        editTextTelephone_slash.setText(" / ");
        tr10.addView(editTextTelephone_slash);

        EditText editTextContact = new EditText(this);
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextContact.setPadding(0, 0, 0, 15);
        } else {
            editTextContact.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextContact.setPadding(0, 0, 0, 15);
        }
        tr10.addView(editTextContact);

        arTable.addView(tr10, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        //----------------------------------------------- Cellphone / Race  ------------------------------------------------

        TableRow tr11 = new TableRow(this);
        tr11.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView textviewCellphone = new TextView(this);
        textviewCellphone.setText("Cellphone other Number");
        tr11.addView(textviewCellphone);


        TextView textviewCelphone_slash = new TextView(this);
        textviewCelphone_slash.setText(" / ");
        tr11.addView(textviewCelphone_slash);

        TextView textviewRace = new TextView(this);
        textviewRace.setText("Driver Ethnicity");
        tr11.addView(textviewRace);

        arTable.addView(tr11, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        TableRow tr12 = new TableRow(this);
        tr12.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        EditText editTextCelphone = new EditText(this);

        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            editTextCelphone.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            editTextCelphone.setPadding(0, 0, 0, 15);
        } else {
            editTextCelphone.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            editTextCelphone.setPadding(0, 0, 0, 15);
        }
        tr12.addView(editTextCelphone);


        TextView editTextCelphone_slash = new TextView(this);
        editTextCelphone_slash.setText(" / ");
        tr12.addView(editTextCelphone_slash);

        Spinner spinnerRace = new Spinner(this);
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            spinnerRace.setBackgroundDrawable(getResources().getDrawable(R.drawable.edittextstyle));
            spinnerRace.setPadding(0, 0, 0, 15);
        } else {
//            spinnerRace.setBackground(getResources().getDrawable(R.drawable.edittextstyle));
            spinnerRace.setPadding(0,0,0,15);
        }

        // Spinner click listener
        spinnerRace.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Asian");
        categories.add("Black");
        categories.add("Coloured");
        categories.add("White");
        categories.add("Other");
        categories.add("Unknown");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerRace.setAdapter(dataAdapter);

        tr12.addView(spinnerRace);

        arTable.addView(tr12, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));

        final Button addDriver = new Button(this);
        addDriver.setText("Add Driver "+String.valueOf((char)((numDrivers+1) + 64)));
        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDriver.setVisibility(View.INVISIBLE);
                numDrivers++;
                addParticularsOfDriver();
            }
        });

        TableRow tr13 = new TableRow(this);
        tr13.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

        tr13.addView(addDriver);

        arTable.addView(tr13, new TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_arform_particulars, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
