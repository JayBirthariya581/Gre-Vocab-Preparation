package com.gre.prep.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.CategoryModel;
import com.gre.prep.Models.GroupModel;
import com.gre.prep.Models.WordModel3;
import com.gre.prep.databinding.ActivityTestMenuBinding;
import com.gre.prep.databinding.CardRevisionAccessBinding;
import com.gre.prep.databinding.CardWordLimitBinding;

import java.util.ArrayList;

public class TestMenuActivity extends AppCompatActivity {
    ActivityTestMenuBinding binding;
    CustomProgressDialog progressDialog;
    DBRefManager dbRefManager;
    Dialog limitDialog;
    Dialog revisionDialog;
    ArrayList<WordModel3> wordList;
    Integer limit;
    ActivityResultLauncher<Intent> groupTestLauncher,categoricalTestLauncher;
    DataSnapshot wordDatabaseSnap;
    String testType;
    DatabaseReference dbRef;
    String DBType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DBType = getIntent().getStringExtra("DB");
        Log.i("checkWithLog",DBType);
        progressDialog = new CustomProgressDialog(TestMenuActivity.this);

        dbRefManager = new DBRefManager();

        limit = 0;

        progressDialog.show();

        wordList = new ArrayList<>();

        CardWordLimitBinding lb = CardWordLimitBinding.inflate(getLayoutInflater());
        limitDialog = new Dialog(TestMenuActivity.this);
        limitDialog.setContentView(lb.getRoot());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(limitDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        limitDialog.getWindow().setAttributes(lp);

        lb.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredLimit = lb.limit.getText().toString();

                if (!enteredLimit.matches("\\d+")) {
                    Toast.makeText(TestMenuActivity.this, "Invalid Input.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer lim = Integer.valueOf(enteredLimit);

                if (lim > limit || lim <0) {
                    Toast.makeText(TestMenuActivity.this, "Enter within limit.", Toast.LENGTH_SHORT).show();
                    return;
                }


                limitDialog.dismiss();
                startActivity(new Intent(TestMenuActivity.this, TestActivity.class)
                        .putExtra("testType",testType)
                        .putExtra("limit", enteredLimit)
                        .putExtra("wordList", wordList));

            }
        });

        lb.all.setVisibility(View.VISIBLE);

        lb.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (limit > limit || limit <0) {
                    Toast.makeText(TestMenuActivity.this, "Invalid limit. Try Again.", Toast.LENGTH_SHORT).show();
                    return;
                }


