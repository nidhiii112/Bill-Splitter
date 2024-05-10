package com.example.billsplitter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MemberViewModel  extends AndroidViewModel {
    private DatabaseReference databaseReference;
    private MemberRepository repository;
    private LiveData<List<MemberEntity>> allMembers;

    public MemberViewModel(@NonNull Application application, String gName) {
        super(application);
        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("members").child(gName);
        // Initialize LiveData
        allMembers = new FirebaseLiveData();
    }

    // Method to insert member into Firebase
    public void insertMember(MemberEntity member) {
        String memberId = databaseReference.push().getKey();
        if (memberId != null) {
            member.setId(memberId);
            databaseReference.child(memberId).setValue(member);
        }
    }

    public void update(MemberEntity member) {
        databaseReference.child(member.getId()).setValue(member);
    }

    public void delete(MemberEntity member) {
        databaseReference.child(member.getId()).removeValue();
    }

    public void deleteAll(String gName) {
        databaseReference.removeValue();
    }

    LiveData<List<MemberEntity>> getAllMembers() {
        return allMembers;
    }

    private class FirebaseLiveData extends LiveData<List<MemberEntity>> {
        FirebaseLiveData() {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<MemberEntity> members = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Extract id, name, email, avatar, and gName from the snapshot
                        String id = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        int avatar = snapshot.child("avatar").getValue(Integer.class);
                        String gName = snapshot.child("gName").getValue(String.class);

                        // Create a new MemberEntity object with the extracted values
                        MemberEntity member = new MemberEntity(id, name, email, avatar, gName);

                        // Add the member to the list
                        members.add(member);
                    }
                    // Set the value of LiveData with the updated list of members
                    setValue(members);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

}
