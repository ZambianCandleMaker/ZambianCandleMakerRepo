package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Activities.MainActivity;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class OperatorFragment extends Fragment {
    private static final String VERTICAL_SELECTION_KEY = "VERTICAL_SELECTION";
    private static final String ROTATIONAL_SELECTION_KEY = "ROTATIONAL_SELECTION";
    private static final String DIPS_SELECTION_KEY = "DIPS_SELECTION";
    private static final String UNIT_SELECTION_KEY = "UNIT_SELECTION";
    private static final String PROGRAM_SELECTION_KEY = "PROGRAM_SELECTION";
    private static final int MAX_DIPS_PER_REV = 25;
    private static final int MIN_DIPS_PER_REV = 1;
    private String vertical_units;
    private int vertical_selection;
    private int rotational_selection;
    private int dips_selection;
    private int unit_selection;
    private OperatorFragmentListener mCallback;
    private ArrayAdapter<CharSequence> vertical_adapter_cm;
    private ArrayAdapter<CharSequence> vertical_adapter_mm;
    private ArrayAdapter<CharSequence> rotational_adapter;

    private ArrayAdapter<CharSequence> prog_adapter;

    private Button vertJogButton;
    private Button rotJogButton;
    private Button dipsButton;
    private Button softPauseButton;
    private Button manualVertUp;
    private Button manualVertDown;
    private Button manualRotLeft;
    private Button manualRotRight;
    private Button programButton;

    private Handler mHandler;
    private String mDirection;
    private Map<String, DipProgram> pathToProgramHash;
    private int programSelection;

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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        dips_selection = sharedPref.getInt(DIPS_SELECTION_KEY, 0);
        unit_selection = sharedPref.getInt(UNIT_SELECTION_KEY, 0);
        vertical_selection = sharedPref.getInt(VERTICAL_SELECTION_KEY, 0);
        rotational_selection = sharedPref.getInt(ROTATIONAL_SELECTION_KEY, 0);
        programSelection = sharedPref.getInt(PROGRAM_SELECTION_KEY, 0);
        Gson gson = new Gson();
        String programHashString = sharedPref.getString(MainActivity.PROGRAM_HASH, "");
        Type progHashType = new TypeToken<HashMap<String, DipProgram>>(){}.getType();
        pathToProgramHash = gson.fromJson(programHashString, progHashType);
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(VERTICAL_SELECTION_KEY, vertical_selection);
        editor.putInt(ROTATIONAL_SELECTION_KEY, rotational_selection);
        editor.putInt(DIPS_SELECTION_KEY, dips_selection);
        editor.putInt(UNIT_SELECTION_KEY, unit_selection);
        editor.putInt(PROGRAM_SELECTION_KEY, programSelection);
        Gson gson = new Gson();
        String pathToProgramHashJson = gson.toJson(pathToProgramHash);
        editor.putString(MainActivity.PROGRAM_HASH, pathToProgramHashJson);
        editor.apply();
    }

    @Override
    public void onResume(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        dips_selection = sharedPref.getInt(DIPS_SELECTION_KEY, 0);
        unit_selection = sharedPref.getInt(UNIT_SELECTION_KEY, 0);
        vertical_selection = sharedPref.getInt(VERTICAL_SELECTION_KEY, 0);
        rotational_selection = sharedPref.getInt(ROTATIONAL_SELECTION_KEY, 0);
        programSelection = sharedPref.getInt(PROGRAM_SELECTION_KEY, 0);
        Gson gson = new Gson();
        String programHashString = sharedPref.getString(MainActivity.PROGRAM_HASH, "");
        Type progHashType = new TypeToken<HashMap<String, DipProgram>>(){}.getType();
        pathToProgramHash = gson.fromJson(programHashString, progHashType);
        prog_adapter.clear();
        prog_adapter.addAll(pathToProgramHash.keySet());
        super.onResume();
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

        // ArrayAdapter to be used when centimeters are selected on the unit spinner
        ArrayAdapter<CharSequence> adapter_cm = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = 1; i < 16; i++) {
            adapter_cm.add(String.valueOf(i));
        }
        adapter_cm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.vertical_adapter_cm = adapter_cm;

        // ArrayAdapter to be used when millimeters are selected on the unit spinner
        ArrayAdapter<CharSequence> adapter_mm = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = 1; i < 11; i++) {
            adapter_mm.add(String.valueOf(i));
        }
        adapter_mm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.vertical_adapter_mm = adapter_mm;
        final Spinner vertJogSpinner = (Spinner) view.findViewById(R.id.vert_spinner);
        vertJogSpinner.setSelection(vertical_selection);

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
                    unit_selection = 0;
                } else if (vertical_units.equals("mm")) {
                    vertJogSpinner.setAdapter(vertical_adapter_mm);
                    unit_selection = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vertical_units = "cm";
                vertJogSpinner.setAdapter(vertical_adapter_cm);
                unit_selection = 0;
            }
        });

        unitSpinner.setSelection(unit_selection);

        // ArrayAdapter to be used when millimeters are selected on the unit spinner
        ArrayAdapter<CharSequence> adapter_rot = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = 15; i <= 360; i += 15) {
            adapter_rot.add(String.valueOf(i));
        }
        adapter_rot.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.rotational_adapter = adapter_rot;
        final Spinner rotJogSpinner = (Spinner) view.findViewById(R.id.rot_spinner);
        rotJogSpinner.setAdapter(this.rotational_adapter);
        rotJogSpinner.setSelection(rotational_selection);

        // ArrayAdapter to be used for the number of dips per revolution spinner
        ArrayAdapter<CharSequence> dips_per_rev_array = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());
        for (int i = MIN_DIPS_PER_REV; i < MAX_DIPS_PER_REV; i++) {
            dips_per_rev_array.add(String.valueOf(i));
        }
        dips_per_rev_array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner dipsPerRevSpinner = (Spinner) view.findViewById(R.id.dips_per_rev_spinner);
        dipsPerRevSpinner.setAdapter(dips_per_rev_array);
        dipsPerRevSpinner.setSelection(dips_selection);

        final Spinner progNameSpinner = (Spinner) view.findViewById(R.id.program_spinner);

        prog_adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        for(String progName : pathToProgramHash.keySet()){
            prog_adapter.add(progName);
        }
        prog_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        progNameSpinner.setAdapter(prog_adapter);
        progNameSpinner.setSelection(programSelection);

        progNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                programSelection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nothing
            }
        });

        programButton = (Button) view.findViewById(R.id.apply_program_button);
        programButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(progNameSpinner.getSelectedItem() != null){
                    openProgramWarningDialog(pathToProgramHash.get(String.valueOf(progNameSpinner.getSelectedItem())));
                }
            }
        });

        vertJogButton = (Button) view.findViewById(R.id.apply_vert_jog_button);
        rotJogButton = (Button) view.findViewById(R.id.apply_rot_jog_button);
        dipsButton = (Button) view.findViewById(R.id.apply_dips_button);
        softPauseButton = (Button) view.findViewById(R.id.pause_button);
        manualVertUp = (Button) view.findViewById(R.id.vert_up);
        manualVertDown = (Button) view.findViewById(R.id.vert_down);
        manualRotLeft = (Button) view.findViewById(R.id.rot_left);
        manualRotRight = (Button) view.findViewById(R.id.rot_right);

        vertJogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vertical_selection = Integer.parseInt(vertJogSpinner.getSelectedItem().toString());
                Toast.makeText(getContext(), R.string.applying_vert_jog, Toast.LENGTH_SHORT).show();
            }
        });

        rotJogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotational_selection = Integer.parseInt(rotJogSpinner.getSelectedItem().toString());
                Toast.makeText(getContext(), R.string.applying_rot_jog, Toast.LENGTH_SHORT).show();
            }
        });

        dipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dips_selection = Integer.parseInt(dipsPerRevSpinner.getSelectedItem().toString());
                Toast.makeText(getContext(), R.string.applying_dips_per, Toast.LENGTH_SHORT).show();
            }
        });

        softPauseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    softPauseButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.pause_button_red));
                    sendCautionaryPauseDialog();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    softPauseButton.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_pause_button));
                }
                return true;
            }
        });

        manualRotLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    manualRotLeft.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.left_rotate_red));
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    manualRotLeft.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_left_rotate));
                }
                return setForRepetition("LEFT", event, mAction);
            }
        });
        manualRotRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    manualRotRight.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.right_rotate_red));
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    manualRotRight.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_right_rotate));
                }
                return setForRepetition("RIGHT", event, mAction);
            }
        });
        manualVertUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    manualVertUp.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.arrow_up_red));
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    manualVertUp.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_up_jog));
                }
                return setForRepetition("UP", event, mAction);
            }
        });
        manualVertDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    manualVertDown.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.arrow_down_red));
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    manualVertDown.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_down_jog));
                }
                return setForRepetition("DOWN", event, mAction);
            }
        });
        return view;
    }

    public boolean setForRepetition(String variableString, MotionEvent event, Runnable mAction){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mHandler != null) return true;
            mDirection = variableString;
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

    public void openProgramWarningDialog(final DipProgram dipProgram){
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_menu_send);
                builder.setTitle(getResources().getString(R.string.are_you_sure_program));
                View view = getActivity().getLayoutInflater().inflate(R.layout.delete_program_dialog, null, false);
                TextView titleText = (TextView) view.findViewById(R.id.program_title_delete_dialog);
                titleText.setText(dipProgram.getTitle());
                TextView descText = (TextView) view.findViewById(R.id.program_desc_delete_dialog);
                descText.setText(dipProgram.getDescription());
                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Call arduino message code
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };

        df.show(getFragmentManager(), "");
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
        if (context instanceof OperatorFragmentListener) {
            mCallback = (OperatorFragmentListener) context;
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

    public interface OperatorFragmentListener {
    }
}
