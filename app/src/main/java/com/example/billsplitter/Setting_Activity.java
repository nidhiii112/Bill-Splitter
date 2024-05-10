package com.example.billsplitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.billsplitter.databinding.ActivityProfileBinding;
import com.example.billsplitter.databinding.ActivitySettingBinding;

public class Setting_Activity extends Dashboard1 {
    ActivitySettingBinding activitySettingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Setting");
        setContentView(activitySettingBinding.getRoot());
    }
}