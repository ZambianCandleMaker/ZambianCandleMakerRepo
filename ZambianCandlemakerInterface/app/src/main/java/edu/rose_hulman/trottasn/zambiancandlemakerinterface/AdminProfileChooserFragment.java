package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AdminProfileChooserFragment extends Fragment implements AvailableProfilesAdapter.ProfileChooserFragmentHelper {

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private OnAdminProfileChosenListener mListener;

    public AdminProfileChooserFragment() {
        // Required empty public constructor
    }

    public static AdminProfileChooserFragment newInstance() {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
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
        mAvailableAdapter.addProfile(new DipProfile("title", "description"));
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
    public void returnToTop() {
        mAvailableRecycler.scrollToPosition(0);
    }

    @Override
    public void slideToPosition(int position) {
        mAvailableRecycler.scrollToPosition(position);
    }

    public interface OnAdminProfileChosenListener {
        void onProfileChosen(Uri uri);
    }
}
