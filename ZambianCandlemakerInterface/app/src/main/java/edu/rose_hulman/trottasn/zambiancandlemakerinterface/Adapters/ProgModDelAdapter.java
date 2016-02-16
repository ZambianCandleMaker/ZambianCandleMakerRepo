package edu.rose_hulman.trottasn.zambiancandlemakerinterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.trottasn.zambiancandlemakerinterface.Models.DipProgram;
import edu.rose_hulman.trottasn.zambiancandlemakerinterface.R;

public class ProgModDelAdapter extends RecyclerView.Adapter<ProgModDelAdapter.ViewHolder> {

    private List<DipProgram> mPrograms;
    private ProgramEditFragment mProgEditFrag;

    public ProgModDelAdapter(ProgramEditFragment progEditFrag) {
        mProgEditFrag = progEditFrag;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleView;
        private TextView mDescriptionView;
        private Button mDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.saved_program_title);
            mDescriptionView = (TextView) itemView.findViewById(R.id.saved_program_description);
            mDeleteButton = (Button) itemView.findViewById(R.id.del_button);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPos = getAdapterPosition();
                    DipProgram dProg = mPrograms.get(adapterPos);
                    mProgEditFrag.onProgramDeleted(dProg, adapterPos);
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mProgEditFrag.switchToEditing(mPrograms.get(getAdapterPosition()));
        }
    }
}