package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.CallbackActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.MainActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.ProgModDelAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.ProgramEditFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class ProgramModDelFrag extends Fragment implements ProgramEditFragment {

    private RecyclerView mProgramsRecycler;
    private ProgModDelAdapter mProgramsAdapter;

    private DipProgram choppingBlockProg;
    private int choppingBlockPos;

    private static Map<String, DipProgram> pathToProgramHash;

    private CallbackActivity mCallback;

    public ProgramModDelFrag() {
        // Required empty public constructor
    }

    public static ProgramModDelFrag newInstance() {
        ProgramModDelFrag fragment = new ProgramModDelFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonProgramHash = sharedPreferences.getString(MainActivity.PROGRAM_HASH, "");
        Gson gson = new Gson();
        Type progHashType = new TypeToken<Map<String, DipProgram>>(){}.getType();
        pathToProgramHash = gson.fromJson(jsonProgramHash, progHashType);
    }

    @Override
    public void onResume(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonProgramHash = sharedPreferences.getString(MainActivity.PROGRAM_HASH, "");
        Gson gson = new Gson();
        Type progHashType = new TypeToken<Map<String, DipProgram>>(){}.getType();
        pathToProgramHash = gson.fromJson(jsonProgramHash, progHashType);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View totalView = inflater.inflate(R.layout.fragment_program_mod_del, container, false);

        mProgramsRecycler = (RecyclerView) totalView.findViewById(R.id.saved_programs_recycler);
        mProgramsRecycler.setHasFixedSize(true);
        mProgramsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mProgramsAdapter = new ProgModDelAdapter(this);
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
                builder.setIcon(android.R.drawable.ic_menu_delete);
                builder.setTitle("Are You Sure You Want To Delete: " + choppingBlockProg.getTitle());
                View view = getActivity().getLayoutInflater().inflate(R.layout.delete_program_dialog, null, false);
                TextView titleText = (TextView)view.findViewById(R.id.program_title_delete_dialog);
                titleText.setText(choppingBlockProg.getTitle());
                TextView descText = (TextView)view.findViewById(R.id.program_desc_delete_dialog);
                descText.setText(choppingBlockProg.getDescription());
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nameKey = choppingBlockProg.getTitle();
                        pathToProgramHash.remove(nameKey);
                        mProgramsAdapter.removeProgram(choppingBlockPos);
                        File specificFile = new File(getContext().getExternalFilesDir(null) + CONSTANTS.PROGRAMS_PATH_MAIN + "/" + choppingBlockProg.getTitle() + ".csv");
                        String specificFilePath = specificFile.getPath();
                        if(specificFile.exists()) {
                            specificFile.setWritable(true);
                            specificFile.setReadable(true);
                            specificFile.delete();
                            SharedPreferences.Editor prefsEditor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            Gson gson = new Gson();
                            String pathToProgramHashJson = gson.toJson(pathToProgramHash);
                            prefsEditor.putString(MainActivity.PROGRAM_HASH, pathToProgramHashJson);
                            prefsEditor.apply();
                            Toast.makeText(getContext(), "Program successfully deleted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setView(view);

                return builder.create();
            }
        };
        df.show(getFragmentManager(), "");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackActivity) {
            mCallback = (CallbackActivity) context;
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

    @Override
    public void switchToEditing(DipProgram dipProgram) {
        mCallback.switchToProgramEdit(dipProgram);
    }

    @Override
    public void onProgramDeleted(DipProgram dipProgram, int positionInAdapter) {
        choppingBlockPos = positionInAdapter;
        choppingBlockProg = dipProgram;
        displaySaveDialog();
    }
}
