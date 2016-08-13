package com.nattysoft.trafficcop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;


public class AccidentReportChoices extends ActionBarActivity {

    LinearLayout report;

    Button viewButton;
    Button sendButton;
    Button deleteButton;
    ProgressBar sendEmailProgress;
    private String TAG = AccidentReportChoices.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident_report_choices);

        report = (LinearLayout) findViewById(R.id.report_action);
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

        sendEmailProgress = (ProgressBar) findViewById(R.id.email_loading);

        viewButton = (Button)findViewById(R.id.view_report);
        viewButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Util.viewPdf("newFile.pdf", "Dir", AccidentReportChoices.this);
            }
        });

        sendButton = (Button)findViewById(R.id.send_report);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new SendPDFEmailAsyncTask(getApplicationContext()).execute();
            }
        });

        deleteButton = (Button)findViewById(R.id.delete_report);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // delete the original file
                new File(path + "/newFile.pdf").delete();
            }
        });

        File dir = new File(path);
        if(dir.exists()) {
            File file = new File(dir, "newFile.pdf");
            if(file.exists()){
                report.setVisibility(View.VISIBLE);
            }else{
                report.setVisibility(View.INVISIBLE);
            }
        }

        GridView gridview = (GridView) findViewById(R.id.choices_gridview);
        gridview.setAdapter(new ChoicesImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent myIntent;

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        myIntent = new Intent(AccidentReportChoices.this, ARForm.class);
                        AccidentReportChoices.this.startActivity(myIntent);
                        break;
                    case 1:
                        myIntent = new Intent(AccidentReportChoices.this, ARFormParticulars.class);
                        AccidentReportChoices.this.startActivity(myIntent);
                        break;
                }
            }
        });
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            File[] list = dir.listFiles();


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + "ar_report_"+list.length);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath + inputFile).delete();


        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        report = (LinearLayout) findViewById(R.id.report_action);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";
        File dir = new File(path);
        if(dir.exists()) {
            File file = new File(dir, "newFile.pdf");
            if(file.exists()){
                report.setVisibility(View.VISIBLE);
            }else{
                report.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accident_report_choices, menu);
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

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createandDisplayPdf(String text) {

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

            Paragraph p1 = new Paragraph(text);
            Font paraFont= new Font(Font.getFamily("COURIER"));
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            p1.setFont(paraFont);

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

        Util.viewPdf("newFile.pdf", "Dir", AccidentReportChoices.this);
    }

    class SendPDFEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext;
        Mail m = new Mail("13natty@gmail.com", "awandenkosi");

        public SendPDFEmailAsyncTask(Context context) {
            mContext = context;
            if (BuildConfig.DEBUG)
                Log.v(SendPDFEmailAsyncTask.class.getName(), "SendPDFEmailAsyncTask()");

            String[] toArr = {"13natty@gmail.com","garthzu@gmail.com"};
            m.setTo(toArr);
            m.setFrom("trafficReport@saps.co.za");
            m.setSubject("This is an email sent using my Mail JavaMail wrapper from an Android device.");
            m.setBody("please find attached pdf");
        }

        @Override public void onPreExecute() {
            sendEmailProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            sendEmailProgress.setVisibility(View.INVISIBLE);
            recreate();

            if(aBoolean){
                Log.d(TAG,"******************** emailed");
                Toast.makeText(mContext, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Dir/newFile.pdf");
                Uri path = Uri.fromFile(pdfFile);
                File fdelete = new File(path.getPath());
                if (fdelete.exists()) {

                    moveFile(Environment.getExternalStorageDirectory() + "/Dir/", "newFile.pdf", Environment.getExternalStorageDirectory() + "/Dir/sent_ar/");

                }
            }else{
                Log.d(TAG,"******************** failed emailed");
                Toast.makeText(mContext, "Email was not sent.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (BuildConfig.DEBUG) Log.v(SendPDFEmailAsyncTask.class.getName(), "doInBackground()");
            try {

                try {
                    File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Dir/newFile.pdf");
                    Uri path = Uri.fromFile(pdfFile);
                    m.addAttachment(path.getPath());


                } catch (Exception e) {
                    Toast.makeText(AccidentReportChoices.this, "There was a problem sending the email." + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("MailApp", "Could not send email", e);
                }

                if (m.send()) {
                    return true;
                } else {
                    return false;
                }
            } catch (AuthenticationFailedException e) {
                Log.e(SendPDFEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                return false;
            } catch (MessagingException e) {
                Log.e(SendPDFEmailAsyncTask.class.getName(), "failed, exception "+e.getMessage());
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
