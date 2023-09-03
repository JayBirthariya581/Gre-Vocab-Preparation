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
import com.gre.prep.R;
import com.gre.prep.databinding.WordCard3Binding;


import java.util.List;

public class RevisionWordListAdapter extends RecyclerView.Adapter<RevisionWordListAdapter.MyViewHolder> {

    Context context;
    List<WordModel> wordList;
    int selectedPosition;

    public RevisionWordListAdapter(Context context, List<WordModel> wordList) {
        this.context = context;
        this.wordList = wordList;
        selectedPosition = -1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RevisionWordListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.word_card_3, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WordModel word = wordList.get(position);
        holder.binding.position.setText(String.valueOf(position+1));
        holder.binding.word.setText(word.getWord());
        //holder.binding.type.setText(word.getType());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        holder.binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, CategoryListActivity.class)
                        .putExtra("purpose", "Add")
                        .putExtra("wordDetails",word));
            }
        });

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
