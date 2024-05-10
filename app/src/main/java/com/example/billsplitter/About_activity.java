package com.example.billsplitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.billsplitter.databinding.ActivityAboutBinding;


public class About_activity extends Dashboard1 {
    ActivityAboutBinding activityAboutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAboutBinding = ActivityAboutBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Profile");
        setContentView(activityAboutBinding.getRoot());
    }
}