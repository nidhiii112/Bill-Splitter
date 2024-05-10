package com.example.billsplitter;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SigninActivity extends AppCompatActivity {

    TextView signupRedirect;
    EditText loginusername, loginpassword;
    Button loginButton;

    SharedPreferences sharedPreferences;

    TextView forgotPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        loginusername = findViewById(R.id.login_username);
        loginpassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirect = findViewById(R.id.signupRedirect);
        forgotPassword = findViewById(R.id.forgot);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            // If user is already logged in, redirect to Dashboard
            startActivity(new Intent(SigninActivity.this, Dashboard1.class));
            finish();
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword()) {
                    //validation failed , do not proceed with login
                } else {
                    checkuser();
                }
            }
        });

        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public Boolean validateUsername() {
        String val = loginusername.getText().toString();
        if (val.isEmpty()) {
            loginusername.setError("Username Cannot be Empty");
            return false;
        } else {
            loginusername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginpassword.getText().toString();
        if (val.isEmpty()) {
            loginpassword.setError("Password Cannot be Empty");
            return false;
        } else {
            loginpassword.setError(null);
            return true;
        }
    }

    public void checkuser() {
        String userUsername = loginusername.getText().toString();
        String userPassword = loginpassword.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    loginusername.setError(null);
                    String passwordfromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if (Objects.equals(passwordfromDB, userPassword)) {
                        // Password matches, proceed to dashboard
                        Intent intent = new Intent(SigninActivity.this, Dashboard1.class);
                        startActivity(intent);
                    } else {
                        // Password does not match, show error
                        loginpassword.setError("Invalid Credentials");
                        loginpassword.requestFocus();
                    }
                } else {
                    // If the username does not exist in the database
                    loginusername.setError("User does not exist");
                    loginusername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //handle database read error
                Toast.makeText(SigninActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText emailBox = dialogView.findViewById(R.id.emailBox);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailBox.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendPasswordResetEmail(email);
                } else {
                    Toast.makeText(SigninActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SigninActivity.this, "Password Reset Email Sent", Toast.LENGTH_SHORT).show();

                                // Store the email in the Realtime Database for future reference
                                String userId = currentUser.getUid(); // Use currentUser instead of mAuth.getCurrentUser()
                                mDatabase.child(userId).setValue(email);
                            } else {
                                Toast.makeText(SigninActivity.this, "Failed to Send Reset Email Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Handle the case where there is no currently signed-in user
            Toast.makeText(SigninActivity.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
        }
    }
}