package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.CallbackActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.MainActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.ProgModDelAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.ProgramEditFragment;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class ProgramModDelFrag extends Fragment implements ProgramEditFragment {

    private RecyclerView mProgramsRecycler;
    private ProgModDelAdapter mProgramsAdapter;

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
        if (getArguments() != null) {
        }
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
}
