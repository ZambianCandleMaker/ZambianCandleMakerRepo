package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProfile;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.TimePosPair;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

/**
 * Created by Jake on 2/9/16.
 */
public class EditProfileAdapter extends RecyclerView.Adapter<EditProfileAdapter.ViewHolder> {

    private DipProfile mProfile;
    private Context mContext;


    private List<TimePosPair> mPoints;

    public EditProfileAdapter(Context context, DipProfile profile){
        this.mProfile = profile;
        this.mContext = context;
        mPoints = profile.getPairList();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TimePosPair currentPair = mPoints.get(position);
        holder.timeView.setText(Integer.toString(currentPair.getTime()));
        holder.depthView.setText(Integer.toString(currentPair.getPosition()));

    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mPoints.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        private TextView timeView;
        private TextView depthView;

        public ViewHolder(View itemView) {
            super(itemView);
            timeView  = (TextView) itemView.findViewById(R.id.profile_point_time);
            depthView = (TextView) itemView.findViewById(R.id.profile_point_depth);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
//            timeText.setText(Integer.parseInt(timeView.getText().toString()));
//            depthText.setText(Integer.parseInt(depthView.getText().toString()));
//            Toast.makeText(ViewHolder.this, , Toast.LENGTH_SHORT).show();
        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {

            return false;
        }
    }
}
