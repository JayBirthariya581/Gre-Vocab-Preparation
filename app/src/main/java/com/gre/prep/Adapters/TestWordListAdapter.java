package com.gre.prep.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gre.prep.Activities.CategoryListActivity;
import com.gre.prep.Activities.DictionaryActivity;
import com.gre.prep.Models.WordModel;
import com.gre.prep.Models.WordModel3;
import com.gre.prep.R;
import com.gre.prep.databinding.WordCard3Binding;


import java.util.ArrayList;

public class TestWordListAdapter extends RecyclerView.Adapter<TestWordListAdapter.MyViewHolder> {

    Context context;
    ArrayList<WordModel3> wordList;
    int selectedPosition;
    CheckListener checkListener;

    public TestWordListAdapter(Context context, ArrayList<WordModel3> wordList) {
        this.context = context;
        this.wordList = wordList;
        selectedPosition = -1;
    }

    public CheckListener getCheckListener() {
        return checkListener;
    }

    public void setCheckListener(CheckListener checkListener) {
        this.checkListener = checkListener;
    }

    public interface CheckListener {
        void onCheck(boolean state);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TestWordListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.word_card_3, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WordModel3 word = wordList.get(position);
        holder.binding.position.setText(String.valueOf(position+1));
        holder.binding.word.setText(word.getWord());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (word.isCorrect()) {
                    word.setCorrect(false);
                    checkListener.onCheck(false);
                } else {
                    word.setCorrect(true);
                    checkListener.onCheck(true);
                }
                notifyDataSetChanged();

            }
        };

        holder.itemView.setOnClickListener(onClickListener);
        holder.binding.word.setOnClickListener(onClickListener);

        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectedPosition = position;
                /*String googleTranslateAppPackage = "com.google.android.apps.translate";

                if (isPackageInstalled(googleTranslateAppPackage)) {
                    Uri uri = Uri.parse("https://translate.google.com/?q=" + word);
                    Intent translateIntent = new Intent(Intent.ACTION_VIEW, uri);
                    translateIntent.setPackage(googleTranslateAppPackage);
                    context.startActivity(translateIntent);
                }else{
                    String googleSearchUrl = "https://www.google.com/search?q=define:" + word.getWord().trim();
                    Uri uri = Uri.parse(googleSearchUrl);

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }*/


                context.startActivity(new Intent(context, DictionaryActivity.class)
                        .putExtra("word", word.getWord()));

                return true;
            }
        };


        holder.binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordModel w = new WordModel();
                w.setWord(word.getWord());
                w.setWordID(word.getWordID());
                context.startActivity(new Intent(context, CategoryListActivity.class)
                        .putExtra("purpose", "Add")
                        .putExtra("wordDetails",w));
            }
        });

        holder.itemView.setOnLongClickListener(onLongClickListener);
        holder.binding.word.setOnLongClickListener(onLongClickListener);


        if (word.isCorrect()){
            //holder.binding.word.setTextColor(context.getResources().getColor(R.color.cream));
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.green_curved_bg));
        }else{
            //holder.binding.word.setTextColor(context.getResources().getColor(R.color.black));
            holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.blue_curved_bg));
        }



    }


    // Check if a package is installed on the device.
    private boolean isPackageInstalled(String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        WordCard3Binding binding;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = WordCard3Binding.bind(itemView);

        }
    }


}
