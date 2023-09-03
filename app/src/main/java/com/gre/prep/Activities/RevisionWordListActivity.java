package com.gre.prep.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Adapters.RevisionWordListAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.GroupModel;
import com.gre.prep.Models.WordModel;
import com.gre.prep.Models.WordModel3;
import com.gre.prep.databinding.ActivityRevisionWordListBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RevisionWordListActivity extends AppCompatActivity {
    ActivityRevisionWordListBinding binding;
    CustomProgressDialog progressDialog;

    DBRefManager dbRefManager;
    RevisionWordListAdapter adapter;
    List<WordModel> wordList;
    ArrayList<WordModel3> testList;
    String revisionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRevisionWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressDialog = new CustomProgressDialog(RevisionWordListActivity.this);
        revisionCode = (String) getIntent().getSerializableExtra("revisionCode");


        dbRefManager = new DBRefManager();

        wordList = new ArrayList<>();
        testList = new ArrayList<>();

        adapter = new RevisionWordListAdapter(RevisionWordListActivity.this, wordList);


        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(RevisionWordListActivity.this));
        binding.rv.setAdapter(adapter);

        binding.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wordList.size() > 0) {

                    progressDialog.show();
                    testList.clear();
                    for (WordModel wm : wordList) {

                        WordModel3 wm3 = new WordModel3();
                        wm3.setCorrect(false);
                        wm3.setWord(wm.getWord());
                        wm3.setWordID(wm.getWordID());

                        testList.add(wm3);

                    }
                    progressDialog.dismiss();
                    startActivity(new Intent(RevisionWordListActivity.this, TestActivity.class)
                            .putExtra("testType", "Revision Test")
                            .putExtra("limit", String.valueOf(testList.size()))
                            .putExtra("wordList", testList));
                } else {
                    Toast.makeText(RevisionWordListActivity.this, "Revision List is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        makeList();
    }

    private void makeList() {
        progressDialog.show();
        dbRefManager.getRevisionDatabase().child(revisionCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categoryWordListSnap) {

                if (categoryWordListSnap.exists()) {

                    for (DataSnapshot wordSnap : categoryWordListSnap.getChildren()) {

                        WordModel wm = new WordModel();
                        wm.setWordID(wordSnap.getKey());
                        wm.setWord(wordSnap.getValue(String.class));
                        wordList.add(wm);
                    }


                    Log.i("checkWithLog", String.valueOf(wordList.size()));
                    binding.heading.setText(String.valueOf(wordList.size()) + " Revision Words");

                    Collections.sort(wordList, (a, b) -> a.getWord().compareTo(b.getWord()));


                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RevisionWordListActivity.this, "No words in following category", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}