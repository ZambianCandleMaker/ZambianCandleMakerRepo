package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Parcels.ProfileHashParcel;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;


public class GraphFragment extends Fragment {

    private OperatorFragment.OperatorFragmentListener mCallback;
    private Series<DataPoint> currentSeries;
    private Context mContext;
    private GraphView graph;

    private static HashMap<String, DipProfile> pathToProfileHash;
    private static final String HASH = "hash";

    private static DipProfile currentProfile;

    private FloatingActionButton fab;
    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance(Parcelable inHash) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putParcelable(HASH, inHash);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ProfileHashParcel profileParcel = (ProfileHashParcel) getArguments().getParcelable(HASH);
            pathToProfileHash = profileParcel.getHash();

        }
        if(currentSeries == null) showSwitchDialog();

//        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        graph = (GraphView) view.findViewById(R.id.profile_graph);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OperatorFragment.OperatorFragmentListener) {
            mCallback = (OperatorFragment.OperatorFragmentListener) context;
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
    private void showSwitchDialog(){
        DialogFragment df = new DialogFragment(){
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState){

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_graph_profile_choose,null,false);


                final ArrayList profileList = new ArrayList (Arrays.asList(pathToProfileHash.keySet().toArray(new String[pathToProfileHash.size()])));
                final Spinner dropdown = (Spinner) view.findViewById(R.id.profile_choose_spinner);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,profileList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dropdown.setAdapter(adapter);

                builder.setView(view);
//                if(currentSeries == null)
                builder.setTitle("Choose A Profile");
//                else builder.setTitle("Switch Profile");

                builder.setNegativeButton(android.R.string.cancel , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStackImmediate();
                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        currentProfile = pathToProfileHash.get(dropdown.getSelectedItem().toString());
                        currentSeries = currentProfile.getLineGraphSeries();
                        currentSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {

                            }
                        });
                        graph.addSeries(currentSeries);
                    }

                });

                builder.setNeutralButton("NEW PROFILE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        showAddProfileDialog();

                    }
                });

                return builder.create();
            }
        };
//        df.setCancelable(false);
        df.show(getActivity().getSupportFragmentManager(),"search");

    }

//    public LinkedList<TimePosPair> seriesToLinkedList(LineGraphSeries<DataPoint> series){
//        LinkedList<TimePosPair> l = new LinkedList<TimePosPair>();
//        Iterator<DataPoint> i = series.getValues(0,series.getHighestValueX());
//        while (i.hasNext()){
//            DataPoint d = i.next();
//            l.add(new TimePosPair((int) d.getY(),(int) d.getX()));
//        }
//
//        return l;
//    }

//    public LinkedList<TimePosPair> readProfile(String profile){
//        LinkedList l = new LinkedList();
//        int pos;
//        int time;
//
//        for(int i = 0; i < profile.length(); i++){
//
//            if(profile.charAt(i) == '['){
//                String s="";
//                for(i++;profile.charAt(i) != ':';i++){
//                    s = s + profile.charAt(i);
//                }
//
//                pos = Integer.parseInt(s);
//                s = "";
//                for(i++;profile.charAt(i) != ']';i++){
//                    s = s + profile.charAt(i);
//                }
//                time = Integer.parseInt(s);
//                l.add(new TimePosPair(pos,time));
//            }
//
//        }
//
//        return l;
//
//    }



}