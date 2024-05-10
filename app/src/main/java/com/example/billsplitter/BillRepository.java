package com.example.billsplitter;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

class BillRepository {
    private DatabaseReference billsRef;
    private LiveData<List<BillEntity>> allBills;
    private String gName; // Define gName as a class-level variable

    BillRepository(DatabaseReference databaseReference , String gName) {
        // Get a reference to the bills node in the Firebase Realtime Database
        billsRef = FirebaseDatabase.getInstance().getReference().child("bills").child(gName);
    }

    public void insert(BillEntity bill, final RepositoryCallback<Boolean> callback) {
        // Generate a unique key for the new bill
        String billId = billsRef.push().getKey();
        if (billId != null) {
            // Set the bill data under the generated key
            billsRef.child(billId).setValue(bill)
                    .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                    .addOnFailureListener(callback::onError);
        } else {
            callback.onError(new Exception("Failed to generate bill ID"));
        }
    }

    public void delete(BillEntity bill, final RepositoryCallback<Boolean> callback) {
        // Delete the bill using its ID
        billsRef.child(bill.getId()).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onError);
    }

    public void update(BillEntity bill, final RepositoryCallback<Boolean> callback) {
        // Update the bill data using its ID
        billsRef.child(bill.getId()).setValue(bill)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onError);
    }

    public void deleteAll(String gName, final RepositoryCallback<Boolean> callback) {
        // Delete all bills under the specified group name
        billsRef.removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(callback::onError);
    }

    FirebaseQueryLiveData getAllBills() {
        // Implement retrieval of all bills as LiveData
        return new FirebaseQueryLiveData(billsRef);
    }

    public List<BillEntity> getAllBillsForMember(String gName, int mid) {


        return null;
    }
}

