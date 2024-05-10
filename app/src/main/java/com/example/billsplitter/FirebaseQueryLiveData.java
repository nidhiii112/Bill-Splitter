package com.example.billsplitter;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private final DatabaseReference reference;
    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Handle errors if needed
        }
    };

    public FirebaseQueryLiveData(DatabaseReference ref) {
        reference = ref;
    }

    @Override
    protected void onActive() {
        reference.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        reference.removeEventListener(listener);
    }

    public void addValueEventListener(ValueEventListener valueEventListener) {
    }
}