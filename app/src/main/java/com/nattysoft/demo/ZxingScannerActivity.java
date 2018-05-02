package com.nattysoft.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.nattysoft.demo.MainActivity.ZXING_OK;

public class ZxingScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    static final Integer CAMERA = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing_scanner);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
        askForPermission(Manifest.permission.CAMERA, CAMERA);
    }

    private void askForPermission(String permission, Integer requestCode) {

        if (ContextCompat.checkSelfPermission(ZxingScannerActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ZxingScannerActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ZxingScannerActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ZxingScannerActivity.this, new String[]{permission}, requestCode);
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

        Toast.makeText(this, "Contents = " + result.getText() +
                ", Format = " + result.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();

        // * Wait 2 seconds to resume the preview.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(ZxingScannerActivity.this);
//            }
//        }, 3000);
        //Call back data to main activity
        Intent intent = getIntent();
        intent.putExtra(MainActivity.FORMAT, result.getBarcodeFormat().name());
        intent.putExtra(MainActivity.CONTENT, result.getText());

        setResult(ZXING_OK, intent);
        finish();

    }
}