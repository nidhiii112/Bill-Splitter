package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Group_List_Activity extends AppCompatActivity {

    public static final String EXTRA_TEXT_GNAME = "com.nidhisingh.billsplitter.EXTRA_TEXT_GNAME";
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;
    private List<String> groupNames;

    private DatabaseReference groupsRef;

    private TextView noGroupsMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);


        // set toolbar
        Toolbar toolbar = findViewById(R.id.groupListToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Groups");


        // Initialize Firebase Realtime Database reference
        groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        recyclerView = findViewById(R.id.group_list_recycler_view);
        noGroupsMsg = findViewById(R.id.groupnametextView);

        groupNames = new ArrayList<>();
        groupAdapter = new GroupAdapter(this, groupNames);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);

        // Fetch group names from the Firebase Realtime Database
        fetchGroupNames();

        Log.d("hbfkjsnss",groupNames.toString());


        //set OnGroupClickListener To intent
        groupAdapter.setOnGroupClickListener(new GroupAdapter.OnGroupClickListener() {
            @Override
            public void onGroupClick(int position) {
                // get group name of the item the user clicked on from groupNames array
                String gName = groupNames.get(position);

                // create an intent to launch the HandleOnGroupClickActivity, pass the gName along
                Intent intent = new Intent(Group_List_Activity.this, HandelOnGroupClick.class);
                intent.putExtra(EXTRA_TEXT_GNAME, gName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.group_list_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.deleteAllGroups) {
            if(!groupNames.isEmpty()) {
                groupsRef.removeValue(); // Remove all groups from Firebase Realtime Database
                Toast.makeText(this, "All Groups Deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
            Toast.makeText(this, "Nothing To Delete", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        // finish if user clicks on back button
        finish();
        return true;
    }

    @Override
    public void onPause() {
        if (groupAdapter.multiSelect) {
            groupAdapter.actionMode.finish();
            groupAdapter.multiSelect = false;
            groupAdapter.selectedItems.clear();
            groupAdapter.notifyDataSetChanged();
        }
        super.onPause();
    }

    private void fetchGroupNames() {
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String groupName = snapshot.getKey();
                    groupNames.add(groupName);
                }
                // Update the ListView with the fetched group names
                groupAdapter.notifyDataSetChanged();

                // Show or hide the message depending on whether there are groups
                if (groupNames.isEmpty()) {
                    noGroupsMsg.setVisibility(TextView.VISIBLE);
                } else {
                    noGroupsMsg.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(Group_List_Activity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}