package com.nattysoft.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static com.nattysoft.demo.MainActivity.ZXING_OK;


public class MyScanActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    static final Integer CAMERA = 0x1;
    private String TAG = MyScanActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zbar_scanner);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        formats.add(BarcodeFormat.PDF417);
        mScannerView.setFormats(formats);
        contentFrame.addView(mScannerView);
        askForPermission(Manifest.permission.CAMERA, CAMERA);

    }

    private void askForPermission(String permission, Integer requestCode) {

        if (ContextCompat.checkSelfPermission(MyScanActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MyScanActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MyScanActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MyScanActivity.this, new String[]{permission}, requestCode);
            }
        } else {

            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                //Camera
                case 1:
                    mScannerView.setResultHandler(this);
                    mScannerView.startCamera();
                    break;


            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        Toast.makeText(this, "Contents = " + result.getContents() +
                ", Format = " + result.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(MyScanActivity.this);
            }
        }, 2000);

        Log.d(TAG,"-------------------------->  "+result);
        //Call back data to main activity
        Intent intent = new Intent();
        intent.putExtra(MainActivity.FORMAT, result.getBarcodeFormat().getName());
        intent.putExtra(MainActivity.CONTENT, result.getContents());

        setResult(ZXING_OK, intent);
        finish();
    }

}
