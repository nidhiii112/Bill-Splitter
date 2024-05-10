package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText editText;
    private DatabaseReference groupsRef;

   /* private void saveGroup() {
        final String name = editText.getText().toString();

        //check if the name is empty
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            return;
        }
    }*/

        /*

        GroupViewModel groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);


        GroupEntity group = new GroupEntity(name); // create group object that needs to be inserted into the database
        group.gCurrency = "USD-($)"; // set default currency

        // if database already contains group object return and do not save
        List<GroupEntity> groups = groupViewModel.getAllGroupsNonLiveData();
        for(GroupEntity item:groups) {
            if(item.gName.equals(group.gName)) {
                Toast.makeText(this, "Group already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // else store the group object in database
        groupViewModel.insert(group);
        Toast.makeText(this, "Group created successfully", Toast.LENGTH_SHORT).show();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        // set toolbar
        Toolbar toolbar = findViewById(R.id.createNewGroupToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Create New Group");

        // get reference to edit text-"Enter group name"
        editText = findViewById(R.id.createNewGroupName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // fill toolbar menu with save group item
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_group_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // save the group to database if user clicks on save group item in the toolbar
        if (item.getItemId() == R.id.createNewGroupToolbarSaveGroupItem) {
            saveGroup();
            return true; // Return true to consume the menu click event
        }
        // If the user clicks on the back button, finish the activity
        return super.onOptionsItemSelected(item);

    }

    private void saveGroup() {
        String groupName = editText.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(this, "Please enter a group name", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the group name already exists in the database
        groupsRef.child(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Group name already exists, show error message
                    Toast.makeText(CreateGroupActivity.this, "Group name already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Group name is unique, save it to the database
                    groupsRef.child(groupName).setValue(true);
                    Toast.makeText(CreateGroupActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();

                    // Finish the activity to return to the previous screen
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(CreateGroupActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
