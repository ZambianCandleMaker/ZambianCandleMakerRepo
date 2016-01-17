package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OperatorFragment extends Fragment {

    private static final int DEFAULT_MAXIMUM_VERT_JOG = 1500;
    private static final int DEFAULT_MAXIMUM_ROT_JOG = 360;
    private Callback mCallback;
    private int vertNumber;
    private int rotNumber;
    private int dipNumber;

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
        View view = inflater.inflate(R.layout.fragment_operator, container, false);
        RelativeLayout opView = (RelativeLayout) view.findViewById(R.id.operator_view);
        final EditText rotJogText = (EditText) view.findViewById(R.id.jog_rot_text);
        final EditText vertJogText = (EditText) view.findViewById(R.id.jog_vert_text);
        final EditText numDipsRot = (EditText) view.findViewById(R.id.dips_per_rev_text);
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
                String vertString = vertJogText.getText().toString();
                int vertInt = Integer.parseInt(vertString);
                if(vertInt <= DEFAULT_MAXIMUM_VERT_JOG){
                    vertNumber = vertInt;
                }
                else{
                    sendInvalidVerticalNumberDialog(vertInt);
                }
            }
        });
        rotJogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rotString = rotJogText.getText().toString();
                int rotInt = Integer.parseInt(rotString);
                if(rotInt <= DEFAULT_MAXIMUM_ROT_JOG){
                    rotNumber = rotInt;
                }
                else{
                    sendInvalidRotationalNumberDialog(rotInt);
                }
            }
        });
        return view;
    }

    public void sendInvalidRotationalNumberDialog(final int rotInt){
        DialogFragment df = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                builder.setTitle(getResources().getString(R.string.invalid_rotational_jog_input));
                View view = getActivity().getLayoutInflater().inflate(R.layout.invalid_number_dialog, null, false);
                TextView prompt = (TextView) view.findViewById(R.id.invalid_text_prompt);
                prompt.setText(String.format(getResources().getString(R.string.invalid_rot_dialog_prompt), rotInt));
                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, null);
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };

        df.show(getFragmentManager(), "");
    }

    public void sendInvalidVerticalNumberDialog(final int vertInt){
        DialogFragment df = new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle b) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                builder.setTitle(getResources().getString(R.string.invalid_vertical_jog_input));
                View view = getActivity().getLayoutInflater().inflate(R.layout.invalid_number_dialog, null, false);
                TextView prompt = (TextView) view.findViewById(R.id.invalid_text_prompt);
                prompt.setText(String.format(getResources().getString(R.string.invalid_vert_dialog_prompt), vertInt));
                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, null);
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };

        df.show(getFragmentManager(), "");
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
