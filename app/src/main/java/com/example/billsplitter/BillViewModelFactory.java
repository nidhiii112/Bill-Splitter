package com.example.billsplitter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BillViewModelFactory implements ViewModelProvider.Factory {
    private DatabaseReference billsRef;
    private String gName;
    private Application application;

    BillViewModelFactory(Application application, DatabaseReference billsRef, String gName) {
        // Get a reference to the bills node in the Firebase Realtime Database
        this.billsRef = billsRef;
        this.gName = gName;
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BillViewModel(application, billsRef, gName);
    }
}