package com.gre.prep.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gre.prep.Models.DictionaryModel;
import com.gre.prep.R;
import com.gre.prep.databinding.DictionaryCardBinding;

import java.util.List;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.MyViewHolder> {

    Context context;
    List<DictionaryModel> dictionaries;
    int selectedPosition;
    DictionarySelectedListener dictionarySelectedListener;

    public DictionaryAdapter(Context context, List<DictionaryModel> dictionaries) {
        this.context = context;
        this.dictionaries = dictionaries;
        selectedPosition = -1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DictionaryAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.dictionary_card,parent,false));
    }

    public interface DictionarySelectedListener{

        void onDictionarySelected(DictionaryModel dictionary,int position);

    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public DictionarySelectedListener getDictionarySelectedListener() {
        return dictionarySelectedListener;
    }

    public void setDictionarySelectedListener(DictionarySelectedListener dictionarySelectedListener) {
        this.dictionarySelectedListener = dictionarySelectedListener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DictionaryModel dictionary = dictionaries.get(position);

        holder.binding.btn.setText(dictionary.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
                dictionarySelectedListener.onDictionarySelected(dictionary,position);
            }
        });

        if(position==selectedPosition) {
            holder.binding.btn.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.btn.setBackgroundColor(context.getResources().getColor(R.color.blue));
        } else {
            holder.binding.btn.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.btn.setBackgroundColor(context.getResources().getColor(R.color.cream_2));
        }
    }


    @Override
    public int getItemCount() {
        return dictionaries.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        DictionaryCardBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DictionaryCardBinding.bind(itemView);
        }
    }
}
