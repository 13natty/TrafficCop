package com.nattysoft.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.microblink.activity.Pdf417ScanActivity;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.blinkbarcode.BarcodeType;
import com.microblink.recognizers.blinkbarcode.bardecoder.BarDecoderScanResult;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417RecognizerSettings;
import com.microblink.recognizers.blinkbarcode.pdf417.Pdf417ScanResult;
import com.microblink.recognizers.blinkbarcode.usdl.USDLScanResult;
import com.microblink.recognizers.blinkbarcode.zxing.ZXingScanResult;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.results.barcode.BarcodeDetailedData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.GregorianCalendar;

public class Ticket extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1337;
    private String TAG = Ticket.class.getSimpleName();
    EditText rsa, passport, license, foreign, male, female;
    Button scanCar, next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        setTitle("Ticket");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        scanCar = (Button)findViewById(R.id.scan_car);
        scanCar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Intent for Pdf417ScanActivity Activity
                Intent intent = new Intent(Ticket.this, Pdf417ScanActivity.class);

                // set your licence key
                // obtain your licence key at http://microblink.com/login or
                // contact us at http://help.microblink.com
                intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "YK2YMQJG-NPTLQDVU-M7T6UZK2-7A5GUPWO-RECWCSC4-EZWVYJTN-LQTG2XBG-NVOHPPNZ");

                // disable showing of dialog after scan
                intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN, false);

                RecognitionSettings settings = new RecognitionSettings();
                // setup array of recognition settings (described in chapter "Recognition
                // settings and results")
                settings.setRecognizerSettingsArray(setupSettingsArray());
                intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS, settings);

                // Starting Activity
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });

        next = (Button)findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Ticket.this, Infringement.class);
                //myIntent.putExtra("idnumber", idInput.getText().toString()); //Optional parameters
                Ticket.this.startActivity(myIntent);
            }
        });


        rsa = (EditText)findViewById(R.id.idtype_field_rsa);
        rsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passport.setText("");
                license.setText("");
                foreign.setText("");
                rsa.setText("X");
            }
        });
        passport = (EditText)findViewById(R.id.idtype_field_passport);
        passport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passport.setText("X");
                license.setText("");
                foreign.setText("");
                rsa.setText("");
            }
        });
        license = (EditText)findViewById(R.id.idtype_field_license);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passport.setText("");
                license.setText("X");
                foreign.setText("");
                rsa.setText("");
            }
        });
        foreign = (EditText)findViewById(R.id.idtype_field_foreign);
        foreign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passport.setText("");
                license.setText("");
                foreign.setText("X");
                rsa.setText("");
            }
        });
        male = (EditText)findViewById(R.id.gender_field_male);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setText("X");
                female.setText("");
            }
        });
        female = (EditText)findViewById(R.id.gender_field_female);
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setText("");
                female.setText("X");
            }
        });
    }

    private RecognizerSettings[] setupSettingsArray() {
        Pdf417RecognizerSettings sett = new Pdf417RecognizerSettings();
        // disable scanning of white barcodes on black background
        sett.setInverseScanning(true);
        // allow scanning of barcodes that have invalid checksum
        sett.setUncertainScanning(true);
        // disable scanning of barcodes that do not have quiet zone
        // as defined by the standard
        sett.setNullQuietZoneAllowed(true);

        // now add sett to recognizer settings array that is used to configure
        // recognition
        return new RecognizerSettings[] { sett };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_REQUEST_CODE && resultCode == Pdf417ScanActivity.RESULT_OK) {
            // First, obtain recognition result
            RecognitionResults results = data.getParcelableExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_RESULTS);
            // Get scan results array. If scan was successful, array will contain at least one element.
            // Multiple element may be in array if multiple scan results from single image were allowed in settings.
            BaseRecognitionResult[] resultArray = results.getRecognitionResults();

            // Each recognition result corresponds to active recognizer. As stated earlier, there are 4 types of
            // recognizers available (PDF417, Bardecoder, ZXing and USDL), so there are 4 types of results
            // available.

            StringBuilder sb = new StringBuilder();
            String stringData = null;

            for (BaseRecognitionResult res : resultArray) {
                if (res instanceof Pdf417ScanResult) { // check if scan result is result of Pdf417 recognizer
                    Pdf417ScanResult result = (Pdf417ScanResult) res;
                    // getStringData getter will return the string version of barcode contents
                    String barcodeData = result.getStringData();
                    stringData = barcodeData;
                    // isUncertain getter will tell you if scanned barcode contains some uncertainties
                    boolean uncertainData = result.isUncertain();
                    // getRawData getter will return the raw data information object of barcode contents
                    BarcodeDetailedData rawData = result.getRawData();
                    // BarcodeDetailedData contains information about barcode's binary layout, if you
                    // are only interested in raw bytes, you can obtain them with getAllData getter
                    byte[] rawDataBuffer = rawData.getAllData();

                    // if data is URL, open the browser and stop processing result
                    if (checkIfDataIsUrlAndCreateIntent(barcodeData)) {
                        return;
                    } else {
                        // add data to string builder
                        sb.append("PDF417 scan data");
                        if (uncertainData) {
                            sb.append("This scan data is uncertain!\n\n");
                        }
                        sb.append(" string data:\n");
                        sb.append(barcodeData);
                        if (rawData != null) {
                            sb.append("\n\n");
                            sb.append("PDF417 raw data:\n");
                            sb.append(rawData.toString());
                            sb.append("\n");
                            sb.append("PDF417 raw data merged:\n");
                            sb.append("{");
                            for (int i = 0; i < rawDataBuffer.length; ++i) {
                                sb.append((int) rawDataBuffer[i] & 0x0FF);
                                if (i != rawDataBuffer.length - 1) {
                                    sb.append(", ");
                                }
                            }
                            sb.append("}\n\n\n");
                        }
                    }
                } else if (res instanceof BarDecoderScanResult) { // check if scan result is result of BarDecoder recognizer
                    BarDecoderScanResult result = (BarDecoderScanResult) res;
                    // with getBarcodeType you can obtain barcode type enum that tells you the type of decoded barcode
                    BarcodeType type = result.getBarcodeType();
                    // as with PDF417, getStringData will return the string contents of barcode
                    String barcodeData = result.getStringData();
                    if (checkIfDataIsUrlAndCreateIntent(barcodeData)) {
                        return;
                    } else {
                        sb.append(type.name());
                        sb.append(" string data:\n");
                        sb.append(barcodeData);
                        sb.append("\n\n\n");
                    }
                } else if (res instanceof ZXingScanResult) { // check if scan result is result of ZXing recognizer
                    ZXingScanResult result = (ZXingScanResult) res;
                    // with getBarcodeType you can obtain barcode type enum that tells you the type of decoded barcode
                    BarcodeType type = result.getBarcodeType();
                    // as with PDF417, getStringData will return the string contents of barcode
                    String barcodeData = result.getStringData();
                    if (checkIfDataIsUrlAndCreateIntent(barcodeData)) {
                        return;
                    } else {
                        sb.append(type.name());
                        sb.append(" string data:\n");
                        sb.append(barcodeData);
                        sb.append("\n\n\n");
                    }
                } else if (res instanceof USDLScanResult) { // check if scan result is result of US Driver's Licence recognizer
                    USDLScanResult result = (USDLScanResult) res;

                    // USDLScanResult can contain lots of information extracted from driver's licence
                    // you can obtain information using the getField method with keys defined in
                    // USDLScanResult class

                    String name = result.getField(USDLScanResult.kCustomerFullName);
                    Log.i(TAG, "Customer full name is " + name);

                    sb.append(result.getTitle());
                    sb.append("\n\n");
                    sb.append(result.toString());
                }
            }
            Log.d(TAG, "DATA ..........  " + stringData);
            String[] separeted = stringData.split("%");

            Log.d(TAG, "DATA separates..........  " + Arrays.toString(separeted));
            Log.d(TAG, "DATA separated size..........  " + separeted.length);
            if(separeted.length == 15){

                EditText exp_no = (EditText)findViewById(R.id.exp_date);
                String expDateString =  separeted[14];
                GregorianCalendar expDate = new GregorianCalendar( Integer.parseInt(expDateString.substring(0, 3)), Integer.parseInt(expDateString.substring(5, 6)), Integer.parseInt(expDateString.substring(8)) ); // midnight
                GregorianCalendar now = new GregorianCalendar();

                boolean isExpired = now.after( expDate );
                if(isExpired){
                    exp_no.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                }else{
                    exp_no.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP);
                }
                exp_no.setText(separeted[14]);

                EditText vin_no = (EditText)findViewById(R.id.vin_no);
                vin_no.setText(separeted[12]);

                EditText lic_no = (EditText)findViewById(R.id.licence_no);
                lic_no.setText(separeted[6]);

                EditText lic_disc = (EditText)findViewById(R.id.licence_disc);
                lic_disc.setText(separeted[5]);

                EditText desc = (EditText)findViewById(R.id.desc);
                desc.setText(separeted[8]);

                EditText gmv = (EditText)findViewById(R.id.gmv);
                gmv.setText(separeted[1]);

                EditText make = (EditText)findViewById(R.id.make);
                make.setText(separeted[9]);

                EditText model = (EditText)findViewById(R.id.model);
                model.setText(separeted[10]);

                EditText colour = (EditText)findViewById(R.id.colour);
                colour.setText(separeted[11]);

                RelativeLayout vehicle = (RelativeLayout)findViewById(R.id.vehicle_layout);
                vehicle.setVisibility(View.VISIBLE);

                scanCar.setVisibility(View.INVISIBLE);

                colour.setFocusableInTouchMode(true);
                colour.requestFocus();
            }else{
                new AlertDialog.Builder(Ticket.this)
                        .setTitle("Barcode Not Car Disc")
                        .setMessage("Please Scan Car Disc")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }


        }
    }

    /**
     * Checks whether data is URL and in case of URL data creates intent and starts activity.
     * @param data String to check.
     * @return If data is URL returns {@code true}, else returns {@code false}.
     */
    private boolean checkIfDataIsUrlAndCreateIntent(String data) {
        // if barcode contains URL, create intent for browser
        // else, contain intent for message
        boolean barcodeDataIsUrl;
        try {
            @SuppressWarnings("unused")
            URL url = new URL(data);
            barcodeDataIsUrl = true;
        } catch (MalformedURLException exc) {
            barcodeDataIsUrl = false;
        }

        if (barcodeDataIsUrl) {
            // create intent for browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(data));
            //startActivity(Intent.createChooser(intent, getString(R.string.UseWith)));
        }
        return barcodeDataIsUrl;
    }
}
