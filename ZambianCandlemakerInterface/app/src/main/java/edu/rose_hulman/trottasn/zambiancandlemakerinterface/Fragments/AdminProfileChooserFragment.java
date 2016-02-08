package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.HashMap;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.AvailableProfilesAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.SelectedProfilesAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.CONSTANTS;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.FileObserverParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.HashMapParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class AdminProfileChooserFragment extends Fragment implements ProfileHashFragment, AvailableProfilesAdapter.ProfileChooserFragmentHelper, SelectedProfilesAdapter.ProfileSelectedHelper {

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private SelectedProfilesAdapter mSelectedAdapter;
    private Button mOptionsButton;
    private Button mSaveButton;

    private HashMap<String, String> mFieldValuePairs;

    private boolean mSavePreparedness;

    private OnAdminProfileChosenListener mListener;

    private static HashMap<String, DipProfile> pathToProfileHash;
    private static final String HASH = "hash";

    private FileObserver mFileObserver;
    private static final String OBSERVER = "observer";

    public static final int MAX_TIME_DELAY = 60;


    public AdminProfileChooserFragment() {
        // Required empty public constructor
    }

    public static AdminProfileChooserFragment newInstance(Parcelable inHash, Parcelable inObserver) {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
        Bundle args = new Bundle();
        args.putParcelable(HASH, inHash);
        args.putParcelable(OBSERVER, inObserver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFieldValuePairs = new HashMap<String, String>();
        if (getArguments() != null) {
            HashMapParcel profileParcel = getArguments().getParcelable(HASH);
            FileObserverParcel observerParcel = getArguments().getParcelable(OBSERVER);
            pathToProfileHash = profileParcel.getHash();
            mFileObserver = observerParcel.getFileObserver();
        }
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
            }
        });
        populateFromHash();
        return totalView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileChosen(uri);
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

                String popTimeDelay = fieldMap.get(CONSTANTS.TIME_DELAY_KEY);
                if(popTimeDelay != null && !popTimeDelay.equals("")) {
                    timeDelayBox.setSelection(time_delay_array.getPosition(String.valueOf(popTimeDelay)));
                }
                final EditText maxAccelVertBox = (EditText) view.findViewById(R.id.vert_accel_box);
                String popMaxAccelVert = fieldMap.get(CONSTANTS.MAX_ACCEL_VERT_KEY);
                if(popMaxAccelVert != null && !popMaxAccelVert.equals("")) {
                    maxAccelVertBox.setText(String.valueOf(popMaxAccelVert));
                }
                final EditText maxAccelRotBox = (EditText) view.findViewById(R.id.max_accel_rot_box);
                String popMaxAccelRot = fieldMap.get(CONSTANTS.MAX_ACCEL_ROT_KEY);
                if(popMaxAccelRot != null && !popMaxAccelRot.equals("")) {
                    maxAccelRotBox.setText(String.valueOf(popMaxAccelRot));
                }
                final EditText maxVelVertBox = (EditText) view.findViewById(R.id.def_jog_vel_vert_box);
                String popMaxVelVert = fieldMap.get(CONSTANTS.MAX_VEL_VERT_KEY);
                if(popMaxVelVert != null && !popMaxVelVert.equals("")){
                    maxVelVertBox.setText(String.valueOf(popMaxVelVert));
                }
                final EditText maxVelRotBox = (EditText) view.findViewById(R.id.def_jog_vel_rot_box);
                String popMaxVelRot = fieldMap.get(CONSTANTS.MAX_VEL_ROT_KEY);
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
                        redoHash.put(CONSTANTS.TIME_DELAY_KEY, timeDelaySelection);
                        redoHash.put(CONSTANTS.MAX_ACCEL_VERT_KEY, maxAccelVert);
                        redoHash.put(CONSTANTS.MAX_ACCEL_ROT_KEY, maxAccelRot);
                        redoHash.put(CONSTANTS.MAX_VEL_VERT_KEY, maxVelVert);
                        redoHash.put(CONSTANTS.MAX_VEL_ROT_KEY, maxVelRot);

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

    @Override
    public void setNewHash(HashMap<String, DipProfile> newHash) {
        pathToProfileHash = newHash;
        populateFromHash();
    }

    public interface OnAdminProfileChosenListener {
        void onProfileChosen(Uri uri);
    }
}
