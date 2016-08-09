package com.nattysoft.trafficcop;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Multipart;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 1337;
    private String TAG = MainActivity.class.getSimpleName();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    private Multipart _multipart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent myIntent;

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                switch(position){
                    case 0 :
                        myIntent = new Intent(MainActivity.this, EnterID.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                    case 1 :
                        // Intent for Pdf417ScanActivity Activity
                        Intent intent = new Intent(MainActivity.this, Pdf417ScanActivity.class);

                        // set your licence key
                        // obtain your licence key at http://microblink.com/login or
                        // contact us at http://help.microblink.com
                        intent.putExtra(Pdf417ScanActivity.EXTRAS_LICENSE_KEY, "AUA4DSKL-WMYKPWHC-DNXWKT24-BM6RVLKO-IDBBSYYQ-GUMWGEBV-DFRRANIZ-MMIGIYNP");

                        // disable showing of dialog after scan
                        intent.putExtra(Pdf417ScanActivity.EXTRAS_SHOW_DIALOG_AFTER_SCAN, false);

                        RecognitionSettings settings = new RecognitionSettings();
                        // setup array of recognition settings (described in chapter "Recognition
                        // settings and results")
                        settings.setRecognizerSettingsArray(setupSettingsArray());
                        intent.putExtra(Pdf417ScanActivity.EXTRAS_RECOGNITION_SETTINGS, settings);

                        // Starting Activity
                        startActivityForResult(intent, MY_REQUEST_CODE);
                        break;
                    case 2 :
                        myIntent = new Intent(MainActivity.this, ScanFinger.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                    case 3 :
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                Log.i(TAG, "IOException");
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                        break;
                    case 4 :
                        //myIntent = new Intent(MainActivity.this, ARForm.class);
                        myIntent = new Intent(MainActivity.this, AccidentReportChoices.class);
                        MainActivity.this.startActivity(myIntent);
                        break;

                    case 5 :
                        //myIntent = new Intent(MainActivity.this, ARForm.class);
                        myIntent = new Intent(MainActivity.this, AccidentReports.class);
                        MainActivity.this.startActivity(myIntent);
                        break;
                }
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
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
            /*byte[] buf = Base64.decode(base64String,0);

            BitmapFactory factory = new BitmapFactory();
            Bitmap bitmap = factory.decodeByteArray(buf, 0, buf.length);
            img.setImageBitmap(bitmap);*/


            Intent intent = new Intent(this, Ticket.class);
            intent.setType("text/plain");
            intent.putExtra("results", stringData);
            startActivity(intent);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            new SendEmailAsyncTask(getApplicationContext()).execute();


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

    class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        Mail m = new Mail("13natty@gmail.com", "awandenkosi");

        public SendEmailAsyncTask(Context context) {
            mContext = context;
            if (BuildConfig.DEBUG)
                Log.v(SendEmailAsyncTask.class.getName(), "SendEmailAsyncTask()");

            String[] toArr = {"13natty@gmail.com"};
            m.setTo(toArr);
            m.setFrom("trafficOfficer@saps.co.za");
            m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device.");
            m.setBody("please find attached");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
                Log.d(TAG,"******************** emailed");
                Toast.makeText(mContext, "Email was sent successfully.", Toast.LENGTH_LONG).show();
            }else{
                Log.d(TAG,"******************** failed emailed");
                Toast.makeText(mContext, "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (BuildConfig.DEBUG) Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
            try {

                try {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.addAttachment(Uri.parse(mCurrentPhotoPath).getPath());


                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "There was a problem sending the email." + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }

                if (m.send()) {
                    return true;
                } else {
                    return false;
                }
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "failed, exception "+e.getMessage());
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
