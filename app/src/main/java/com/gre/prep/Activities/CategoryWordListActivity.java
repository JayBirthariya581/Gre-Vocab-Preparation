package com.gre.prep.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Adapters.CategoryWordListAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.CategoryModel;
import com.gre.prep.Models.WordModel;
import com.gre.prep.databinding.ActivityCategoryWordListBinding;
import com.gre.prep.databinding.ActivityWordListBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryWordListActivity extends AppCompatActivity {
    ActivityCategoryWordListBinding binding;
    CustomProgressDialog progressDialog;
    CategoryModel categoryDetails;
    DBRefManager dbRefManager;
    CategoryWordListAdapter adapter;
    List<WordModel> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new CustomProgressDialog(CategoryWordListActivity.this);
        categoryDetails = (CategoryModel) getIntent().getSerializableExtra("categoryDetails");

        dbRefManager = new DBRefManager();

        wordList = new ArrayList<>();

        adapter = new CategoryWordListAdapter(CategoryWordListActivity.this, wordList);

        adapter.setCrossListener(new CategoryWordListAdapter.CrossListener() {
            @Override
            public void onCrossed(WordModel word, int selectedPosition) {
                progressDialog.show();
                    dbRefManager.getWordCategoryReference().child(categoryDetails.getCategoryID()).child("wordList").child(word.getWordID())
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    wordList.remove(selectedPosition);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(CategoryWordListActivity.this, "Word removed successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CategoryWordListActivity.this, "Something went wrong try gain.", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

            }
        });

        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(CategoryWordListActivity.this));
        binding.rv.setAdapter(adapter);

        makeList();
    }

    private void makeList() {
        progressDialog.show();
        Log.i("checkWithLog", categoryDetails.getCategoryID());
        dbRefManager.getWordCategoryReference().child(categoryDetails.getCategoryID()).child("wordList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoryWordListSnap) {

                if (categoryWordListSnap.exists()) {

                    for (DataSnapshot wordSnap : categoryWordListSnap.getChildren()) {

                        WordModel wm = new WordModel();
                        wm.setWordID(wordSnap.getKey());
                        wm.setWord(wordSnap.getValue(String.class));
                        wordList.add(wm);
                    }

                    binding.heading.setText(String.valueOf(wordList.size()) + " Word(s) in Category");
                    Collections.sort(wordList, (a, b) -> a.getWord().compareTo(b.getWord()));
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CategoryWordListActivity.this, "No words in following category", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}