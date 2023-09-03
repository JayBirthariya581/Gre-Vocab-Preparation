package com.gre.prep.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gre.prep.Activities.CategoryListActivity;
import com.gre.prep.Activities.CategoryWordListActivity;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.CategoryModel;
import com.gre.prep.Models.WordModel;
import com.gre.prep.R;
import com.gre.prep.databinding.CategoryCardBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    Context context;
    List<CategoryModel> categoryList;
    String purpose;
    WordModel givenWord;
    int selectedPosition;
    DBRefManager dbRefManager;
    CustomProgressDialog progressDialog;

    public CategoryAdapter(Context context, List<CategoryModel> categoryList, String purpose) {
        this.context = context;
        this.purpose = purpose;
        this.categoryList = categoryList;
        selectedPosition = -1;
        progressDialog = new CustomProgressDialog(context);
        dbRefManager = new DBRefManager();
    }

    public CategoryAdapter(Context context, List<CategoryModel> categoryList, String purpose, WordModel givenWord) {
        this.context = context;
        this.categoryList = categoryList;
        this.givenWord = givenWord;
        this.purpose = purpose;
        selectedPosition = -1;
        progressDialog = new CustomProgressDialog(context);
        dbRefManager = new DBRefManager();
    }

    public void filter(List<CategoryModel> filterList) {

        categoryList = filterList;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.category_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);
        holder.binding.name.setText(category.getCategory());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;

                if (purpose.equals("Add")) {
                    progressDialog.show();
                        dbRefManager.getWordCategoryReference().child(category.getCategoryID()).child("wordList")
                            .child(givenWord.getWordID()).setValue(givenWord.getWord()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Word add to category successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });


                } else if (purpose.equals("Test")) {
                    Intent data = new Intent();
                    data.putExtra("categoryDetails", category);
                    ((Activity) context).setResult(Activity.RESULT_OK, data);
                    ((Activity) context).finish();
                } else {

                    context.startActivity(new Intent(context, CategoryWordListActivity.class).putExtra("categoryDetails", category));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CategoryCardBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CategoryCardBinding.bind(itemView);
        }
    }


}
