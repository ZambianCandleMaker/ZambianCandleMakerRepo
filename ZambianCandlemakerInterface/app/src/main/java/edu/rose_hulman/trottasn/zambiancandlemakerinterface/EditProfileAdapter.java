package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Jake on 2/9/16.
 */
public class EditProfileAdapter extends RecyclerView.Adapter<EditProfileAdapter.ViewHolder> {

    private DipProfile profile;

    public EditProfileAdapter(DipProfile profile){
        this.profile = profile;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return profile.getPairList().size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView timeText;
        private TextView depthText;

        public ViewHolder(View itemView) {
            super(itemView);
            timeText  = (TextView) itemView.findViewById(R.id.profile_point_time);
            depthText = (TextView) itemView.findViewById(R.id.profile_point_depth);
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {

        }
    }
}
