package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Spinner;

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

    private static final String HASH = "hash";
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
//            currentProfile = (DipProfile) getArguments().getParcelable(PROFILE);
            ProfileHashParcel profileHashParcel = (ProfileHashParcel) getArguments().getParcelable(HASH);
            pathToProfileHash = profileHashParcel.getHash();
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

        viewport = graph.getViewport();



        //DONE fix this as this is just a way to test and will crash if path is null
//        currentProfile = pathToProfileHash.get(pathToProfileHash.keySet().toArray()[0]);
        currentProfile = new DipProfile();

        if(currentProfile == null) currentProfile = new DipProfile();

        editProfile = new DipProfile(currentProfile);
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
                editProfile = new DipProfile(currentProfile);
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

    private void saveProfileDialog(){
        DialogFragment df = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.save_profile_dialog,null,false);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final ArrayList profileList = new ArrayList (Arrays.asList(pathToProfileHash.keySet().toArray(new String[pathToProfileHash.size()])));
                final Spinner dropdown = (Spinner) view.findViewById(R.id.save_profile_spinner);
                final CheckBox confirmationCheckbox = (CheckBox) view.findViewById(R.id.confirmation_checkbox);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);


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

                        if(confirmationCheckbox.isChecked()) {
                            pathToProfileHash.put(dropdown.getSelectedItem().toString(),editProfile);
                            currentProfile = new DipProfile(editProfile);
                        }
                    }

                });

                return builder.create();
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
                        editProfile = new DipProfile(currentProfile);

                        mAdapter = new EditProfileAdapter(getContext(), editProfile);
                        pointRecycler.setAdapter(mAdapter);

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
