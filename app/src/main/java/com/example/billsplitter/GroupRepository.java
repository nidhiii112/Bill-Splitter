package com.example.billsplitter;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupRepository {
    private static DatabaseReference databaseReference;

    public GroupRepository(Application application) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("groups");
    }

    public void insert(GroupEntity group) {
        String key = databaseReference.push().getKey();
        if (key != null) {
            databaseReference.child(key).setValue(group);
        }
    }

    public void delete(GroupEntity group) {
        databaseReference.child(group.getId()).removeValue();
    }

    public void update(GroupEntity group) {
        databaseReference.child(group.getId()).setValue(group);
    }

    public void deleteAll() {
        databaseReference.removeValue();
    }

    public LiveData<List<GroupEntity>> getAllGroups() {
        MutableLiveData<List<GroupEntity>> groupsLiveData = new MutableLiveData<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GroupEntity> groups = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupEntity group = snapshot.getValue(GroupEntity.class);
                    if (group != null) {
                        groups.add(group);
                    }
                }
                groupsLiveData.setValue(groups);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        return groupsLiveData;
    }

    // You need to implement the methods for fetching currency as well.
    // These methods will be similar to the getAllGroups method.

    // For example:

    public static void getGroupCurrencyNonLive(String gName, final GroupCurrencyCallback callback) {
        DatabaseReference groupRef = databaseReference.child(gName).child("groupCurrency");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currency = dataSnapshot.getValue(String.class);
                callback.onCurrencyReceived(currency);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                callback.onCurrencyReceived(null); // Notify callback with null currency
            }
        });
    }
    // Define an interface for the callback
    public interface GroupCurrencyCallback {
        void onCurrencyReceived(String currency);
    }

    public LiveData<String> getGroupCurrency(String gName) {
        MutableLiveData<String> currencyLiveData = new MutableLiveData<>();
        databaseReference.child(gName).child("currency").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currency = dataSnapshot.getValue(String.class);
                currencyLiveData.setValue(currency);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        return currencyLiveData;
    }


}
