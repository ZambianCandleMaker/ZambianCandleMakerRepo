package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
<<<<<<< HEAD
=======
import android.os.Parcelable;
>>>>>>> master
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

<<<<<<< HEAD
public class AdminProfileChooserFragment extends Fragment implements AvailableProfilesAdapter.ProfileChooserFragmentHelper {
=======
import java.util.HashMap;

public class AdminProfileChooserFragment extends Fragment implements AvailableProfilesAdapter.ProfileChooserFragmentHelper, SelectedProfilesAdapter.ProfileSelectedHelper {
>>>>>>> master

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private OnAdminProfileChosenListener mListener;
<<<<<<< HEAD
=======
    private static HashMap<String, DipProfile> pathToProfileHash;
    private static final String HASH = "hash";
>>>>>>> master

    public AdminProfileChooserFragment() {
        // Required empty public constructor
    }

<<<<<<< HEAD
    public static AdminProfileChooserFragment newInstance() {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
        Bundle args = new Bundle();
=======
    public static AdminProfileChooserFragment newInstance(Parcelable inHash) {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
        Bundle args = new Bundle();
        args.putParcelable(HASH, inHash);
>>>>>>> master
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
<<<<<<< HEAD
=======
            HashMapParcel profileParcel = (HashMapParcel) getArguments().getParcelable(HASH);
            pathToProfileHash = profileParcel.getHash();
>>>>>>> master
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View totalView = inflater.inflate(R.layout.fragment_admin_profile_chooser, container, false);

        mAvailableRecycler = (RecyclerView) totalView.findViewById(R.id.remaining_choices_recycler);
        mSelectedRecycler = (RecyclerView) totalView.findViewById(R.id.selected_recycler);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAvailableRecycler.setLayoutManager(layoutManager);

        final LinearLayoutManager layoutManagerSelected = new LinearLayoutManager(getContext());
        layoutManagerSelected.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectedRecycler.setLayoutManager(layoutManagerSelected);

        mAvailableAdapter = new AvailableProfilesAdapter(this);
        mAvailableRecycler.setAdapter(mAvailableAdapter);
<<<<<<< HEAD
        mAvailableAdapter.addProfile(new DipProfile("title", "description"));
=======
        for(DipProfile dipProf : pathToProfileHash.values()){
            mAvailableAdapter.addProfile(dipProf);
        }
>>>>>>> master
        return totalView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileChosen(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAdminProfileChosenListener) {
            mListener = (OnAdminProfileChosenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
<<<<<<< HEAD
    public void returnToTop() {
=======
    public void returnAvailableToTop() {
>>>>>>> master
        mAvailableRecycler.scrollToPosition(0);
    }

    @Override
<<<<<<< HEAD
    public void slideToPosition(int position) {
        mAvailableRecycler.scrollToPosition(position);
    }

=======
    public void slideAvailableToPosition(int position) {
        mAvailableRecycler.scrollToPosition(position);
    }

    @Override
    public void returnSelectedToTop() {

    }

    @Override
    public void slideSelectedToPosition(int position) {

    }

>>>>>>> master
    public interface OnAdminProfileChosenListener {
        void onProfileChosen(Uri uri);
    }
}
