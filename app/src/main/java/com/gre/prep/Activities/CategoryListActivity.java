package com.gre.prep.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Adapters.CategoryAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.CategoryModel;
import com.gre.prep.Models.GroupModel;
import com.gre.prep.Models.WordModel;
import com.gre.prep.databinding.ActivityCategoryListBinding;
import com.gre.prep.databinding.CardAddCategoryBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryListActivity extends AppCompatActivity {
    ActivityCategoryListBinding binding;
    DBRefManager dbRefManager;
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryList,filterList;
    CustomProgressDialog progressDialog;
    Dialog addCategoryDialog;
    String purpose;
    WordModel givenWord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        purpose = getIntent().getStringExtra("purpose");


        progressDialog = new CustomProgressDialog(CategoryListActivity.this);

        categoryList = new ArrayList<>();
        filterList = new ArrayList<>();

        if (purpose.equals("Add")) {
            givenWord = (WordModel) getIntent().getSerializableExtra("wordDetails");
            categoryAdapter = new CategoryAdapter(CategoryListActivity.this, categoryList, purpose, givenWord);
        }else if(purpose.equals("Test")){
            categoryAdapter = new CategoryAdapter(CategoryListActivity.this, categoryList, purpose);
        } else {
            categoryAdapter = new CategoryAdapter(CategoryListActivity.this, categoryList, purpose);
        }


        binding.rv.setLayoutManager(new LinearLayoutManager(CategoryListActivity.this));
        binding.rv.setAdapter(categoryAdapter);
        binding.rv.setHasFixedSize(true);

        dbRefManager = new DBRefManager();

        CardAddCategoryBinding acb = CardAddCategoryBinding.inflate(getLayoutInflater());
        addCategoryDialog = new Dialog(CategoryListActivity.this);
        addCategoryDialog.setContentView(acb.getRoot());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addCategoryDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        addCategoryDialog.getWindow().setAttributes(lp);

        acb.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category = acb.category.getText().toString();

                if (category.isEmpty() || category.length() < 1) {
                    Toast.makeText(CategoryListActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }

                addCategoryDialog.dismiss();
                progressDialog.show();

                CategoryModel categoryModel = new CategoryModel();

                categoryModel.setCategoryID(dbRefManager.getWordCategoryReference().push().getKey());
                categoryModel.setCategory(category);

                dbRefManager.getWordCategoryReference().child(categoryModel.getCategoryID()).setValue(categoryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            categoryList.add(categoryModel);
                            categoryAdapter.notifyDataSetChanged();
                            Toast.makeText(CategoryListActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CategoryListActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

            }
        });


        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addCategoryDialog.show();

            }
        });

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterList(s.toString());
            }
        });


        makeList();

    }

    private void filterList(String newText) {

        progressDialog.show();

        filterList = new ArrayList<>();

        for (CategoryModel c : categoryList) {
            String cat = c.getCategory().toLowerCase();

            if (cat.contains(newText.toLowerCase())) {

                filterList.add(c);

            }

        }



        categoryAdapter.filter(filterList);

        progressDialog.dismiss();
    }

    private void makeList() {
        progressDialog.show();

        dbRefManager.getWordCategoryReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoriesSnap) {

                if (categoriesSnap.exists()) {
                    Log.i("checkWithLog", "hai");


                    for (DataSnapshot categorySnap : categoriesSnap.getChildren()) {

                        CategoryModel categoryModel = new CategoryModel();

                        categoryModel.setCategory(categorySnap.child("category").getValue(String.class));
                        categoryModel.setCategoryID(categorySnap.child("categoryID").getValue(String.class));

                        /*if (categorySnap.child("wordList").exists()) {
                            List<WordModel> wordList = new ArrayList<>();

                            for (DataSnapshot wordSnap : categorySnap.child("categoryID").getChildren()) {

                                WordModel word = new WordModel();
                                word.setWord(wordSnap.getValue(String.class));
                                word.setWordID(wordSnap.getValue(String.class));

                                wordList.add(word);
                            }


                        }*/

                        categoryList.add(categoryModel);
                    }

                    Collections.sort(categoryList,(a,b)->a.getCategory().compareTo(b.getCategory()));

                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CategoryListActivity.this, "No categories exist", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}