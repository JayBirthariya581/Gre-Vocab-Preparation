package com.gre.prep.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gre.prep.Fragments.ProfileFragment;
import com.gre.prep.Fragments.VerbalFragment;
import com.gre.prep.R;
import com.gre.prep.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Fragment selectorFragment;
    SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Fragment f1 = new VerbalFragment();
        Fragment f2 = new ProfileFragment();

        FragmentManager fm = getSupportFragmentManager();


        fm.beginTransaction().add(R.id.fragmentContainerView, f2, "2").hide(f2).commit();
        fm.beginTransaction().replace(R.id.fragmentContainerView, f1, "1").commit();
        selectorFragment = f1;

        binding.bnv.setItemIconTintList(null);

        sdf = new SimpleDateFormat("dd MMM yyyy");
        Calendar currentDate = Calendar.getInstance();
        Calendar greDate = Calendar.getInstance();
        Calendar greDate2 = Calendar.getInstance();
        Calendar ieltsDate = Calendar.getInstance();
        greDate.set(2023, Calendar.SEPTEMBER, 14);
        greDate2.set(2023, Calendar.SEPTEMBER, 19);
        ieltsDate.set(2023, Calendar.SEPTEMBER, 23);

        Long currentTimeInMillis = currentDate.getTimeInMillis();
        Long greTimeInMillis = greDate.getTimeInMillis();
        Long gre2TimeInMillis = greDate2.getTimeInMillis();
        Long ieltsTimeInMillis = ieltsDate.getTimeInMillis();

        Long timeDifferenceInMillisGre = greTimeInMillis - currentTimeInMillis;
        Long timeDifferenceInMillisGre2 = gre2TimeInMillis - currentTimeInMillis;
        Long timeDifferenceInMillisIelts = ieltsTimeInMillis - currentTimeInMillis;


        Long daysDifferenceGre = timeDifferenceInMillisGre / (24 * 60 * 60 * 1000);
        Long daysDifferenceGre2 = timeDifferenceInMillisGre2 / (24 * 60 * 60 * 1000);
        Long daysDifferenceIelts = timeDifferenceInMillisIelts / (24 * 60 * 60 * 1000);

        binding.gre.setText(String.valueOf(daysDifferenceGre)+" or "+String.valueOf(daysDifferenceGre2));
        binding.greDate.setText("14 or "+sdf.format(greDate2.getTime().getTime()));
        binding.ielts.setText(String.valueOf(daysDifferenceIelts));
        binding.ieltsDate.setText(sdf.format(ieltsDate.getTime().getTime()));


        binding.bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.verbal:
                        binding.heading.setText("Verbal");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, f1, "1").commit();
                        selectorFragment = f1;
                        break;
                    case R.id.profile:
                        binding.heading.setText("Profile");
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, f2, "2").commit();
                        selectorFragment = f2;
                        break;

                }
                return true;
            }
        });

        binding.addWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddWordActivity.class)
                        .putExtra("purpose", "Add"));
            }
        });

        binding.heading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, WordLoadActivity.class));

            }
        });


    }
}