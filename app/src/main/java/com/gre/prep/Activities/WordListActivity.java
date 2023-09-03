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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Adapters.WordListAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.GroupModel;
import com.gre.prep.Models.MeaningModel;
import com.gre.prep.Models.WordModel;
import com.gre.prep.Models.WordModel3;
import com.gre.prep.databinding.ActivityWordListBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordListActivity extends AppCompatActivity {

    ActivityWordListBinding binding;
    DBRefManager dbRefManager;
    GroupModel group;
    List<WordModel> wordList;
    ArrayList<WordModel3> testList;
    WordListAdapter adapter;
    CustomProgressDialog progressDialog;
    String DBType;
    DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        group = (GroupModel) getIntent().getSerializableExtra("Category");
        DBType = getIntent().getStringExtra("DB");

        progressDialog = new CustomProgressDialog(WordListActivity.this);
        dbRefManager = new DBRefManager();


        wordList = new ArrayList<>();
        testList = new ArrayList<>();

        adapter = new WordListAdapter(WordListActivity.this, wordList);


        binding.rv.setLayoutManager(new LinearLayoutManager(WordListActivity.this));
        binding.rv.setHasFixedSize(true);
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
                    startActivity(new Intent(WordListActivity.this, TestActivity.class)
                            .putExtra("testType", "Test for "+group.getGroupName()+" Group(s)")
                            .putExtra("limit", String.valueOf(testList.size()))
                            .putExtra("wordList", testList));
                } else {
                    Toast.makeText(WordListActivity.this, "Revision List is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        binding.addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WordListActivity.this, AddWordActivity.class)
                        .putExtra("purpose", "Add"));
            }
        });

        makeList();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        makeList();
//    }

    private void makeList() {
        progressDialog.show();
        wordList.clear();
        Log.i("checkWithLog", group.getGroupID());

        if(DBType!=null && DBType.equals("New")) dbRef = dbRefManager.getNewWordDatabaseReference();
        else if(DBType!=null && DBType.equals("Combined")) dbRef =dbRefManager.getCombinedWordDatabaseReference();
        else dbRef = dbRefManager.getWordDatabaseReference();

        dbRef.child(group.getGroupID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot wordListSnap) {

                if (wordListSnap.exists()) {
                    binding.heading.setText(group.getGroupName() + " Words " + "(" + String.valueOf(wordListSnap.getChildrenCount()) + ")");
                    Log.i("checkWithLog", "hai");
                    for (DataSnapshot wordSnap : wordListSnap.getChildren()) {

                        WordModel wordModel = new WordModel();

                        if (wordSnap.child("word").exists())
                            wordModel.setWord(wordSnap.child("word").getValue(String.class));
                        if (wordSnap.child("wordID").exists())
                            wordModel.setWordID(wordSnap.child("wordID").getValue(String.class));
                        if (wordSnap.child("image").exists())
                            wordModel.setImage(wordSnap.child("image").getValue(String.class));
                        if (wordSnap.child("note").exists())
                            wordModel.setNote(wordSnap.child("note").getValue(String.class));
                        if (wordSnap.child("type").exists())
                            wordModel.setType(wordSnap.child("type").getValue(String.class));

                        if (wordSnap.child("meaningList").exists()) {

                            List<MeaningModel> meaningList = new ArrayList<>();

                            for (DataSnapshot meaningSnap : wordSnap.child("meaningList").getChildren()) {

                                MeaningModel meaningModel = new MeaningModel();
                                meaningModel.setMeaningID(meaningSnap.getKey());
                                meaningModel.setMeaning(meaningSnap.getValue(String.class));
                                meaningList.add(meaningModel);
                            }

                            wordModel.setMeaningList(meaningList);

                        }

                        if (wordSnap.child("categoryList").exists()) {
                            List<GroupModel> categoryList = new ArrayList<>();

                            for (DataSnapshot categorySnap : wordSnap.child("categoryList").getChildren()) {

                                GroupModel groupModel = new GroupModel();
                                groupModel.setGroupID(categorySnap.getKey());
                                groupModel.setGroupName(categorySnap.getValue(String.class));
                                categoryList.add(groupModel);

                            }

                            wordModel.setCategoryList(categoryList);
                        }

                        wordList.add(wordModel);
                    }

                    Collections.sort(wordList, (a, b) -> a.getWord().compareTo(b.getWord()));

                    Log.i("checkWithLog", String.valueOf(wordList.size()));
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(WordListActivity.this, "No words in this group.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}