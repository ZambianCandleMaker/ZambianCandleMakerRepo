package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdministratorProgramFragment extends Fragment {

    private OperatorFragment.Callback mCallback;
    private TextView testText;
    private EditText testEdit;
    private Button runSave;
    private Button runLoad;

    public AdministratorProgramFragment() {
        // Required empty public constructor
    }

    public static AdministratorProgramFragment newInstance() {
        AdministratorProgramFragment fragment = new AdministratorProgramFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.administrator_program_fragment, container, false);
        testText = (TextView) view.findViewById(R.id.test_text);
        testEdit = (EditText) view.findViewById(R.id.testEdit);
        runSave = (Button) view.findViewById(R.id.run_save_button);
        runSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToCSV();
            }
        });
        runLoad = (Button) view.findViewById(R.id.run_load_button);
        runLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFromCSV();
            }
        });
        return view;
    }

    private void writeToCSV(){
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/CSVs");
        boolean var = false;
        if(!folder.exists()){
            var = folder.mkdir();
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/CSVs" + "/" + "Test.csv");
        file.setWritable(true);
        final String filename = file.toString();
        MediaScannerConnection.scanFile(getContext(), new String[] {file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener(){
            public void onScanCompleted(String path, Uri uri){
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        CharSequence contentTitle = getString(R.string.app_name);
        final ProgressDialog progDailog = ProgressDialog.show(
                getContext(), contentTitle, "Please Wait.",
                true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        new Thread(){
            public void run(){
                try {
                    CSVWriter writer = new CSVWriter(new FileWriter(filename));
                    String[] entries = testEdit.getText().toString().split("#");
                    writer.writeNext(entries);
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
                progDailog.dismiss();
            }
        }.start();
    }

    public void loadFromCSV(){
        testText.setText("");
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/CSVs");
        boolean var = false;
        if(!folder.exists()){
            return;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/CSVs/Test.csv");
        if(!file.exists()){
            return;
        }
        file.setWritable(true);
        MediaScannerConnection.scanFile(getContext(), new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        final String filename = file.toString();
        CharSequence contentTitle = getString(R.string.app_name);
        final List<String> strList = new ArrayList<String>();
        final ProgressDialog progDailog = ProgressDialog.show(
                getContext(), contentTitle, "Please Wait.",
                true);//please wait
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                for(String str : strList){
                    testText.setText(testText.getText().toString() + "" + str);
                }
            }
        };
        new Thread(){
            public void run(){
                try {
                    CSVReader reader = new CSVReader(new FileReader(filename));
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        for(int i = 0; i < nextLine.length; i++){
                            strList.add(nextLine[i]);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
                progDailog.dismiss();
            }
        }.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OperatorFragment.Callback) {
            mCallback = (OperatorFragment.Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
