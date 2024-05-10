package com.example.billsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextView  loginRedirect;
    EditText signupName, signupEmail, signupUsername, signupPassword ;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirect = findViewById(R.id.loginRedirect);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                // Get user input values from EditTexts

                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                // Create an instance of firebaseHelper class with user details

                firebaseHelper helperclass = new firebaseHelper(name, email, username, password);
                // Save user data to Firebase database under a unique identifier (in this case, 'Myname')
                reference.child(username).setValue(helperclass);

                Toast.makeText(RegisterActivity.this, "You have Registered Succesfully", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(RegisterActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });
        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegisterActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });

    }
}