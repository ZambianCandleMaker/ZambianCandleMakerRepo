package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters.EditProfileAdapter;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;


public class EditProfileFragment extends Fragment {

    OperatorFragment.OperatorFragmentListener mCallback;

    private RecyclerView pointRecycler;

    private static final String PROFILE = "profile";
    private static DipProfile currentProfile;

    public static EditProfileFragment newInstance(Parcelable profile) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putParcelable(PROFILE, profile);
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
            currentProfile = (DipProfile) getArguments().getParcelable(PROFILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        pointRecycler = (RecyclerView) view.findViewById(R.id.profile_point_recycler);

        pointRecycler.setAdapter(new EditProfileAdapter(getContext(), currentProfile));

        pointRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pointRecycler.setHasFixedSize(true);
        return view;

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
