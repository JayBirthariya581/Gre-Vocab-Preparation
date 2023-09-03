package com.gre.prep.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gre.prep.Activities.WordListActivity;
import com.gre.prep.Models.GroupModel;
import com.gre.prep.R;
import com.gre.prep.databinding.GridCardBinding;

import java.util.List;

public class AtoZAdapter extends RecyclerView.Adapter<AtoZAdapter.MyViewHolder> {
    Context context;
    List<GroupModel> list;
    int selectedPosition;
    Boolean multiSelect;
    ClickListener clickListener;


    public AtoZAdapter(Context context, List<GroupModel> list,Boolean multiSelect) {
        this.context = context;
        this.list = list;
        selectedPosition = -1;
        this.multiSelect = multiSelect;

    }

    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public interface ClickListener{

        void onClick(GroupModel group);

    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AtoZAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.grid_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GroupModel c = list.get(position);

        holder.binding.name.setText(c.getGroupName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                clickListener.onClick(c);

            }
        });

        if (multiSelect){

            if(c.getSelected()){
                holder.binding.getRoot().setBackgroundResource(R.drawable.green_curved_bg);
            } else holder.binding.getRoot().setBackgroundResource(R.drawable.blue_curved_bg);

        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        GridCardBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GridCardBinding.bind(itemView);
        }
    }
}
