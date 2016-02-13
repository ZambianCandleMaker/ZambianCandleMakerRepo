package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class SelectedProfilesAdapter extends RecyclerView.Adapter<SelectedProfilesAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<DipProfile> mSelectedProfiles;
    private ProfileSelectedHelper mProfileSelectedHelper;

    public SelectedProfilesAdapter(ProfileSelectedHelper selectedHelper) {
        mProfileSelectedHelper = selectedHelper;
        mSelectedProfiles = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_profile_view, parent, false);
        return new ViewHolder(v);
    }

    public List<String> getTitleList(){
        List<String> titleList = new ArrayList<>();
        for(DipProfile dipProfile : mSelectedProfiles){
            titleList.add(dipProfile.getTitle());
        }
        return titleList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DipProfile dipProfile = mSelectedProfiles.get(position);
        String title = dipProfile.getTitle();
        String description = dipProfile.getDescription();
        holder.mTitleView.setText(title);
//        holder.mDescriptionView.setText(description);
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
        Log.d("GOT_HERE", "got here");
        if((toPosition > mSelectedProfiles.size() - 1) || toPosition < 0){
            return;
        }
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
        Log.d("AVAIL_CLICK", "Clicked a View");
        DipProfile lastRemoved = mSelectedProfiles.remove(position);
        notifyItemRemoved(position);
        mProfileSelectedHelper.slideSelectedToPosition(position);
        Log.d("CHECK", lastRemoved.getTitle());
        mProfileSelectedHelper.addToAvailableAdapter(lastRemoved);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitleView;
//        private TextView mDescriptionView;
        private Button mDownButton;
        private Button mUpButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.avail_profile_title);
//            mDescriptionView = (TextView) itemView.findViewById(R.id.avail_profile_desc);
            mDownButton = (Button) itemView.findViewById(R.id.down_button);
            mDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemMove(getAdapterPosition(), getAdapterPosition() + 1);
                }
            });
            mUpButton = (Button) itemView.findViewById(R.id.up_button);
            mUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemMove(getAdapterPosition(), getAdapterPosition() - 1);
                }
            });
        }
    }

    public interface ProfileSelectedHelper {
        void returnSelectedToTop();
        void slideSelectedToPosition(int position);
        void addToAvailableAdapter(DipProfile dipProfile);
    }
}
