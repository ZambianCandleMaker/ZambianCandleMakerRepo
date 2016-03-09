package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

/**
 * Created by TrottaSN on 3/9/2016.
 */
public class MessagingUtility {
    public static void sendMessageAndArchive(String message, Activity sendingActivity){
        Log.d("SENT", message);
        File finalFile = new File(sendingActivity.getFilesDir().getAbsolutePath(), "archived_messages.txt");
        finalFile.setWritable(true);
        final String filename = finalFile.toString();
        final ProgressDialog progDialog = ProgressDialog.show(
                sendingActivity, sendingActivity.getResources().getString(R.string.waiting_message_sending), "Please Wait.",
                true);//please wait
        MediaScannerConnection.scanFile(sendingActivity, new String[]{filename}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        try {
            PrintWriter newWriter = new PrintWriter(new FileWriter(finalFile, true));
            newWriter.append(message + "\n");
            newWriter.close();

//            FileReader fileReader = new FileReader(filename);
//
//            // Always wrap FileReader in BufferedReader.
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            String line = "";
//            Log.d("PMT", "Previous Messages Follow: ");
//            while((line = bufferedReader.readLine()) != null) {
//                Log.d("PMT", line);
//            }
//
//            // Always close files.
//            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progDialog.dismiss();
    }
    public static void sendMessage(String message){
        Log.d("SENT", message);
    }
}
