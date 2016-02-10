package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.ProgModDelAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.FileObserverParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProgramHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class ProgramModDelFrag extends Fragment {

    private RecyclerView mProgramsRecycler;
    private ProgModDelAdapter mProgramsAdapter;

    private static HashMap<String, DipProgram> pathToProgramHash;
    private static final String PROGRAM_HASH = "PROGRAM_HASH";

    private FileObserver mProfileObserver;
    private static final String PROFILE_OBSERVER = "PROFILE_OBSERVER";

    private FileObserver mProgramObserver;
    private static final String PROGRAM_OBSERVER = "PROGRAM_OBSERVER";


    public ProgramModDelFrag() {
        // Required empty public constructor
    }

    public static ProgramModDelFrag newInstance(Parcelable inProgramHash, Parcelable inProfileObserver, Parcelable inProgramObserver) {
        ProgramModDelFrag fragment = new ProgramModDelFrag();
        Bundle args = new Bundle();
        args.putParcelable(PROGRAM_HASH, inProgramHash);
        args.putParcelable(PROFILE_OBSERVER, inProfileObserver);
        args.putParcelable(PROGRAM_OBSERVER, inProgramObserver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ProgramHashParcel programParcel = getArguments().getParcelable(PROGRAM_HASH);
            FileObserverParcel profileObserverParcel = getArguments().getParcelable(PROFILE_OBSERVER);
            FileObserverParcel programObserverParcel = getArguments().getParcelable(PROGRAM_OBSERVER);
            pathToProgramHash = programParcel.getHash();
            mProfileObserver = profileObserverParcel.getFileObserver();
            mProgramObserver = programObserverParcel.getFileObserver();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View totalView = inflater.inflate(R.layout.fragment_program_mod_del, container, false);

        mProgramsRecycler = (RecyclerView) totalView.findViewById(R.id.saved_programs_recycler);
        mProgramsRecycler.setHasFixedSize(true);
        mProgramsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgramsAdapter = new ProgModDelAdapter();
        mProgramsRecycler.setAdapter(mProgramsAdapter);

        populateFromHash();
        return totalView;
    }

    public void onButtonPressed(Uri uri) {
        // Do Nothing
    }

    private void populateFromHash() {
        if (mProgramsAdapter != null) {
            mProgramsAdapter.clear();
            for(DipProgram dipProg : pathToProgramHash.values()){
                mProgramsAdapter.addProgram(dipProg);
            }
        }
    }

    public void displaySaveDialog(){
        final DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_menu_save);
                builder.setTitle(getResources().getString(R.string.save_new_program));
                View view = getActivity().getLayoutInflater().inflate(R.layout.save_program_dialog, null, false);
                final EditText titleBox = (EditText)view.findViewById(R.id.program_title_box);
                final EditText descBox = (EditText)view.findViewById(R.id.program_desc_box);
                final Button saveButton = (Button)view.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                builder.setView(view);

                return builder.create();
            }
        };
        df.show(getFragmentManager(), "");
    }

    public void writeToCSVFile(File file){
        file.setWritable(true);
        MediaScannerConnection.scanFile(getActivity(), new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i("EXTERNAL STORAGE", "SCANNED");
            }
        });
        final String filename = file.toString();
        CharSequence contentTitle = getString(R.string.app_name);
        final List<String> strList = new ArrayList<String>();
        final ProgressDialog progDialog = ProgressDialog.show(
                getActivity(), contentTitle, "Please Wait.",
                true);//please wait
        final DipProgram newProgram = new DipProgram();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(strList.size() < 2){
                    Log.d("INVALID_CSV_FOR_PROGRAM", "CSV has less than two entries (no title / description)");
                    return;
                };
                if(strList.size()%2 != 0){
                    Log.d("INVALID_CSV_FOR_PROGRAM", "CSV has an odd number of entries (there exists an unequal pair)");
                }
                newProgram.setTitle(strList.get(0));
                newProgram.setDescription(strList.get(1));
                newProgram.setPath(filename);
                pathToProgramHash.put(filename, newProgram);
            }
        };
        new Thread(){
            public void run(){
                try {
                    CSVWriter writer = new CSVWriter(new FileWriter("yourfile.csv"), '\t');
                    // feed in your array (or convert your data to an array)
                    String[] entries = "first#second#third".split("#");
//                    while (true) {
//                        writer.writeNext(entries);
//                    }
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
                progDialog.dismiss();
            }
        }.start();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnAdminProfileChosenListener) {
//            mListener = (OnAdminProfileChosenListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    public interface OnProgramModDelChosenListener {
        void onProgramModDelChosen(Uri uri);
    }
}
