package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectedProfilesAdapter extends RecyclerView.Adapter<SelectedProfilesAdapter.ViewHolder> implements ItemTouchHelperAdapter{

    private List<DipProfile> mSelectedProfiles;
    private ProfileSelectedHelper mProfileSelectedHelper;

    public SelectedProfilesAdapter(ProfileSelectedHelper selectedHelper) {
        mProfileSelectedHelper = selectedHelper;
        mSelectedProfiles = new ArrayList<>();
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

    public void addProfile(DipProfile newProfile){
        mSelectedProfiles.add(newProfile);
        mProfileSelectedHelper.returnSelectedToTop();
        notifyItemInserted(mSelectedProfiles.size() - 1);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mSelectedProfiles, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mSelectedProfiles, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position){
        mSelectedProfiles.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleView;
        private TextView mDescriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.avail_profile_title);
            mDescriptionView = (TextView) itemView.findViewById(R.id.avail_profile_desc);

        }
    }

    public interface ProfileSelectedHelper {
        void returnSelectedToTop();
        void slideSelectedToPosition(int position);
    }
}
