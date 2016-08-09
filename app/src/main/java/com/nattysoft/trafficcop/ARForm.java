package com.nattysoft.trafficcop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ARForm extends ActionBarActivity {

    EditText area;
    EditText ar_no;
    EditText cas;
    EditText serial_number;
    EditText capturing_number;
    // calender class's instance and get current date , month and year from calender
    final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR); // current year
    int mMonth = c.get(Calendar.MONTH); // current month
    int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
    int mDayWeek = c.get(Calendar.DAY_OF_WEEK); // day of week
    int CalendarHour  = c.get(Calendar.HOUR_OF_DAY); // current hour
    int CalendarMinute = c.get(Calendar.MINUTE); // current minute

    EditText date;
    EditText day;
    EditText num_vehicles;
    EditText time;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private EditText built_yes;
    private EditText built_no;

    EditText speed_limit;
    Spinner province;

    LocationManager locationManager;
    private double longitude;
    private double latitude;

    EditText intersection_with;
    EditText main_street;
    EditText street_1;
    EditText street_2;
    EditText suburb;
    EditText city;
    EditText x_co_ordinate;
    EditText y_co_ordinate;

    Button addToAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arform);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        addToAR = (Button) findViewById(R.id.button_add_location);
        addToAR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createPdf();
                onBackPressed();
            }
        });

        area = (EditText) findViewById(R.id.area_field);
        ar_no = (EditText) findViewById(R.id.ar_no_field);
        cas = (EditText) findViewById(R.id.cas_field);
        serial_number = (EditText) findViewById((R.id.serial_field));
        capturing_number = (EditText) findViewById(R.id.captiring_field);

        // initiate the date picker and a button
        date = (EditText) findViewById(R.id.accident_date_field);
        date.setText(mDay+"/"+mMonth+"/"+mYear);

        //day of the week
        day = (EditText) findViewById(R.id.accident_day_field);

        num_vehicles = (EditText) findViewById(R.id.involved_cars_field);

        // time of accident
        time = (EditText) findViewById(R.id.accident_time_field);
        time.setText(CalendarHour + ":" + CalendarMinute);

        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // date picker dialog
                datePickerDialog = new DatePickerDialog(ARForm.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(String.format("%02d", dayOfMonth) + "/"
                                        + String.format("%02d", (monthOfYear + 1)) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                String dayStr = null;
                switch(mDayWeek){
                    case Calendar.SUNDAY :
                        dayStr = "SUNDAY";
                        break;
                    case Calendar.MONDAY :
                        dayStr = "MONDAY";
                        break;
                    case Calendar.TUESDAY :
                        dayStr = "TUESDAY";
                        break;
                    case Calendar.WEDNESDAY :
                        dayStr = "WEDNESDAY";
                        break;
                    case Calendar.THURSDAY :
                        dayStr = "THURSDAY";
                        break;
                    case Calendar.FRIDAY :
                        dayStr = "FRIDAY";
                        break;
                    case Calendar.SATURDAY :
                        dayStr = "SATURDAY";
                        break;
                }

                day.setText(dayStr);
                datePickerDialog.show();
            }
        });

        // perfom click event on time field
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // time picker dialog
                timePickerDialog = new TimePickerDialog(ARForm.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // set time in the edit text
                                time.setText(String.format("%02d", hourOfDay) + " : "
                                        + String.format("%02d", minute));

                            }
                        }, CalendarHour, CalendarMinute, true);
                timePickerDialog.show();
            }
        });

        built_yes = (EditText)findViewById(R.id.built_up_area_field_yes);
        built_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                built_no.setText("");
                built_yes.setText("X");
            }
        });

        built_no = (EditText)findViewById(R.id.built_up_area_field_no);
        built_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                built_no.setText("X");
                built_yes.setText("");
            }
        });

        speed_limit = (EditText)findViewById(R.id.speed_limit_field);

        province = (Spinner) findViewById(R.id.province_spinner);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_arform, menu);
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

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            longitude = loc.getLongitude();
            latitude = loc.getLatitude();

            main_street = (EditText) findViewById(R.id.road_name_field);
            intersection_with = (EditText) findViewById(R.id.intersection_field);
            street_1 = (EditText) findViewById(R.id.road_1_field);
            street_2 = (EditText) findViewById(R.id.road_2_field);
            suburb = (EditText) findViewById(R.id.suburb_field);
            city = (EditText) findViewById(R.id.city_field);
            x_co_ordinate = (EditText) findViewById(R.id.x_co_ordinate_field);
            x_co_ordinate.setText(""+longitude);
            y_co_ordinate = (EditText) findViewById(R.id.y_co_ordinate_field);
            y_co_ordinate.setText(""+latitude);

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                    for(int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(addresses.get(0).getAddressLine(i)).append("\n");
                    }
                    System.out.println("Address ...... :\n"+strReturnedAddress);
                    System.out.println("AdminArea ...... :\n"+addresses.get(0).getAdminArea());
                    System.out.println("FeatureName ...... :\n"+addresses.get(0).getFeatureName());
                    System.out.println("Locality ...... :\n"+addresses.get(0).getLocality());
                    System.out.println("Premises() ...... :\n"+addresses.get(0).getPremises());
                    System.out.println("SubLocality ...... :\n"+addresses.get(0).getSubLocality());
                    cityName = addresses.get(0).getLocality();
                    main_street.setText(addresses.get(0).getAddressLine(0));
                    intersection_with.setText(addresses.get(0).getSubLocality());
                    street_1.setText(addresses.get(0).getAddressLine(0));
                    street_2.setText(addresses.get(0).getSubLocality());
                    suburb.setText(addresses.get(0).getSubLocality());
                    city.setText(cityName);

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createPdf() {

        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "newFile.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Font titleFont= new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
            Font subFontLabel = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);

            Paragraph p1 = new Paragraph();
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(titleFont);

            p1.add(new Paragraph("Accident Report (AR)Form", titleFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Police Station Area : ", subFontLabel));
            p1.add(new Paragraph("" + area.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("AR no : ", subFontLabel));
            p1.add(new Paragraph("" + ar_no.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("CAS : ", subFontLabel));
            p1.add(new Paragraph("" + cas.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Serial number : ", subFontLabel));
            p1.add(new Paragraph("" + serial_number.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Capturing number : ", subFontLabel));
            p1.add(new Paragraph("" + capturing_number.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Accident Date : ", subFontLabel));
            p1.add(new Paragraph("" + date.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Day of the week : ", subFontLabel));
            p1.add(new Paragraph("" + day.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Number of Vehicles involved : ", subFontLabel));
            p1.add(new Paragraph("" + num_vehicles.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Time of accident : ", subFontLabel));
            p1.add(new Paragraph("" + time.getText(), subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("LOCATION", titleFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Built-up area : ", subFontLabel));
            p1.add(new Paragraph("" + built_yes.getText() == "X" ? "yes" : "no", subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Speed limit on road (km/h) : ", subFontLabel));
            p1.add(new Paragraph("" + speed_limit.getText() == "X" ? "yes" : "no", subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Province : ", subFontLabel));
            String[] mProvinceArray = getResources().getStringArray(R.array.provinces_arrays);
            p1.add(new Paragraph("" + mProvinceArray[province.getSelectedItemPosition()], subFont));
            addEmptyLine(p1, 1);

            p1.add(new Paragraph("Street/road name/ road number : ", subFontLabel));
            p1.add(new Paragraph("" + main_street.getText(), subFont));
            addEmptyLine(p1, 1);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        }
        finally {
            doc.close();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}
