package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class SelectedProfilesAdapter extends RecyclerView.Adapter<SelectedProfilesAdapter.ViewHolder> {

    private ArrayList<DipProfile> mSelectedProfiles;
    private ProfileSelectedHelper mProfileSelectedHelper;

    public SelectedProfilesAdapter(ProfileSelectedHelper selectedHelper) {
        mProfileSelectedHelper = selectedHelper;
        mSelectedProfiles = new ArrayList<DipProfile>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_profile_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DipProfile dipProfile = mSelectedProfiles.get(position);
        String title = dipProfile.getTitle();
        String description = dipProfile.getDescription();
        holder.mTitleView.setText(title);
        holder.mDescriptionView.setText(description);
    }

    @Override
    public int getItemCount() {
        return mSelectedProfiles.size();
    }

    public synchronized void addProfile(DipProfile newProfile){
        mSelectedProfiles.add(newProfile);
        mProfileSelectedHelper.returnSelectedToTop();
        notifyItemInserted(0);
    }

    public void removeProfile(int position){
        mSelectedProfiles.remove(position);
        mProfileSelectedHelper.slideSelectedToPosition(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mTitleView;
        private TextView mDescriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.avail_profile_title);
            mDescriptionView = (TextView) itemView.findViewById(R.id.avail_profile_desc);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("AVAIL_CLICK", "Clicked a View");
        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }

    public interface ProfileSelectedHelper {
        void returnSelectedToTop();
        void slideSelectedToPosition(int position);
    }
}
