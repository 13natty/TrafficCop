package com.nattysoft.trafficcop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Infringement extends AppCompatActivity {

    private static final String TAG = Infringement.class.getSimpleName();
    // will show the statuses like bluetooth open, close or data sent
    TextView myLabel;

    // will enable user to enter any text to be printed
    EditText myTextbox;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;

    Button printButton;
    private boolean qsPrinterFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infringement);

        new findBlueToothPrinterAsyncTask(getApplicationContext()).execute();

        printButton = (Button) findViewById(R.id.button_print);
        printButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        new findBlueToothPrinterAsyncTask(getApplicationContext()).execute();
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {

            EditText charge = (EditText)findViewById(R.id.charge_field);
            EditText legalRef = (EditText)findViewById(R.id.legal_ref_field);
            EditText vehicleLicence = (EditText)findViewById(R.id.vehicle_lic_number_field);
            EditText description = (EditText)findViewById(R.id.infringement_desc_field);
            EditText points = (EditText)findViewById(R.id.points_field);
            EditText penaltyFee = (EditText)findViewById(R.id.penalty_amt_field);
            EditText discount = (EditText)findViewById(R.id.discount_amt_field);
            EditText discounted = (EditText)findViewById(R.id.discounted_amt_field);
            // the text typed by the user
            String msg = "Traffic fine.\n\n\n"+
                    "Main carge code :"+charge.getText().toString()+"\n\n"+
                    "Legal Ref :"+legalRef.getText().toString()+"\n\n"+
                    "Vehicle licence number :"+vehicleLicence.getText().toString()+"\n\n"+
                    "Description :"+description.getText().toString()+"\n\n"+
                    "Taken points :"+points.getText().toString()+"\n\n"+
                    "Penalty amount :R"+penaltyFee.getText().toString()+"\n\n"+
                    "Discount amount :R"+discount.getText().toString()+"\n\n"+
                    "Discounted amount :R"+discounted.getText().toString()+"\n\n\n\n\n\n";


            mmOutputStream.write(msg.getBytes());

            // tell the user data were sent
            myLabel.setText("Data sent.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
                printButton.setText("No bluetooth adapter available");
                printButton.setEnabled(false);

            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                qsPrinterFound = false;
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("Qsprinter")) {
                        qsPrinterFound = true;
                        mmDevice = device;

                        break;
                    }
                }
                if (!qsPrinterFound){
                    pairDevice();
                }
                return;
            }else{
                pairDevice();
            }

            myLabel.setText("Bluetooth device found.");
            printButton.setText("Bluetooth device found.");
            printButton.setEnabled(false);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void pairDevice() {
        new AlertDialog.Builder(this)
                .setTitle("Pair QS Printer")
                .setMessage("Paired QS Printer device not found, please pair bluetooth printer or continue.")
                .setPositiveButton("Pair", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // open settings
                        dialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Bluetooth Opened");
            printButton.setText("Print");
            printButton.setEnabled(true);

        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(), "Printer not available", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }

    /*
 * after opening a connection to bluetooth printer device,
 * we have to listen and check if a data were sent to be printed.
 */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class findBlueToothPrinterAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        public findBlueToothPrinterAsyncTask(Context context) {
            mContext = context;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
                Log.d(TAG, "******************** found printer");
                Toast.makeText(mContext, "Printer found", Toast.LENGTH_LONG).show();
            }else{
                Log.d(TAG,"******************** No printer found");
                Toast.makeText(mContext, "No Printer found.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                findBT();
                openBT();
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}
