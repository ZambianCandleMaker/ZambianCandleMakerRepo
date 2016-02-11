package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.EditProfileAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.TimePosPair;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;


public class EditProfileFragment extends Fragment implements ProfileHashFragment {

    OperatorFragment.OperatorFragmentListener mCallback;

    private RecyclerView pointRecycler;
    private EditProfileAdapter mAdapter;
    private GraphView graph;
    private Viewport viewport;
    private LineGraphSeries<DataPoint> currentSeries;
    private SharedPreferences prefs;

    private static final String HASH = "hash";
    private static final String EDIT_PROFILE = "edit_profile";
    private static final String CURRENT_PROFILE = "current_profile";

    public static final String PREFS_NAME = "editorPrefs";

//    private static final String PROFILE = "profile";

    private static DipProfile currentProfile;
    private static DipProfile editProfile;

    private static HashMap<String, DipProfile> pathToProfileHash;


    public static EditProfileFragment newInstance(Parcelable inHash) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
//        args.putParcelable(PROFILE, profile);
        args.putParcelable(HASH,inHash);
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            prefs = this.getActivity().getSharedPreferences("profilePrefs", Context.MODE_PRIVATE);
            ProfileHashParcel profileHashParcel = (ProfileHashParcel) getArguments().getParcelable(HASH);
            pathToProfileHash = profileHashParcel.getHash();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String editJson = gson.toJson(editProfile);
        String currentJson = gson.toJson(currentProfile);

