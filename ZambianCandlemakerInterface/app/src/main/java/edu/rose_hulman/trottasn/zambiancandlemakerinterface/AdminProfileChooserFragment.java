package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminProfileChooserFragment extends Fragment implements AvailableProfilesAdapter.ProfileChooserFragmentHelper {

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private OnAdminProfileChosenListener mListener;

    public AdminProfileChooserFragment() {
        // Required empty public constructor
    }

    public static AdminProfileChooserFragment newInstance() {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
        Bundle args = new Bundle();
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
        View totalView = inflater.inflate(R.layout.fragment_admin_profile_chooser, container, false);

        mAvailableRecycler = (RecyclerView) totalView.findViewById(R.id.remaining_choices_recycler);
        mSelectedRecycler = (RecyclerView) totalView.findViewById(R.id.selected_recycler);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAvailableRecycler.setLayoutManager(layoutManager);

        final LinearLayoutManager layoutManagerSelected = new LinearLayoutManager(getContext());
        layoutManagerSelected.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectedRecycler.setLayoutManager(layoutManagerSelected);

        mAvailableAdapter = new AvailableProfilesAdapter(this);
        mAvailableRecycler.setAdapter(mAvailableAdapter);
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/CSVs");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (file.isDirectory()) {
                    Log.d("NO_DIRECTORIES_ALLOWED", "There should not be a directory in this folder");
                } else {
                    readInCSVFile(file);
                }
            }
        }
        return totalView;
    }

    public void readInCSVFile(File file){
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
        final DipProfile newProfile = new DipProfile();
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
                newProfile.setTitle(strList.get(0));
                newProfile.setDescription(strList.get(1));
                for(int i = 2; i < strList.size(); i+=2){
                    TimePosPair newPair = new TimePosPair(Integer.parseInt(strList.get(i)), Integer.parseInt(strList.get(i+1)));
                    newProfile.addPair(newPair);
                }
                mAvailableAdapter.addProfile(newProfile);
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileChosen(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAdminProfileChosenListener) {
            mListener = (OnAdminProfileChosenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void returnToTop() {
        mAvailableRecycler.scrollToPosition(0);
    }

    @Override
    public void slideToPosition(int position) {
        mAvailableRecycler.scrollToPosition(position);
    }

    public interface OnAdminProfileChosenListener {
        void onProfileChosen(Uri uri);
    }
}
