package com.nattysoft.demo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Nkosinathi on 2016/08/13.
 */
public class Util {

    // Method for opening a pdf file
    public static void viewPdf(String file, String directory, Context context) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }
}
