package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class GraphFragment extends Fragment {

    private OperatorFragment.Callback mCallback;
    private Series<DataPoint> currentSeries;
    private Context mContext;
    private GraphView graph;

    public GraphFragment() {
        // Required empty public constructor
    }

    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(currentSeries == null) showSwitchDialog();

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
        if (context instanceof OperatorFragment.Callback) {
            mCallback = (OperatorFragment.Callback) context;
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


//                final ArrayList profileList = new ArrayList (Arrays.asList(profileSet.keySet().toArray(new String[profileSet.size()])));
               final Spinner dropdown = (Spinner) view.findViewById(R.id.spinner);

//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,profileList);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                dropdown.setAdapter(adapter);

                builder.setView(view);
                if(currentSeries == null) builder.setTitle("Choose A Profile");
                else builder.setTitle("Switch Profile");

                builder.setNegativeButton(android.R.string.cancel , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getFragmentManager().popBackStackImmediate();
                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        currentSeries = profileToSeries(profileSet.get(dropdown.getSelectedItem().toString()));
//                        graph.removeAllSeries();

//                        graph.addSeries(currentSeries);
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
        df.setCancelable(false);
        df.show(getActivity().getSupportFragmentManager(),"search");

    }

    public LineGraphSeries<DataPoint> getLineGraphSeries(LinkedList<TimePosPair> link){
        Iterator<TimePosPair> i = link.listIterator();

        ArrayList<DataPoint> sList = new ArrayList<DataPoint>();

        while(i.hasNext()){
            TimePosPair p = i.next();
            sList.add(new DataPoint(p.getTime(),p.getPosition()));
        }

        DataPoint[] dp = sList.toArray(new DataPoint[sList.size()]);
        return new LineGraphSeries<DataPoint>(dp);
    }

    public LineGraphSeries<DataPoint> profileToSeries(String profile){
        return getLineGraphSeries(readProfile(profile));
    }

    public LinkedList<TimePosPair> seriesToLinkedList(LineGraphSeries<DataPoint> series){
        LinkedList<TimePosPair> l = new LinkedList<TimePosPair>();
        Iterator<DataPoint> i = series.getValues(0,series.getHighestValueX());
        while (i.hasNext()){
            DataPoint d = i.next();
            l.add(new TimePosPair((int) d.getY(),(int) d.getX()));
        }

        return l;
    }

    public LinkedList<TimePosPair> readProfile(String profile){
        LinkedList l = new LinkedList();
        int pos;
        int time;

        for(int i = 0; i < profile.length(); i++){

            if(profile.charAt(i) == '['){
                String s="";
                for(i++;profile.charAt(i) != ':';i++){
                    s = s + profile.charAt(i);
                }

                pos = Integer.parseInt(s);
                s = "";
                for(i++;profile.charAt(i) != ']';i++){
                    s = s + profile.charAt(i);
                }
                time = Integer.parseInt(s);
                l.add(new TimePosPair(pos,time));
            }

        }

        return l;

    }

}
