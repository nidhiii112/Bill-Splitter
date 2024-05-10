package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.billsplitter.databinding.ActivityDashboard1Binding;
import com.example.billsplitter.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Profile_Activity extends Dashboard1 {
    ActivityProfileBinding activityProfileBinding;
    TextView name, username;
    Button edtname, edtusername, edtemail, edtpassword, feedback, save_changes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        allocateActivityTitle("Profile");
        setContentView(activityProfileBinding.getRoot());

        name = findViewById(R.id.Updated_name);
        username = findViewById(R.id.Updated_username);
        edtname = findViewById(R.id.edit_name);
        edtusername = findViewById(R.id.edit_username);
        edtemail = findViewById(R.id.edit_email);
        edtpassword = findViewById(R.id.edit_password);
        feedback = findViewById(R.id.feedback);
        save_changes = findViewById(R.id.save_changes);

        showUserData();
    }

    private void showUserData() {

        Intent intent = getIntent();

        String nameuser = intent.getStringExtra(String.valueOf(name));
        String usernameuser = intent.getStringExtra(String.valueOf(username));
        String emailuser = intent.getStringExtra(String.valueOf(edtemail));
        String passworduser = intent.getStringExtra(String.valueOf(edtpassword));


        name.setText(nameuser);
        username.setText(usernameuser);
        edtname.setText(nameuser);
        edtusername.setText(usernameuser);
        edtemail.setText(emailuser);
        edtpassword.setText(passworduser);

    }

       public void passUserData(){
        String userUsername = edtusername.getText().toString().trim();

           DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
           Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

           checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.exists()){

                       String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                       String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                       String UsernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                       String PasswordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);


                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
       }
}