package com.gre.prep.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gre.prep.Models.MeaningModel;
import com.gre.prep.R;
import com.gre.prep.databinding.CardMeaningBinding;

import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.MyViewHolder> {

    Context context;
    List<MeaningModel> meaningList;
    int selectedPosition;

    public MeaningAdapter(Context context, List<MeaningModel> meaningList) {
        this.context = context;
        this.meaningList = meaningList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MeaningAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_meaning, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MeaningModel meaning = meaningList.get(position);

        holder.binding.meaning.setText(meaning.getMeaning());



    }

    @Override
    public int getItemCount() {
        return meaningList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        CardMeaningBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardMeaningBinding.bind(itemView);
        }
    }

}
