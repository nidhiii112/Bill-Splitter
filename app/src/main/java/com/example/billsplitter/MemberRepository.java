package com.example.billsplitter;

import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MemberRepository {
    private DatabaseReference databaseReference;
    String gName;
    private ValueEventListener listener;


    public MemberRepository() {
            // Initialize Firebase Database reference
            databaseReference = FirebaseDatabase.getInstance().getReference().child("members");
            // This assumes that "members" is the correct path to your Firebase Database node.
            // If your path is different, make sure to adjust it accordingly.
        }

        public void insertMember(MemberEntity member) {
            String memberId = databaseReference.push().getKey();
            databaseReference.child(memberId).setValue(member);
        }

        public void delete(MemberEntity member) {
            // Assuming each member has a unique ID
            databaseReference.child(member.getId()).removeValue();
        }

        public void update(MemberEntity member) {
            databaseReference.child(member.getId()).setValue(member);
        }

        public void deleteAll(String gName) {
            databaseReference.removeValue();
        }

        // Setter method for setting the ValueEventListener
        public void setListener(ValueEventListener listener) {
            this.listener = listener;
        }

        // Method to fetch all members once
        public void getAllMembersOnce() {
            databaseReference.addListenerForSingleValueEvent(listener);
        }
    }
