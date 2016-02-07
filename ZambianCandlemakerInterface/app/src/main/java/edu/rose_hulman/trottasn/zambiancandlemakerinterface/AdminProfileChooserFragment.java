package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.app.Dialog;
import android.content.ClipData;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminProfileChooserFragment extends Fragment implements ProfileHashFragment, AvailableProfilesAdapter.ProfileChooserFragmentHelper, SelectedProfilesAdapter.ProfileSelectedHelper {

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private SelectedProfilesAdapter mSelectedAdapter;
    private Button mOptionsButton;
    private OnAdminProfileChosenListener mListener;
    private static HashMap<String, DipProfile> pathToProfileHash;
    private static final String HASH = "hash";
    private static final String OBSERVER = "observer";
    private FileObserver mFileObserver;
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
                displayOptionsDialog();
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

    public void displayOptionsDialog(){
        final DialogFragment df = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle(getResources().getString(R.string.general_program_settings_prompt));
                View view = getActivity().getLayoutInflater().inflate(R.layout.program_options_admin, null, false);
                final Spinner timeDelayBox = (Spinner) view.findViewById(R.id.time_delay_spinner);
                ArrayAdapter<CharSequence> time_delay_array = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
                for (int i = 0; i < MAX_TIME_DELAY; i++) {
                    time_delay_array.add(String.valueOf(i));
                }
                time_delay_array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                timeDelayBox.setAdapter(time_delay_array);
                final EditText maxAccelVertBox = (EditText) view.findViewById(R.id.vert_accel_box);
                final EditText maxAccelRotBox = (EditText) view.findViewById(R.id.max_accel_rot_box);
                final EditText maxVelVertBox = (EditText) view.findViewById(R.id.def_jog_vel_vert_box);
                final EditText maxVelRotBox = (EditText) view.findViewById(R.id.def_jog_vel_rot_box);
                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String timeDelaySelection = timeDelayBox.getSelectedItem().toString();
                        String maxAccelVert = maxAccelVertBox.getText().toString();
                        String maxAccelRot = maxAccelRotBox.getText().toString();
                        String maxVelVert = maxVelVertBox.getText().toString();
                        String maxVelRot = maxVelRotBox.getText().toString();
                        if(timeDelaySelection.equals("") || maxAccelVert.equals("") || maxAccelRot.equals("") || maxVelVert.equals("") || maxVelRot.equals("")){
                            Log.d("NOT_ENOUGH_INFORMATION", "Not enough info was given to the dialog fragment.");
                            return;
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
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
