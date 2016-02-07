package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;

public class AdminProfileChooserFragment extends Fragment implements ProfileHashFragment, AvailableProfilesAdapter.ProfileChooserFragmentHelper, SelectedProfilesAdapter.ProfileSelectedHelper {

    private RecyclerView mSelectedRecycler;
    private RecyclerView mAvailableRecycler;
    private AvailableProfilesAdapter mAvailableAdapter;
    private SelectedProfilesAdapter mSelectedAdapter;
    private OnAdminProfileChosenListener mListener;
    private static HashMap<String, DipProfile> pathToProfileHash;
    private static final String HASH = "hash";
    private static final String OBSERVER = "observer";
    private FileObserver mFileObserver;


    public AdminProfileChooserFragment() {
        // Required empty public constructor
    }

    public static AdminProfileChooserFragment newInstance(Parcelable inHash, Parcelable inObserver) {
        AdminProfileChooserFragment fragment = new AdminProfileChooserFragment();
        Bundle args = new Bundle();
        args.putParcelable(HASH, inHash);
        args.putParcelable(OBSERVER, inObserver);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            HashMapParcel profileParcel = getArguments().getParcelable(HASH);
            FileObserverParcel observerParcel = getArguments().getParcelable(OBSERVER);
            pathToProfileHash = profileParcel.getHash();
            mFileObserver = observerParcel.getFileObserver();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View totalView = inflater.inflate(R.layout.fragment_admin_profile_chooser, container, false);

        mAvailableRecycler = (RecyclerView) totalView.findViewById(R.id.remaining_choices_recycler);
        mSelectedRecycler = (RecyclerView) totalView.findViewById(R.id.selected_recycler);
        mAvailableRecycler.setHasFixedSize(true);
        mAvailableRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSelectedRecycler.setHasFixedSize(true);
        mSelectedRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAvailableAdapter = new AvailableProfilesAdapter(this);
        mAvailableRecycler.setAdapter(mAvailableAdapter);

        mSelectedAdapter = new SelectedProfilesAdapter(this);
        mSelectedRecycler.setAdapter(mSelectedAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleTouchHelperCallback(mSelectedAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mSelectedRecycler);

        populateFromHash();
        return totalView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onProfileChosen(uri);
        }
    }

    private void populateFromHash() {
        if (mAvailableAdapter != null) {
            mAvailableAdapter.clear();
            for(DipProfile dipProf : pathToProfileHash.values()){
                mAvailableAdapter.addProfile(dipProf);
            }
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
    public void returnAvailableToTop() {
        mAvailableRecycler.scrollToPosition(0);
    }

    @Override
    public void slideAvailableToPosition(int position) {
        mAvailableRecycler.scrollToPosition(position);
    }

    @Override
    public void addToSelectedAdapter(DipProfile dipProfile) {
        mSelectedAdapter.addProfile(dipProfile);
    }

    @Override
    public void returnSelectedToTop() {

    }

    @Override
    public void slideSelectedToPosition(int position) {

    }

    @Override
    public void setNewHash(HashMap<String, DipProfile> newHash) {
        pathToProfileHash = newHash;
        populateFromHash();
    }

    public interface OnAdminProfileChosenListener {
        void onProfileChosen(Uri uri);
    }
}
