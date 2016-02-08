package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by TrottaSN on 1/25/2016.
 */
public class AdministratorProfileFragment extends Fragment {

    private OperatorFragment.Callback mCallback;

    public AdministratorProfileFragment() {
        // Required empty public constructor
    }

    public static AdministratorProgramFragment newInstance() {
        AdministratorProgramFragment fragment = new AdministratorProgramFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.administrator_program_fragment, container, false);
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
}
