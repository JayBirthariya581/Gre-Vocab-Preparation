package com.gre.prep.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gre.prep.Adapters.AtoZAdapter;
import com.gre.prep.Helper.CustomProgressDialog;
import com.gre.prep.Models.GroupModel;
import com.gre.prep.databinding.ActivityWordDirectoryBinding;

import java.util.ArrayList;
import java.util.List;

public class WordDirectoryActivity extends AppCompatActivity {
    ActivityWordDirectoryBinding binding;
    ArrayList<GroupModel> aToZList;
    AtoZAdapter adapter;
    String purpose;
    CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordDirectoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        purpose = getIntent().getStringExtra("purpose");
        progressDialog = new CustomProgressDialog(WordDirectoryActivity.this);

        progressDialog.show();
        aToZList = new ArrayList<>();
        if(purpose.equals("Test")){
            binding.heading.setText("Select Group(s)");
            binding.check.setVisibility(View.VISIBLE);
            binding.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent data = new Intent();
                    data.putExtra("groupDetails",aToZList);
                    setResult(Activity.RESULT_OK,data);
                    finish();
                }
            });
            adapter = new AtoZAdapter(WordDirectoryActivity.this, aToZList,true);
        } else{
            adapter = new AtoZAdapter(WordDirectoryActivity.this, aToZList,false);
        }



        adapter.setClickListener(new AtoZAdapter.ClickListener() {
            @Override
            public void onClick(GroupModel group) {

                if (adapter.getMultiSelect()) {
                    progressDialog.show();
                    if(group.getSelected()==false) group.setSelected(true);
                    else group.setSelected(false);



                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                } else {
                    startActivity(new Intent(WordDirectoryActivity.this, WordListActivity.class)
                            .putExtra("DB",getIntent().getStringExtra("DB"))
                            .putExtra("Category", group));
                }

            }
        });

        binding.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rv.setAdapter(adapter);
        binding.rv.setHasFixedSize(true);


        for (char c = 'A'; c <= 'Z'; c++) {

            GroupModel cat = new GroupModel();
            cat.setGroupID(String.valueOf(c));
            cat.setGroupName(String.valueOf(c));
            cat.setSelected(false);

            aToZList.add(cat);

        }

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}