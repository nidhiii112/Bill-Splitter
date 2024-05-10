package com.example.billsplitter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BillViewModel extends AndroidViewModel {
    private BillRepository billRepository;
    private DatabaseReference billref;

    private MutableLiveData<List<BillEntity>> allBillsLiveData = new MutableLiveData<>();

    public BillViewModel(@NonNull Application application, DatabaseReference billsRef, String gName) {
        super(application);
        this.billref = FirebaseDatabase.getInstance().getReference().child("bills").child(gName);
        billRepository = new BillRepository(billsRef, gName); // Pass gName here
        attachDatabaseListener();
    }

    public void insert(BillEntity bill) {
        billRepository.insert(bill, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Handle success if needed
            }

            @Override
            public void onError(Exception e) {
                // Handle error if needed
            }
        });
    }

    public void update(BillEntity bill) {
        billRepository.update(bill, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Handle success if needed
            }

            @Override
            public void onError(Exception e) {
                // Handle error if needed
            }
        });
    }

    public void delete(BillEntity bill) {
        billRepository.delete(bill, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Handle success if needed
            }

            @Override
            public void onError(Exception e) {
                // Handle error if needed
            }
        });
    }

    public void deleteAll(String gName) {
        billRepository.deleteAll(gName, new RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                // Handle success if needed
            }

            @Override
            public void onError(Exception e) {
                // Handle error if needed
            }
        });
    }

    private void attachDatabaseListener() {
        billref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<BillEntity> bills = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BillEntity bill = snapshot.getValue(BillEntity.class);
                    if (bill != null) {
                        bills.add(bill);
                    }
                }
                allBillsLiveData.setValue(bills);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors if needed
            }
        });
    }

    public LiveData<List<BillEntity>> getAllBills() {
        return allBillsLiveData;
    }
}
