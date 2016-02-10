package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class ProgModDelAdapter extends RecyclerView.Adapter<ProgModDelAdapter.ViewHolder> {

    private List<DipProgram> mPrograms;

    public ProgModDelAdapter() {
        mPrograms = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_program_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DipProgram dipProgram = mPrograms.get(position);
        String title = dipProgram.getTitle();
        String description = dipProgram.getDescription();
        holder.mTitleView.setText(title);
        holder.mDescriptionView.setText(description);
    }

    @Override
    public int getItemCount() {
        return mPrograms.size();
    }

    public void addProgram(DipProgram newProgram){
        mPrograms.add(newProgram);
        notifyItemInserted(mPrograms.size() - 1);
    }

    public void clear(){
        mPrograms.clear();
        notifyDataSetChanged();
    }

    public void removeProgram(int position){
        mPrograms.remove(position);
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
}