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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.MainActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.AvailableProfilesAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.SelectedProfilesAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.CSVUtility;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class AdminProfileChooserFragment extends Fragment implements AvailableProfilesAdapter.ProfileChooserFragmentHelper, SelectedProfilesAdapter.ProfileSelectedHelper {

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private SelectedProfilesAdapter mSelectedAdapter;
    private Button mOptionsButton;
    private Button mSaveButton;

    private DipProgram startingProgram;
    private boolean startFromProgram;

    private Map<String, String> mFieldValuePairs;

    private boolean mSavePreparedness;

    private OnProgramSavedListener mListener;

    private static final String STARTING_PROGRAM = "STARTING_PROGRAM";

    private static Map<String, DipProfile> pathToProfileHash;
    private static Map<String, DipProgram> pathToProgramHash;

    public static final int MAX_TIME_DELAY = 60;


    public AdminProfileChooserFragment() {
        // Required empty public constructor
    }

    public static AdminProfileChooserFragment newInstance(DipProgram dipProgram) {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
        Bundle args = new Bundle();
        if(dipProgram != null){
            Log.d("BREAK", "GIVE ME ONE");
            args.putParcelable(STARTING_PROGRAM, dipProgram);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFieldValuePairs = new HashMap<>();
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences != null){
            String profileHashString = sharedPreferences.getString(MainActivity.PROFILE_HASH, "");
            String programHashString = sharedPreferences.getString(MainActivity.PROGRAM_HASH, "");
            Type hashType = new TypeToken<HashMap<String ,DipProfile>>(){}.getType();
            Type progHashType = new TypeToken<HashMap<String, DipProgram>>(){}.getType();
            pathToProfileHash = gson.fromJson(profileHashString, hashType);
            pathToProgramHash = gson.fromJson(programHashString, progHashType);
        }
        Bundle args = getArguments();
        if(args != null){
            Log.d("BREAK", "GIVE ME 2");
            startingProgram = args.getParcelable(STARTING_PROGRAM);
            if(startingProgram != null){
                startFromProgram = true;
            }
        }
    }

    @Override
    public void onResume(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(sharedPreferences != null){
            String profileHashString = sharedPreferences.getString(MainActivity.PROFILE_HASH, "");
            String programHashString = sharedPreferences.getString(MainActivity.PROGRAM_HASH, "");
            Type hashType = new TypeToken<HashMap<String ,DipProfile>>(){}.getType();
            Type progHashType = new TypeToken<HashMap<String, DipProgram>>(){}.getType();
            pathToProfileHash = gson.fromJson(profileHashString, hashType);
            pathToProgramHash = gson.fromJson(programHashString, progHashType);
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View totalView = inflater.inflate(R.layout.fragment_admin_profile_chooser, container, false);

        mAvailableRecycler = (RecyclerView) totalView.findViewById(R.id.remaining_choices_recycler);
        mSelectedRecycler = (RecyclerView) totalView.findViewById(R.id.selected_recycler);
        mAvailableRecycler.setHasFixedSize(true);
        mAvailableRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSelectedRecycler.setHasFixedSize(true);
        mSelectedRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAvailableAdapter = new AvailableProfilesAdapter(this);
        mAvailableRecycler.setAdapter(mAvailableAdapter);

        mSelectedAdapter = new SelectedProfilesAdapter(this);
        mSelectedRecycler.setAdapter(mSelectedAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleTouchHelperCallback(mSelectedAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mSelectedRecycler);

        mOptionsButton = (Button)totalView.findViewById(R.id.options_button);
        mOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> tempHash = new HashMap<String, String>();
                tempHash.putAll(mFieldValuePairs);
                displayOptionsDialog(false, tempHash);
            }
        });

        mSaveButton = (Button)totalView.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open New Dialog - Should Only be Available After Options Verified
                displaySaveDialog();
            }
        });
        if(mSavePreparedness){
            mSaveButton.setVisibility(View.VISIBLE);
        }
        else{
            mSaveButton.setVisibility(View.GONE);
        };
        populateFromHash();
        if(startFromProgram){
            populateStartingData(startingProgram);
        }
        return totalView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    private void populateFromHash() {
        if (mAvailableAdapter != null) {
            mAvailableAdapter.clear();
            for(DipProfile dipProf : pathToProfileHash.values()){
                mAvailableAdapter.addProfile(dipProf);
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
                String possTitleText = mFieldValuePairs.get(CSVUtility.PROGRAM_TITLE_KEY);
                if(possTitleText != null && possTitleText != ""){
                    titleBox.setText(possTitleText);
                }
                final EditText descBox = (EditText)view.findViewById(R.id.program_desc_box);
                String possDescriptionText = mFieldValuePairs.get(CSVUtility.PROGRAM_DESCRIPTION_KEY);
                if(possDescriptionText != null && possDescriptionText != ""){
                    descBox.setText(possDescriptionText);
                }
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Need to check for invalidity first
                        String titleGet = titleBox.getText().toString();
                        String descGet = descBox.getText().toString();
                        if(titleGet == null || titleGet.equals("")){
                            Toast.makeText(getContext(), R.string.please_enter_a_title, Toast.LENGTH_SHORT).show();
                            mFieldValuePairs.put(CSVUtility.PROGRAM_TITLE_KEY, "");
                            displaySaveDialog();
                            return;
                        }
                        if(descGet == null && descGet.equals("")){
                            Toast.makeText(getContext(), R.string.please_enter_a_description, Toast.LENGTH_SHORT).show();
                            mFieldValuePairs.put(CSVUtility.PROGRAM_DESCRIPTION_KEY, "");
                            displaySaveDialog();
                            return;
                        }
                        mFieldValuePairs.put(CSVUtility.PROGRAM_TITLE_KEY, titleBox.getText().toString());
                        mFieldValuePairs.put(CSVUtility.PROGRAM_DESCRIPTION_KEY, descBox.getText().toString());
                        List<String> selectedProfiles = mSelectedAdapter.getTitleList();
                        List<DipProfile> dipProfs = new ArrayList<>();
                        for(String name : selectedProfiles) {
                            DipProfile dipProf = pathToProfileHash.get(name);
                            if(dipProf == null){
                                Toast.makeText(getContext(), "Could Not Save Program - Data Out of Sync. Sorry for the inconvenience. Please restart the Application.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            dipProfs.add(pathToProfileHash.get(name));
                        }
                        Toast.makeText(getContext(), "Saving Program to CSV file and in Cache", Toast.LENGTH_SHORT).show();
                        DipProgram newProgram = new DipProgram();
                        newProgram.assignFromReading(mFieldValuePairs, dipProfs);
                        pathToProgramHash.put(mFieldValuePairs.get(CSVUtility.PROGRAM_TITLE_KEY), newProgram);
                        CSVUtility.writeProgramCSV(mFieldValuePairs, selectedProfiles, getActivity(), getContext().getFilesDir().getPath());
                        SharedPreferences.Editor prefsEditor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        Gson gson = new Gson();
                        String pathToProfileHashJson = gson.toJson(pathToProfileHash);
                        String pathToProgramHashJson = gson.toJson(pathToProgramHash);
                        prefsEditor.putString(MainActivity.PROFILE_HASH, pathToProfileHashJson);
                        prefsEditor.putString(MainActivity.PROGRAM_HASH, pathToProgramHashJson);
                        prefsEditor.apply();
                    }
                });

                builder.setView(view);

                return builder.create();
            }
        };
        df.show(getFragmentManager(), "");
    }

    public void displayOptionsDialog(final boolean fromInvalidOptions, final HashMap<String, String> fieldMap){
        final DialogFragment df = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle(getResources().getString(R.string.general_program_settings_prompt));
                if(fromInvalidOptions){
                    Toast.makeText(getContext(),Html.fromHtml("<font color='#FF0000'>" + "\nMUST FILL IN ALL FIELDS TO SAVE PROGRAM" + "</font>"), Toast.LENGTH_SHORT).show();
                }
                View view = getActivity().getLayoutInflater().inflate(R.layout.program_options_admin, null, false);
                final Spinner timeDelayBox = (Spinner) view.findViewById(R.id.time_delay_spinner);
                ArrayAdapter<CharSequence> time_delay_array = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
                for (int i = 0; i < MAX_TIME_DELAY; i++) {
                    time_delay_array.add(String.valueOf(i));
                }
                time_delay_array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeDelayBox.setAdapter(time_delay_array);

                // BELOW : STRATEGY PATTERN FOR THESE? THERE'S GOT TO BE A WAY TO CONDENSE
                String popTimeDelay = fieldMap.get(CSVUtility.PROGRAM_TIME_DELAY_KEY);
                if(popTimeDelay != null && !popTimeDelay.equals("")) {
                    timeDelayBox.setSelection(time_delay_array.getPosition(String.valueOf(popTimeDelay)));
                }
                final EditText maxAccelVertBox = (EditText) view.findViewById(R.id.vert_accel_box);
                String popMaxAccelVert = fieldMap.get(CSVUtility.PROGRAM_MAX_AV_KEY);
                if(popMaxAccelVert != null && !popMaxAccelVert.equals("")) {
                    maxAccelVertBox.setText(String.valueOf(popMaxAccelVert));
                }
                final EditText maxAccelRotBox = (EditText) view.findViewById(R.id.max_accel_rot_box);
                String popMaxAccelRot = fieldMap.get(CSVUtility.PROGRAM_MAX_AR_KEY);
                if(popMaxAccelRot != null && !popMaxAccelRot.equals("")) {
                    maxAccelRotBox.setText(String.valueOf(popMaxAccelRot));
                }
                final EditText maxVelVertBox = (EditText) view.findViewById(R.id.def_jog_vel_vert_box);
                String popMaxVelVert = fieldMap.get(CSVUtility.PROGRAM_MAX_VV_KEY);
                if(popMaxVelVert != null && !popMaxVelVert.equals("")){
                    maxVelVertBox.setText(String.valueOf(popMaxVelVert));
                }
                final EditText maxVelRotBox = (EditText) view.findViewById(R.id.def_jog_vel_rot_box);
                String popMaxVelRot = fieldMap.get(CSVUtility.PROGRAM_MAX_VR_KEY);
                if(popMaxVelRot != null && !popMaxVelRot.equals("")){
                    maxVelRotBox.setText(String.valueOf(popMaxVelRot));
                }

                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String timeDelaySelection = timeDelayBox.getSelectedItem().toString();
                        String maxAccelVert = maxAccelVertBox.getText().toString();
                        String maxAccelRot = maxAccelRotBox.getText().toString();
                        String maxVelVert = maxVelVertBox.getText().toString();
                        String maxVelRot = maxVelRotBox.getText().toString();

                        HashMap<String, String> redoHash = new HashMap<String, String>();
                        redoHash.put(CSVUtility.PROGRAM_TIME_DELAY_KEY, timeDelaySelection);
                        redoHash.put(CSVUtility.PROGRAM_MAX_AV_KEY, maxAccelVert);
                        redoHash.put(CSVUtility.PROGRAM_MAX_AR_KEY, maxAccelRot);
                        redoHash.put(CSVUtility.PROGRAM_MAX_VV_KEY, maxVelVert);
                        redoHash.put(CSVUtility.PROGRAM_MAX_VR_KEY, maxVelRot);

                        boolean proceed = true;

                        ///// CAN AND SHOULD BE EXTRACTED OUT - STRATEGY PATTERN? ////////
                        if (timeDelaySelection.equals("") || maxAccelVert.equals("") || maxAccelRot.equals("") || maxVelVert.equals("") || maxVelRot.equals("")){
                            mSavePreparedness = false;
                            proceed = false;
                        };
                        if (!proceed) {
                            displayOptionsDialog(true, redoHash);
                        }
                        else{
                            mFieldValuePairs.putAll(redoHash);
                            for(String key : mFieldValuePairs.keySet()){
                                Log.d("PFHV", key + " : " + mFieldValuePairs.get(key));
                            }
                            mSaveButton.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), "General Program Options Recorded - Save to Commit Changes to CSV", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(String key : mFieldValuePairs.keySet()){
                            Log.d("CFHV", key + " : " + mFieldValuePairs.get(key));
                        }
                        Toast.makeText(getContext(), "Options Will Remain With Previously Chosen Values", Toast.LENGTH_SHORT).show();
                    }
                });
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProgramSavedListener) {
            mListener = (OnProgramSavedListener) context;
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
    public void returnAvailableToTop() {
        mAvailableRecycler.scrollToPosition(0);
    }

    @Override
    public void slideAvailableToPosition(int position) {
        mAvailableRecycler.scrollToPosition(position);
    }

    @Override
    public void addToSelectedAdapter(DipProfile dipProfile) {
        mSelectedAdapter.addProfile(dipProfile);
    }

    @Override
    public void returnSelectedToTop() {
        mSelectedRecycler.scrollToPosition(0);
    }

    @Override
    public void slideSelectedToPosition(int position) {
        mSelectedRecycler.scrollToPosition(position);
    }

    @Override
    public void addToAvailableAdapter(DipProfile dipProfile) {
        mAvailableAdapter.addProfile(dipProfile);
    }

    public interface OnProgramSavedListener {
        void onProgramSaved();
    }

    public void populateStartingData(DipProgram dipProgram){
        mFieldValuePairs = dipProgram.getFieldHash();
        List<DipProfile> prevSelectedProfs = dipProgram.getProfileList();
        mSelectedAdapter.setSelectedProfiles(prevSelectedProfs);
        mAvailableAdapter.subtractProfiles(prevSelectedProfs);
    }
}
