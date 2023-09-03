package com.gre.prep.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Adapters.TestWordListAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Models.WordModel3;
import com.gre.prep.databinding.ActivityTestBinding;
import com.gre.prep.databinding.CardAddMeaningBinding;
import com.gre.prep.databinding.CardRevisionCreateBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    CustomProgressDialog progressDialog;

    DBRefManager dbRefManager;
    TestWordListAdapter adapter;
    ArrayList<WordModel3> wordList;
    ArrayList<WordModel3> receivedList;
    Integer limit;
    Integer correct;
    Dialog revisionDialog;
    HashMap<String, String> revisionMap;
    String testType;
    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_PRESS_INTERVAL = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        testType = getIntent().getStringExtra("testType");
        receivedList = (ArrayList) getIntent().getSerializableExtra("wordList");
        limit = Integer.valueOf((String) getIntent().getSerializableExtra("limit"));
        correct = 0;

        CardRevisionCreateBinding rcb = CardRevisionCreateBinding.inflate(getLayoutInflater());
        revisionDialog = new Dialog(TestActivity.this);
        revisionDialog.setContentView(rcb.getRoot());

        revisionMap = new HashMap<>();
        dbRefManager = new DBRefManager();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(revisionDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        revisionDialog.getWindow().setAttributes(lp);

        rcb.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String revisionCode = rcb.revisionCode.getText().toString();
                if (revisionCode.isEmpty() || revisionCode.length() < 1) {
                    Toast.makeText(TestActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (revisionCode.contains("NEWWORDS") || revisionCode.contains("OLDWORDS") || revisionCode.contains("TESTWORDS")) {
                    Toast.makeText(TestActivity.this, "Choose other name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                revisionDialog.dismiss();
                progressDialog.show();
                revisionMap.clear();

                dbRefManager.getRevisionDatabase().child(revisionCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot revisionListSnap) {

                        if (revisionListSnap.exists()) {

                            for (DataSnapshot wordSnap : revisionListSnap.getChildren()) {

                                revisionMap.put(wordSnap.getKey(), wordSnap.getValue(String.class));

                            }

                        }
                        addToRevisionMap(revisionCode);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        rcb.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String revisionCode = rcb.revisionCode.getText().toString().trim();
                if (revisionCode.isEmpty() || revisionCode.length() < 1) {
                    Toast.makeText(TestActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (revisionCode.contains("NEWWORDS") || revisionCode.contains("OLDWORDS") || revisionCode.contains("TESTWORDS")) {
                    Toast.makeText(TestActivity.this, "Choose other name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                revisionDialog.dismiss();
                progressDialog.show();
                revisionMap.clear();

                addToRevisionMap(revisionCode);

            }
        });


        binding.total.setText("/" + limit.toString());

        progressDialog = new CustomProgressDialog(TestActivity.this);


        wordList = new ArrayList<>();

        adapter = new TestWordListAdapter(TestActivity.this, wordList);

        adapter.setCheckListener(new TestWordListAdapter.CheckListener() {
            @Override
            public void onCheck(boolean state) {

                if (state) {
                    correct += 1;
                } else {
                    correct -= 1;
                }
                binding.correct.setText(correct.toString());
            }
        });




        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(TestActivity.this));
        binding.rv.setAdapter(adapter);

        getRandomElements();

        binding.revision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revisionDialog.show();


            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "My score in "+testType+" : "+correct.toString()+"/"+String.valueOf(wordList.size()));

                // Create a chooser to let the user pick which app to share with
                Intent chooser = Intent.createChooser(shareIntent, "Share via");

                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
            }
        });

    }


    public void addToRevisionMap(String revisionCode) {
        progressDialog.show();

        if (wordList.size() < 1) {
            Toast.makeText(this, "No word list.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }


        for (WordModel3 w : wordList) {

            if (!w.isCorrect()) {

                revisionMap.put(w.getWordID(), w.getWord());

            }

        }


        dbRefManager.getRevisionDatabase().child(revisionCode).setValue(revisionMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(TestActivity.this, "Revision list created successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestActivity.this, "Something went wrong try gain.", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit test.", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, BACK_PRESS_INTERVAL);
    }

    public void getRandomElements() {
        progressDialog.show();
        wordList.clear();

        if (receivedList.size() <= limit) {
            // If the original list size is less than or equal to the limit, return a shuffled copy of the original list.
            wordList.addAll(receivedList);
            Collections.shuffle(wordList);
        } else {
            // If the original list size is greater than the limit, select random elements from the list.
            wordList.clear();
            Random random = new Random();

            for (int i = 0; i < limit; i++) {
                int randomIndex = random.nextInt(receivedList.size());
                wordList.add(receivedList.get(randomIndex));
            }

        }

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}