package com.example.billsplitter;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Member_add_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editText;
    private String gName;
    private int requestCode;
    private int userId;
    private int avatarResource;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_add);

        editText = findViewById(R.id.addMemberNameText);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // get data from the intent that started this activity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("groupName")) {
            gName = intent.getStringExtra("groupName");
        } else {
            // Handle the case where groupName is not provided
            Log.e(TAG, "Group name is not provided");
            // You might want to handle this situation appropriately,
            // such as finishing the activity or displaying an error message.
            // For now, let's assign a default value to gName.
            gName = "default_group";
        }
        requestCode = intent.getIntExtra("requestCode", 0);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.addMemberToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // implement spinner for avatar
        final Spinner spinnerAvatar = findViewById(R.id.addMemberActivityAvatarSpinner);
        final MemberAvatarSpinnerAdapter addEditMemberAvatarSpinnerAdapter = new MemberAvatarSpinnerAdapter(this, new ArrayList<Integer>());
        addEditMemberAvatarSpinnerAdapter.setDropDownViewResource(0);
        spinnerAvatar.setAdapter(addEditMemberAvatarSpinnerAdapter);
        spinnerAvatar.setOnItemSelectedListener(this);

        // populate the spinner adapter's list
        List<Integer> avatarOptions = new ArrayList<>();
        populateAvatarList(avatarOptions);
        addEditMemberAvatarSpinnerAdapter.addAll(avatarOptions);

        if (intent.hasExtra("memberId")) {
            // Only edit member intent sends "memberId" with it
            // Get data from the edit member intent that started this activity
            userId = intent.getIntExtra("memberId", -1);

            setTitle("Edit Member");

            editText.setText(intent.getStringExtra("memberName"));

            // set default spinner item
            int spinnerAvatarPosition = addEditMemberAvatarSpinnerAdapter.getPosition(intent.getIntExtra("avatarResource", -1));
            spinnerAvatar.setSelection(spinnerAvatarPosition);
        } else {
            setTitle("Add Member to Group");
        }
    }

    private void populateAvatarList(List<Integer> avatarOptions) {
        avatarOptions.add(R.drawable.member);
        avatarOptions.add(R.drawable.member_female_one);
        avatarOptions.add(R.drawable.member_female_two);
        avatarOptions.add(R.drawable.member_female_three);
        avatarOptions.add(R.drawable.member_female_four);
        avatarOptions.add(R.drawable.member_female_five);
        avatarOptions.add(R.drawable.member_male_one);
        avatarOptions.add(R.drawable.member_male_two);
        avatarOptions.add(R.drawable.member_male_three);
        avatarOptions.add(R.drawable.member_male_four);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_member_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addMemberToolbarMenu) {
            saveEditMember();
            return true;
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void saveEditMember() {
        String name = editText.getText().toString();

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if gName is null before accessing the database reference
        if (gName == null) {
            Log.e(TAG, "Group name is null");
            // Handle the case where gName is null
            // For example, you can display an error message to the user
            Toast.makeText(this, "Group name is null", Toast.LENGTH_SHORT).show();
            return;
        }

        MemberEntity member = new MemberEntity(name, gName);
        member.setMAvatar(avatarResource);

        if (requestCode == 1) {
            // Add member to the database
            databaseReference.child("members").child(gName).push().setValue(member);
        } else if (requestCode == 2) {
            // Edit member in the database
            if (userId != -1) {
                databaseReference.child("members").child(gName).child(String.valueOf(userId)).setValue(member);
            }
        }

        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.addMemberActivityAvatarSpinner) {
            avatarResource = Integer.parseInt(parent.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}