                limitDialog.dismiss();
                startActivity(new Intent(TestMenuActivity.this, TestActivity.class)
                        .putExtra("testType",testType)
                        .putExtra("limit", limit.toString())
                        .putExtra("wordList", wordList));
            }
        });


        // Revision Dialog ===============================


        CardRevisionAccessBinding rab = CardRevisionAccessBinding.inflate(getLayoutInflater());
        revisionDialog = new Dialog(TestMenuActivity.this);
        revisionDialog.setContentView(rab.getRoot());


        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(revisionDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;

        revisionDialog.getWindow().setAttributes(lp2);

        rab.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String revisionCode = rab.revisionCode.getText().toString();
                if (revisionCode.isEmpty() || revisionCode.length() < 1) {
                    Toast.makeText(TestMenuActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                    return;
                }
                revisionDialog.dismiss();
                progressDialog.show();

                dbRefManager.getRevisionDatabase().child(revisionCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot revisionListSnap) {

                        if(revisionListSnap.exists()){
                            wordList.clear();
                            for(DataSnapshot wordSnap : revisionListSnap.getChildren()){

                                WordModel3 wm = new WordModel3();
                                wm.setCorrect(false);
                                wm.setWordID(wordSnap.getKey());
                                wm.setWord(wordSnap.getValue(String.class));

                                wordList.add(wm);
                            }


                            limit = wordList.size();

                            if(limit<1){
                                Toast.makeText(TestMenuActivity.this, "No words found for selected category", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                            lb.head.setText("Total " + String.valueOf(limit) + " words found. Enter Limit within it");
                            lb.limit.setText("");
                            progressDialog.dismiss();
                            limitDialog.show();
                        }else{
                            Toast.makeText(TestMenuActivity.this, "No revision list found for given code", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });



        // Revision Dialog ===============================


        groupTestLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    progressDialog.show();
                    Intent data = result.getData();


                    ArrayList<GroupModel> selectedGroups = (ArrayList) data.getSerializableExtra("groupDetails");

                    progressDialog.show();
                    wordList.clear();
                    StringBuilder sb = new StringBuilder("Test for ");
                    boolean commaFlag=true;
                    for(GroupModel groupModel : selectedGroups){


                        if (groupModel.getSelected() && wordDatabaseSnap.child(groupModel.getGroupID()).exists()) {

                            if(commaFlag){
                                commaFlag = false;
                            }else{
                                sb.append(", ");
                            }

                            for (DataSnapshot wordSnap : wordDatabaseSnap.child(groupModel.getGroupID()).getChildren()) {
                                wordList.add(wordSnap.getValue(WordModel3.class));
                            }

                            sb.append(groupModel.getGroupName());

                        }
                    }
                    sb.append(" Group(s)");
                    testType = sb.toString();



                    limit = wordList.size();

                    if(limit<1){
                        Toast.makeText(TestMenuActivity.this, "No words found for selected groups", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }

                    lb.head.setText("Total " + String.valueOf(limit) + " words found. Enter Limit within it");
                    lb.limit.setText("");
                    progressDialog.dismiss();
                    limitDialog.show();



                }
            }
        });


        categoricalTestLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    progressDialog.show();
                    Intent data = result.getData();


                   CategoryModel selectedCategory = (CategoryModel) data.getSerializableExtra("categoryDetails");

                    progressDialog.show();
                    wordList.clear();

                    dbRefManager.getWordCategoryReference().child(selectedCategory.getCategoryID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot categorySnap) {

                            if(categorySnap.exists()){
                                wordList.clear();
                                for(DataSnapshot wordSnap : categorySnap.child("wordList").getChildren()){

                                    WordModel3 wm = new WordModel3();
                                    wm.setCorrect(false);
                                    wm.setWordID(wordSnap.getKey());
                                    wm.setWord(wordSnap.getValue(String.class));

                                    wordList.add(wm);
                                }
                                testType = "Test for "+selectedCategory.getCategory().trim()+" Category";

                                limit = wordList.size();

                                if(limit<1){
                                    Toast.makeText(TestMenuActivity.this, "No words found for selected category", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    return;
                                }

                                lb.head.setText("Total " + String.valueOf(limit) + " words found. Enter Limit within it");
                                lb.limit.setText("");
                                progressDialog.dismiss();
                                limitDialog.show();

                            }else{
                                Toast.makeText(TestMenuActivity.this, "No words found for selected category", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });










                }
            }
        });


        if(DBType!=null && DBType.equals("New")) dbRef = dbRefManager.getNewWordDatabaseReference();
        else if(DBType!=null && DBType.equals("Combined")) dbRef =dbRefManager.getCombinedWordDatabaseReference();
        else dbRef = dbRefManager.getWordDatabaseReference();


        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot wordDatabaseSnap) {

                if (wordDatabaseSnap.exists()) {

                    TestMenuActivity.this.wordDatabaseSnap = wordDatabaseSnap;

                    binding.generalTest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            wordList.clear();
                            for (DataSnapshot categorySnap : wordDatabaseSnap.getChildren()) {

                                for (DataSnapshot wordSnap : categorySnap.getChildren()) {


                                    wordList.add(wordSnap.getValue(WordModel3.class));

                                }

                            }

                            testType = "General Test";

                            limit = wordList.size();
                            lb.head.setText("Total " + String.valueOf(limit) + " words found. Enter Limit within it");
                            lb.limit.setText("");
                            progressDialog.dismiss();
                            limitDialog.show();

                        }
                    });

                    binding.random.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            wordList.clear();
                            for (DataSnapshot categorySnap : wordDatabaseSnap.getChildren()) {

                                for (DataSnapshot wordSnap : categorySnap.getChildren()) {


                                    wordList.add(wordSnap.getValue(WordModel3.class));

                                }

                            }

                            testType = "Random-Word Test";

                            limit = wordList.size();
                            progressDialog.dismiss();
                            startActivity(new Intent(TestMenuActivity.this, TestActivity.class)
                                    .putExtra("testType",testType)
                                    .putExtra("limit", "1")
                                    .putExtra("wordList", wordList));
                        }
                    });

                    binding.shuffled.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            wordList.clear();
                            for (DataSnapshot categorySnap : wordDatabaseSnap.getChildren()) {

                                for (DataSnapshot wordSnap : categorySnap.getChildren()) {


                                    wordList.add(wordSnap.getValue(WordModel3.class));

                                }

                            }

                            Log.i("checkWithLog", String.valueOf(wordList.size()));

                            testType = "All-Shuffled Test";

                            limit = wordList.size();
                            progressDialog.dismiss();
                            startActivity(new Intent(TestMenuActivity.this, TestActivity.class)
                                    .putExtra("testType",testType)
                                    .putExtra("limit", limit.toString())
                                    .putExtra("wordList", wordList));
                        }
                    });



                    binding.groupedTest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            groupTestLauncher.launch(new Intent(TestMenuActivity.this, WordDirectoryActivity.class)
                                    .putExtra("purpose", "Test"));
                        }
                    });


                    binding.revision.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            testType = "Revision Test";
                            revisionDialog.show();
                        }
                    });

                    binding.categorical.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            categoricalTestLauncher.launch(new Intent(TestMenuActivity.this, CategoryListActivity.class)
                                    .putExtra("purpose", "Test"));
                        }
                    });


                } else {
                    Toast.makeText(TestMenuActivity.this, "Word Database not found.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}