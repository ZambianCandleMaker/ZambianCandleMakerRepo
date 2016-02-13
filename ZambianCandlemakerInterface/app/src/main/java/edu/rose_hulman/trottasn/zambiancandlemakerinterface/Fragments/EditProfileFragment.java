package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.CallbackActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.MainActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.EditProfileAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.TimePosPair;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;


public class EditProfileFragment extends Fragment {
    private RecyclerView pointRecycler;
    private EditProfileAdapter mAdapter;
    private GraphView graph;
    private LineGraphSeries<DataPoint> currentSeries;
    private SharedPreferences prefs;
    private TextView profileTitleView;

    private static final String HASH = "hash";
    private static final String EDIT_PROFILE = "edit_profile";
    private static final String CURRENT_PROFILE = "current_profile";
    private CallbackActivity mCallback;

    private static DipProfile currentProfile;

    private static Map<String, DipProfile> pathToProfileHash;



    public static EditProfileFragment newInstance(Parcelable inHash) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        String currentJson = gson.toJson(currentProfile);

        prefsEditor.putString(CURRENT_PROFILE, currentJson);
        prefsEditor.apply();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.savePathToProfileHash(pathToProfileHash);
    }

    @Override
    public void onStart() {
        super.onStart();
        Gson gson = new Gson();
        String json;
        if(prefs.contains(CURRENT_PROFILE)){

            json = prefs.getString(CURRENT_PROFILE, "");
            currentProfile = gson.fromJson(json, DipProfile.class);

            mAdapter = new EditProfileAdapter(getContext(),currentProfile);
            pointRecycler.setAdapter(mAdapter);

            profileTitleView.setText(getResources().getString(R.string.edit_points) + " " + currentProfile.getTitle());

            updateGraph();

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        setHasOptionsMenu(true);

        pointRecycler = (RecyclerView) view.findViewById(R.id.profile_point_recycler);
        graph = (GraphView) view.findViewById(R.id.edit_graph);
        profileTitleView = (TextView) view.findViewById(R.id.edit_point_title);

        if(currentProfile == null) createNewCurrentProfile();

        pointRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pointRecycler.setHasFixedSize(true);

        final Button confirmationButton = (Button) view.findViewById(R.id.insert_point_button);
        final EditText timeText = (EditText) view.findViewById(R.id.edit_time_text);
        final EditText depthText = (EditText) view.findViewById(R.id.edit_depth_text);

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = currentProfile.getPairList().size();
                int time = Integer.parseInt(timeText.getText().toString());
                int depth = Integer.parseInt(depthText.getText().toString());
                int pos = currentProfile.addPair(new TimePosPair(depth,time));

                if(currentProfile.getPairList().size() > size) mAdapter.notifyItemInserted(pos);
                else mAdapter.notifyItemChanged(pos);

                updateGraph();


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
                resetCurrentProfile();

            }
        });

        return view;

    }

    private void createNewCurrentProfile() {
        currentProfile = new DipProfile();
        for(int i = 1; ; i++){
            String title = "New Profile "+ i;
            if(!pathToProfileHash.containsKey(title)) {
                currentProfile.setTitle(title);
                updateAdapter();
                return;
            }

        }
    }

    private void resetCurrentProfile(){
        String title = currentProfile.getTitle();
        if(pathToProfileHash.containsKey(title)) currentProfile = new DipProfile(pathToProfileHash.get(title));
        else {
            currentProfile = new DipProfile();
            currentProfile.setTitle(title);
        }
        updateAdapter();
    }

    private void updateAdapter(){
        profileTitleView.setText(getResources().getString(R.string.edit_points) + " " + currentProfile.getTitle());
        mAdapter = new EditProfileAdapter(getContext(), currentProfile);
        pointRecycler.setAdapter(mAdapter);
        updateGraph();
    }

    private void updateGraph(){
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = currentProfile.getLineGraphSeries();
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setColor(getResources().getColor(R.color.graphSeriesLine));
        graph.addSeries(series);
    }

    //TODO
    private void renameProfile(){

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_choose_program);
        item.setVisible(false);

        MenuItem choose_profile_item = menu.findItem(R.id.action_choose_profile);
        choose_profile_item.setVisible(true);

        MenuItem reset_profile_item = menu.findItem(R.id.action_new_profile);
        reset_profile_item.setVisible(true);

        MenuItem delete_profile_item = menu.findItem(R.id.action_delete_profile);
        delete_profile_item.setVisible(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_choose_profile:
                selectProfileDialog();
                break;
            case R.id.action_new_profile:
                createNewCurrentProfile();
                break;
            case R.id.action_delete_profile:
                deleteProfileDialog();
            default:
        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackActivity) {
            mCallback = (CallbackActivity) context;
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


    private void saveProfileDialog(){
        final DialogFragment df = new DialogFragment(){
            ArrayList profileList;
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.save_profile_dialog,null,false);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                profileList = new ArrayList<String>(Arrays.asList(pathToProfileHash.keySet().toArray(new String[pathToProfileHash.size()])));
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

//                Collections.sort(profileList,String.CASE_INSENSITIVE_ORDER);

                if(profileList.contains(currentProfile.getTitle())){
                    confirmationCheckbox.setChecked(true);
                    dropdown.setSelection(profileList.indexOf(currentProfile.getTitle()));
                }else {
                    confirmationCheckbox.setChecked(false);
                    profileName.setText(currentProfile.getTitle());

                }


                builder.setTitle("Save Profile?");
                builder.setView(view);
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = profileName.getText().toString();

                        if (confirmationCheckbox.isChecked()) {
                            pathToProfileHash.put(dropdown.getSelectedItem().toString(), currentProfile);
                            mCallback.savePathToProfileHash(pathToProfileHash);
                            dismiss();

                        } else if (title != "" && title != " " && !title.contains("  ")) {
                            currentProfile = new DipProfile(currentProfile);
                            currentProfile.setTitle(title);
                            pathToProfileHash.put(title, currentProfile);
                            mCallback.savePathToProfileHash(pathToProfileHash);
                            profileTitleView.setText(getResources().getString(R.string.edit_points) + " " + currentProfile.getTitle());
                            dismiss();

                        }
                    }
                });

                return dialog;
            }
        };
        df.show(getActivity().getSupportFragmentManager(),"save");

    }

    private void deleteProfileDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Profile?");
        builder.setMessage("Are you sure you want to delete the profile "+currentProfile.getTitle()+"?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pathToProfileHash.remove(currentProfile.getTitle());
                        createNewCurrentProfile();
                        pathToProfileHash.remove("derp");
                    }
                });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void selectProfileDialog(){
        DialogFragment df = new DialogFragment(){
            ArrayList profileList;
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState){

                final View view = getActivity().getLayoutInflater().inflate(R.layout.profile_choose_dialog,null,false);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                profileList = new ArrayList<String>(Arrays.asList(pathToProfileHash.keySet().toArray(new String[pathToProfileHash.size()])));
                final Spinner dropdown = (Spinner) view.findViewById(R.id.profile_choose_spinner);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);

                builder.setView(view);
                builder.setTitle("Choose Profile");
                builder.setMessage("Please select a profile:");
                builder.setNegativeButton(android.R.string.cancel , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        currentProfile = new DipProfile(pathToProfileHash.get(dropdown.getSelectedItem().toString()));
                        resetCurrentProfile();



                    }

                });

                return builder.create();
            }

        };
        df.show(getActivity().getSupportFragmentManager(),"select");

    }
}
