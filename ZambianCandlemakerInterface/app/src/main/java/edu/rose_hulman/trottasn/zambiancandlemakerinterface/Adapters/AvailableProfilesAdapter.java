package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class AvailableProfilesAdapter extends RecyclerView.Adapter<AvailableProfilesAdapter.ViewHolder> {

    private ArrayList<DipProfile> mAvailableProfiles;
    private ProfileChooserFragmentHelper mProfileChooserHelper;

    public AvailableProfilesAdapter(ProfileChooserFragmentHelper chooseHelper) {
        mProfileChooserHelper = chooseHelper;
        mAvailableProfiles = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_profile_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DipProfile dipProfile = mAvailableProfiles.get(position);
        String title = dipProfile.getTitle();
        String description = dipProfile.getDescription();
        holder.mTitleView.setText(title);
        holder.mDescriptionView.setText(description);
    }

    @Override
    public int getItemCount() {
        return mAvailableProfiles.size();
    }

    public void addProfile(DipProfile newProfile){
        mAvailableProfiles.add(newProfile);
        mProfileChooserHelper.returnAvailableToTop();
        notifyItemInserted(mAvailableProfiles.size() - 1);
    }

    public DipProfile removeProfile(int position){
        DipProfile removed = mAvailableProfiles.remove(position);
        mProfileChooserHelper.slideAvailableToPosition(position);
        notifyItemRemoved(position);
        return removed;
    }

    public void clear(){
        mAvailableProfiles.clear();
        mProfileChooserHelper.returnAvailableToTop();
        notifyDataSetChanged();
    }

    public void subtractProfiles(List<DipProfile> dipProfiles){
        if(dipProfiles != null){
            mAvailableProfiles.removeAll(dipProfiles);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleView;
        private TextView mDescriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.avail_profile_title);
            mDescriptionView = (TextView) itemView.findViewById(R.id.avail_profile_desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("AVAIL_CLICK", "Clicked a View");
            DipProfile lastRemoved = removeProfile(getAdapterPosition());
            Log.d("CHECK", lastRemoved.getTitle());
            mProfileChooserHelper.addToSelectedAdapter(lastRemoved);
        }
    }

    public interface ProfileChooserFragmentHelper {
        void returnAvailableToTop();
        void slideAvailableToPosition(int position);
        void addToSelectedAdapter(DipProfile dipProfile);
    }
}