        prefsEditor.putString(EDIT_PROFILE, editJson);
        prefsEditor.putString(CURRENT_PROFILE, currentJson);
        prefsEditor.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        Gson gson = new Gson();
        String json;
        if(!prefs.getString(EDIT_PROFILE, "").equals(null)){
            json = prefs.getString(EDIT_PROFILE, "");
            editProfile = gson.fromJson(json, DipProfile.class);

            json = prefs.getString(CURRENT_PROFILE, "");
            currentProfile = gson.fromJson(json, DipProfile.class);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        onStart();
//        if(prefs.getString(HASH,"") != null){
//            currentProfile = pathToProfileHash.get(prefs.getString(HASH,""));
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        setHasOptionsMenu(true);

        pointRecycler = (RecyclerView) view.findViewById(R.id.profile_point_recycler);
        graph = (GraphView) view.findViewById(R.id.edit_graph);

        viewport = graph.getViewport();




        if(currentProfile == null) {
            currentProfile = new DipProfile();
            for(int i = 1; ; i++){
                String title = "New Profile "+ i;
                if(pathToProfileHash.get(title) == null) {
                    currentProfile.setTitle(title);
                    break;
                }

            }
            editProfile = new DipProfile(currentProfile);
        }
        mAdapter = new EditProfileAdapter(getContext(), editProfile);
        pointRecycler.setAdapter(mAdapter);
        pointRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pointRecycler.setHasFixedSize(true);

        setGraphMaxes(editProfile);
        graph.addSeries(editProfile.getLineGraphSeries());


        Button confirmationButton = (Button) view.findViewById(R.id.insert_point_button);
        final EditText timeText = (EditText) view.findViewById(R.id.edit_time_text);
        final EditText depthText = (EditText) view.findViewById(R.id.edit_depth_text);

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = editProfile.getPairList().size();
                int time = Integer.parseInt(timeText.getText().toString());
                int depth = Integer.parseInt(depthText.getText().toString());

                int pos = editProfile.addPair(new TimePosPair(depth,time));

                graph.removeAllSeries();
                setGraphMaxes(editProfile);
                graph.addSeries(editProfile.getLineGraphSeries());

                if(editProfile.getPairList().size() > size) mAdapter.notifyItemInserted(pos);
                else mAdapter.notifyItemChanged(pos);


            }
        });

        Button saveButton = (Button) view.findViewById(R.id.save_profile_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileDialog();
            }
        });

        Button resetButton = (Button) view.findViewById(R.id.reset_profile_button);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetProfile();
                graph.removeAllSeries();
                setGraphMaxes(editProfile);
                graph.addSeries(editProfile.getLineGraphSeries());

            }
        });



        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_choose_program);
        item.setVisible(false);

        MenuItem choose_profile_item = menu.findItem(R.id.action_choose_profile);
        choose_profile_item.setVisible(true);

        MenuItem reset_profile_item = menu.findItem(R.id.action_reset_profile);
        reset_profile_item.setVisible(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_choose_profile:
                selectProfileDialog();
                break;
            default:
        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OperatorFragment.OperatorFragmentListener) {
            mCallback = (OperatorFragment.OperatorFragmentListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    private void setGraphMaxes(DipProfile profile){
//        viewport.setYAxisBoundsManual(true);
//        viewport.setXAxisBoundsManual(true);
//        viewport.setYAxisBoundsStatus();
//        viewport.setMinX(0);
//        viewport.setMinY(0);
//        viewport.setMaxX(profile.getMaxTime());
//        viewport.setMaxY(profile.getmaxPos());
//        viewport.scrollToEnd();

    }

    private void resetProfile(){
        editProfile = new DipProfile(currentProfile);
        mAdapter = new EditProfileAdapter(getContext(), editProfile);
        pointRecycler.setAdapter(mAdapter);
    }
    private void saveProfileDialog(){
        final DialogFragment df = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.save_profile_dialog,null,false);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final ArrayList profileList = new ArrayList (Arrays.asList(pathToProfileHash.keySet().toArray(new String[pathToProfileHash.size()])));
                final Spinner dropdown = (Spinner) view.findViewById(R.id.save_profile_spinner);
                final EditText profileName = (EditText) view.findViewById(R.id.new_profile_name);
                final CheckBox confirmationCheckbox = (CheckBox) view.findViewById(R.id.confirmation_checkbox);

                confirmationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            profileName.setEnabled(false);
                            dropdown.setEnabled(true);
                        }
                        else{
                            profileName.setEnabled(true);
                            dropdown.setEnabled(false);
                        }
                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);

                if(pathToProfileHash.get(currentProfile.getTitle()) != null){
                    confirmationCheckbox.setChecked(true);
                    dropdown.setSelection(profileList.indexOf(currentProfile.getTitle()));
                }

                builder.setTitle("Save Profile?");
                builder.setView(view);
                builder.setNegativeButton(android.R.string.cancel , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if(confirmationCheckbox.isChecked()) {
//                            pathToProfileHash.put(dropdown.getSelectedItem().toString(),editProfile);
//                            currentProfile = new DipProfile(editProfile);
//                        }

                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = profileName.getText().toString();
                        if(confirmationCheckbox.isChecked()) {
                            pathToProfileHash.put(dropdown.getSelectedItem().toString(),editProfile);
                            currentProfile = new DipProfile(editProfile);
                            dismiss();
                        }else if(title != "" && title !=" " && !title.contains("  ")){
                            //TODO fix saving a bit
                            pathToProfileHash.put(title, editProfile);
                            currentProfile = new DipProfile(editProfile);
                            dismiss();
                        }
                    }
                });

                return dialog;
            }
        };
        df.show(getActivity().getSupportFragmentManager(),"save");

    }

    private void selectProfileDialog(){
        DialogFragment df = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState){

                final View view = getActivity().getLayoutInflater().inflate(R.layout.profile_choose_dialog,null,false);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final ArrayList profileList = new ArrayList (Arrays.asList(pathToProfileHash.keySet().toArray(new String[pathToProfileHash.size()])));
                final Spinner dropdown = (Spinner) view.findViewById(R.id.profile_choose_spinner);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);

                builder.setView(view);
                builder.setTitle("Choose A Profile");
                builder.setNegativeButton(android.R.string.cancel , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        currentProfile = pathToProfileHash.get(dropdown.getSelectedItem().toString());
                        resetProfile();
//                        editProfile = new DipProfile(currentProfile);
//                        mAdapter = new EditProfileAdapter(getContext(), editProfile);
//                        pointRecycler.setAdapter(mAdapter);

                        graph.removeAllSeries();
                        setGraphMaxes(editProfile);
                        graph.addSeries(editProfile.getLineGraphSeries());



                    }

                });

                return builder.create();
            }
        };
        df.show(getActivity().getSupportFragmentManager(),"select");

    }

    @Override
    public void setNewProfileHash(HashMap<String, DipProfile> newHash) {
        pathToProfileHash = newHash;
    }

    @Override
    public void setNewProgramHash(HashMap<String, DipProgram> newHash) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
