package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class OperatorFragment extends Fragment {
    private static final int MAX_DIPS_PER_REV = 25;
    private static final int MIN_DIPS_PER_REV = 1;
    private String vertical_units;
    private int vertical_selection;
    private int rotational_selection;
    private int dips_selection;
    private Callback mCallback;
    private ArrayAdapter<CharSequence> vertical_adapter_cm;
    private ArrayAdapter<CharSequence> vertical_adapter_mm;
    private ArrayAdapter<CharSequence> rotational_adapter;
    private Handler mHandler;
    private String mDirection;

    public OperatorFragment() {
        // Required empty public constructor
    }

    public static OperatorFragment newInstance() {
        OperatorFragment fragment = new OperatorFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.operator_fragment, container, false);

        mHandler = new Handler();

        final Runnable mAction = new Runnable() {
            @Override public void run() {
                switch(mDirection) {
                    case "LEFT":
                        Log.d("TESTING_RUNNABLE", "LEFT");
                        break;
                    case "RIGHT":
                        Log.d("TESTING_RUNNABLE", "RIGHT");
                        break;
                    case "UP":
                        Log.d("TESTING_RUNNABLE", "UP");
                        break;
                    case "DOWN":
                        Log.d("TESTING_RUNNABLE", "DOWN");
                        break;
                }
                mHandler.postDelayed(this, 100);
            }
        };

        RelativeLayout opView = (RelativeLayout) view.findViewById(R.id.operator_view);

        // ArrayAdapter to be used when centimeters are selected on the unit spinner
        ArrayAdapter<CharSequence> adapter_cm = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = 1; i < 16; i++) {
            adapter_cm.add(String.valueOf(i));
        }
        adapter_cm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.vertical_adapter_cm = adapter_cm;

        // ArrayAdapter to be used when millimeters are selected on the unit spinner
        ArrayAdapter<CharSequence> adapter_mm = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = 1; i < 11; i++) {
            adapter_mm.add((CharSequence) String.valueOf(i));
        }
        adapter_mm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.vertical_adapter_mm = adapter_mm;
        final Spinner vertJogSpinner = (Spinner) view.findViewById(R.id.vert_spinner);

        final Spinner unitSpinner = (Spinner) view.findViewById(R.id.unit_spinner);
        ArrayAdapter<CharSequence> adapter_units = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        adapter_units.add("cm");
        adapter_units.add("mm");
        adapter_units.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter_units);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vertical_units = parent.getItemAtPosition(position).toString();
                if (vertical_units.equals("cm")) {
                    vertJogSpinner.setAdapter(vertical_adapter_cm);
                } else if (vertical_units.equals("mm")) {
                    vertJogSpinner.setAdapter(vertical_adapter_mm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vertical_units = "cm";
                vertJogSpinner.setAdapter(vertical_adapter_cm);
            }
        });

        // ArrayAdapter to be used when millimeters are selected on the unit spinner
        ArrayAdapter<CharSequence> adapter_rot = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = 15; i <= 360; i += 15) {
            adapter_rot.add((CharSequence) String.valueOf(i));
        }
        adapter_rot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.rotational_adapter = adapter_rot;
        final Spinner rotJogSpinner = (Spinner) view.findViewById(R.id.rot_spinner);
        rotJogSpinner.setAdapter(this.rotational_adapter);

        // ArrayAdapter to be used for the number of dips per revolution spinner
        ArrayAdapter<CharSequence> dips_per_rev_array = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = MIN_DIPS_PER_REV; i < MAX_DIPS_PER_REV; i++) {
            dips_per_rev_array.add((CharSequence) String.valueOf(i));
        }
        dips_per_rev_array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner dipsPerRevSpinner = (Spinner) view.findViewById(R.id.dips_per_rev_spinner);
        dipsPerRevSpinner.setAdapter(dips_per_rev_array);

        final Button vertJogButton = (Button) view.findViewById(R.id.apply_vert_jog_button);
        final Button rotJogButton = (Button) view.findViewById(R.id.apply_rot_jog_button);
        final Button dipsButton = (Button) view.findViewById(R.id.apply_dips_button);
        final Button softPauseButton = (Button) view.findViewById(R.id.pause_button);
        final Button manualVertUp = (Button) view.findViewById(R.id.vert_up);
        final Button manualVertDown = (Button) view.findViewById(R.id.vert_down);
        final Button manualRotLeft = (Button) view.findViewById(R.id.rot_left);
        final Button manualRotRight = (Button) view.findViewById(R.id.rot_right);

        vertJogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vertical_selection = Integer.parseInt(vertJogSpinner.getSelectedItem().toString());
            }
        });
        rotJogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotational_selection = Integer.parseInt(rotJogSpinner.getSelectedItem().toString());
            }
        });
        dipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dips_selection = Integer.parseInt(dipsPerRevSpinner.getSelectedItem().toString());
            }
        });

        softPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCautionaryPauseDialog();
            }
        });

        manualRotLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) return true;
                    mDirection = "LEFT";
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                    Log.d("TESTING_RUNNABLE", "STOPPING");
                }
                return true;
            }
        });
        manualRotRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) return true;
                    mDirection = "RIGHT";
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                    Log.d("TESTING_RUNNABLE", "STOPPING");
                }
                return true;
            }
        });
        manualVertUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) return true;
                    mDirection = "UP";
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                    Log.d("TESTING_RUNNABLE", "STOPPING");
                }
                return true;
            }
        });
        manualVertDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (mHandler != null) return true;
                    mDirection = "DOWN";
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 0);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                    Log.d("TESTING_RUNNABLE", "STOPPING");
                }
                return true;
            }
        });
        return view;
    }

    public void sendCautionaryPauseDialog(){
        DialogFragment df = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                builder.setTitle(getResources().getString(R.string.caution_soft_pause));
                View view = getActivity().getLayoutInflater().inflate(R.layout.caution_soft_pause_dialog, null, false);
                TextView prompt = (TextView) view.findViewById(R.id.assurance_pause_prompt);
                prompt.setText(getResources().getString(R.string.pause_are_you_sure));
                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performSoftPause();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };

        df.show(getFragmentManager(), "");
    }

    private void performSoftPause(){
        Log.d("SOFT_PAUSE", "A Soft-Pause has been attempted!");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
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

    public interface Callback {
    }
}
