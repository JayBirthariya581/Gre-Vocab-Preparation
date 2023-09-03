package com.gre.prep.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.gre.prep.Activities.AddWordActivity;
import com.gre.prep.Activities.CategoryListActivity;
import com.gre.prep.Activities.DictionaryActivity;
import com.gre.prep.Activities.RevisionWordListActivity;
import com.gre.prep.Activities.TestActivity;
import com.gre.prep.Activities.TestMenuActivity;
import com.gre.prep.Activities.WordDirectoryActivity;
import com.gre.prep.Activities.WordListActivity;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Helper.DBRefManager;
import com.gre.prep.R;
import com.gre.prep.databinding.CardRevisionAccessBinding;
import com.gre.prep.databinding.CardRevisionCreateBinding;
import com.gre.prep.databinding.CardTestMenuTypeBinding;
import com.gre.prep.databinding.FragmentVerbalBinding;

import java.util.HashMap;


public class VerbalFragment extends Fragment {

    FragmentVerbalBinding binding;
    Dialog revisionDialog;
    Dialog testMenuDialog;
    DBRefManager dbRefManager;
    CustomProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerbalBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbRefManager = new DBRefManager();
        progressDialog = new CustomProgressDialog(getContext());

        CardRevisionAccessBinding rab = CardRevisionAccessBinding.inflate(getLayoutInflater());
        revisionDialog = new Dialog(getContext());
        revisionDialog.setContentView(rab.getRoot());


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(revisionDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        revisionDialog.getWindow().setAttributes(lp);


        CardTestMenuTypeBinding ctb = CardTestMenuTypeBinding.inflate(getLayoutInflater());
        testMenuDialog = new Dialog(getContext());
        testMenuDialog.setContentView(ctb.getRoot());


        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(testMenuDialog.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;

        testMenuDialog.getWindow().setAttributes(lp2);

        ctb.oldDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TestMenuActivity.class).putExtra("DB", "Old"));
                testMenuDialog.dismiss();
            }
        });
        ctb.newDatabse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TestMenuActivity.class).putExtra("DB", "New"));
                testMenuDialog.dismiss();
            }
        });
        ctb.combinedDatabse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TestMenuActivity.class).putExtra("DB", "Combined"));
                testMenuDialog.dismiss();
            }
        });

        rab.proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String revisionCode = rab.revisionCode.getText().toString();
                if (revisionCode.isEmpty() || revisionCode.length() < 1) {
                    Toast.makeText(getContext(), "Invalid code!", Toast.LENGTH_SHORT).show();
                    return;
                }
                revisionDialog.dismiss();
                progressDialog.show();

                dbRefManager.getRevisionDatabase().child(revisionCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            progressDialog.dismiss();
                            startActivity(new Intent(getContext(), RevisionWordListActivity.class)
                                    .putExtra("revisionCode", revisionCode));
                        } else {
                            Toast.makeText(getContext(), "No revision list found for given code", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        binding.revision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revisionDialog.show();
            }
        });

        binding.newWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddWordActivity.class)
                        .putExtra("purpose", "Add"));
            }
        });

        binding.combinedWordDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WordDirectoryActivity.class)
                        .putExtra("DB", "Combined")
                        .putExtra("purpose", "Normal"));
            }
        });

        binding.newWordDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WordDirectoryActivity.class)
                        .putExtra("DB", "New")
                        .putExtra("purpose", "Normal"));
            }
        });

        binding.directory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WordDirectoryActivity.class)
                        .putExtra("DB", "Old")
                        .putExtra("purpose", "Normal"));
            }
        });


        binding.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMenuDialog.show();
            }
        });


        binding.categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CategoryListActivity.class)
                        .putExtra("purpose", "List"));
            }
        });

        binding.dictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DictionaryActivity.class)
                        .putExtra("word", ""));
            }
        });


    }
}