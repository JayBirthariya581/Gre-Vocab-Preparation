package com.gre.prep.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.gre.prep.Adapters.MeaningAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.Helper.PathBuilder;
import com.gre.prep.Models.MeaningModel;
import com.gre.prep.Models.WordModel;
import com.gre.prep.Models.WordModel2;
import com.gre.prep.R;
import com.gre.prep.databinding.ActivityAddWordBinding;
import com.gre.prep.databinding.CardAddMeaningBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddWordActivity extends AppCompatActivity {
    ActivityAddWordBinding binding;
    DBRefManager dbRefManager;
    Dialog addMeaningDialog;
    List<MeaningModel> meaningList;
    CustomProgressDialog progressDialog;
    String purpose;
    WordModel wordDetails;
    MeaningAdapter meaningAdapter;
    PathBuilder pathBuilder;
    HashMap<String, Object> updateMap;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        purpose = getIntent().getStringExtra("purpose");
        pathBuilder = new PathBuilder();
        updateMap = new HashMap<>();

        dbRefManager = new DBRefManager();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        meaningList = new ArrayList<>();
        progressDialog = new CustomProgressDialog(AddWordActivity.this);

        CardAddMeaningBinding amb = CardAddMeaningBinding.inflate(getLayoutInflater());
        addMeaningDialog = new Dialog(AddWordActivity.this);
        addMeaningDialog.setContentView(amb.getRoot());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(addMeaningDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        addMeaningDialog.getWindow().setAttributes(lp);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(AddWordActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.type));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.typeSpinner.setAdapter(spinnerAdapter);


        amb.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String meaning = amb.meaning.getText().toString();

                if (meaning.isEmpty() || meaning.length() < 1) {
                    Toast.makeText(AddWordActivity.this, "Invalid input.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                String meaningID = dbRefManager.getWordDatabaseReference().push().getKey();

                MeaningModel meaningModel = new MeaningModel();
                meaningModel.setMeaning(meaning);
                meaningModel.setMeaningID(meaningID);

                meaningList.add(meaningModel);
                meaningAdapter.notifyDataSetChanged();
                amb.meaning.setText("");
                addMeaningDialog.dismiss();
                progressDialog.dismiss();


            }
        });


        binding.addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addWord(false);

            }
        });

        binding.addWordDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                addWord(true);

            }
        });

        meaningAdapter = new MeaningAdapter(AddWordActivity.this, meaningList);


        binding.meaningRv.setAdapter(meaningAdapter);
        binding.meaningRv.setHasFixedSize(true);
        binding.meaningRv.setLayoutManager(new LinearLayoutManager(AddWordActivity.this));

        if (sharedPreferences.contains("newWordList"))
            binding.newWordListNumber.setText(sharedPreferences.getString("newWordList", ""));


        binding.addMeaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addMeaningDialog.show();

            }
        });

        binding.dictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddWordActivity.this, DictionaryActivity.class)
                        .putExtra("word", ""));
            }
        });


        if (purpose.equals("Edit")) {
            binding.addWord.setText("update");
            wordDetails = (WordModel) getIntent().getSerializableExtra("wordDetails");

            if (wordDetails.getWord() != null) binding.word.setText(wordDetails.getWord());
            if (wordDetails.getImage() != null) binding.image.setText(wordDetails.getImage());
            if (wordDetails.getNote() != null) binding.note.setText(wordDetails.getNote());
            if (wordDetails.getType() != null) {
                switch (wordDetails.getType()) {
                    case "Verb":
                        binding.typeSpinner.setSelection(0);
                        break;
                    case "Adjective":
                        binding.typeSpinner.setSelection(1);
                        break;
                    case "Noun":
                        binding.typeSpinner.setSelection(2);
                        break;
                }
            }

            if (wordDetails.getMeaningList() != null && wordDetails.getMeaningList().size() > 0) {

                meaningList.addAll(wordDetails.getMeaningList());
                meaningAdapter.notifyDataSetChanged();
            }

            binding.word.setText(wordDetails.getWord());
            if (wordDetails.getWord() != null) binding.word.setText(wordDetails.getWord());

        }


    }

    private void addWord(boolean openDictionary) {
        progressDialog.show();

        String word = binding.word.getText().toString();
        String type = binding.typeSpinner.getSelectedItem().toString();
        String note = binding.note.getText().toString();
        String image = binding.note.getText().toString();


        if (word.isEmpty() || word.length() < 1 || !Character.isLetter(word.charAt(0))) {
            Toast.makeText(this, "Invalid word.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        char firstCharacter = word.charAt(0);
        String category = Character.toString(firstCharacter).toUpperCase();


        WordModel2 wordModel = new WordModel2();

        wordModel.setWord(capitalizeFirstCharacter(word.trim()));
        if (purpose.equals("Edit")) wordModel.setWordID(wordDetails.getWordID());
        else {
            //wordModel.setWordID(dbRefManager.getWordDatabaseReference().child(category).push().getKey());
            wordModel.setWordID(getSpecialKey(wordModel.getWord()));
        }

        wordModel.setType(type);
        if (!note.isEmpty()) wordModel.setNote(note);
        if (!image.isEmpty()) wordModel.setNote(image);

        if (meaningList.size() > 0) {
            HashMap<String, String> meaningMap = new HashMap<>();

            for (MeaningModel m : meaningList) {
                meaningMap.put(m.getMeaningID(), m.getMeaning());
            }

            wordModel.setMeaningList(meaningMap);
        }
        Log.i("checkWithLog", "aa");

        String newWordNumber = binding.newWordListNumber.getText().toString().trim();

        updateMap.clear();
//        pathBuilder.clear();
//        updateMap.put(pathBuilder.child("NewWordDatabase").child(category).child(wordModel.getWordID()).build(), wordModel);
//        pathBuilder.clear();
//        updateMap.put(pathBuilder.child("CombinedWordDatabase").child(category).child(wordModel.getWordID()).build(), wordModel);
        Log.i("checkWithLog", "1 : " + pathBuilder.getPath().toString());
        pathBuilder.clear();
        if (newWordNumber.isEmpty() || newWordNumber.length() < 1 || !newWordNumber.matches("\\d+")) {
            updateMap.put(pathBuilder.child("RevisionDatabase").child("NEWWORDS").child(wordModel.getWordID()).build(), wordModel.getWord());
        } else {
            if (newWordNumber.equals("00"))
                updateMap.put(pathBuilder.child("RevisionDatabase").child("TESTWORDS").child(wordModel.getWordID()).build(), wordModel.getWord());
            else
                updateMap.put(pathBuilder.child("RevisionDatabase").child("NEWWORDS" + newWordNumber).child(wordModel.getWordID()).build(), wordModel.getWord());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("newWordList", newWordNumber);
            editor.apply();
        }
        Log.i("checkWithLog", pathBuilder.getPath().toString());

        FirebaseDatabase.getInstance().getReference().updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {


                    binding.word.setText("");
                    binding.note.setText("");
                    binding.image.setText("");
                    binding.typeSpinner.setSelection(0);
                    meaningList.clear();
                    meaningAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                    Toast.makeText(AddWordActivity.this, "Word added successfully.", Toast.LENGTH_SHORT).show();

                    if (openDictionary) {
                        startActivity(new Intent(AddWordActivity.this, DictionaryActivity.class)
                                .putExtra("word", wordModel.getWord()));
                    }

                } else {
                    Toast.makeText(AddWordActivity.this, "Something went wrong try again.", Toast.LENGTH_SHORT).show();
                    //Log.i("checkWithLog",task.getResult().toString());
                    progressDialog.dismiss();
                }

            }
        });

    }

    public static String capitalizeFirstCharacter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char firstChar = Character.toUpperCase(input.charAt(0));
        return firstChar + input.substring(1);
    }

    public static String getSpecialKey(String input) {
        StringBuilder filteredString = new StringBuilder();

        for (char c : input.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                filteredString.append(c);
            }
        }

        return filteredString.toString().toLowerCase();
    }


}