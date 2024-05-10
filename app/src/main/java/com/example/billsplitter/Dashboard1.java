package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class Dashboard1 extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard1);

        //navigation_baar handler
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0); // removes shadow/elevation between toolbar and status bar
        }
        setTitle("");

        // set drawer
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // get view references for "Groups" and "Create New group" Buttons
        View listGroups = findViewById(R.id.listGroups);
        View createNewGroup = findViewById(R.id.createNewGroup);

        // attach click listener to buttons
        listGroups.setOnClickListener((View.OnClickListener) this);
        createNewGroup.setOnClickListener((View.OnClickListener) this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mainMenuShare) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        return super.onOptionsItemSelected(item);
    }

    // method for handling clicks on our buttons
    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.listGroups : intent = new Intent(this,Group_List_Activity.class);
                startActivity(intent);
                break;

            case R.id.createNewGroup: intent = new Intent(this,CreateGroupActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // close the drawer if user clicks on back button while drawer is open
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
       drawer.closeDrawer(GravityCompat.START);

       switch (item.getItemId()){
           case R.id.nav_profile:
               startActivity(new Intent(this,Profile_Activity.class));
               overridePendingTransition(0,0);
               break;

           case R.id.nav_setting:
               startActivity(new Intent(this,Setting_Activity.class));
               overridePendingTransition(0,0);
               break;

           case R.id.nav_logout:
               AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard1.this);
               builder.setTitle("Logout");
               builder.setMessage("Are you sure you want to logout?");
               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // Perform logout action here
                       // For example, redirect the user to the login screen
                       Intent intent = new Intent(Dashboard1.this, SigninActivity.class);
                       startActivity(intent);
                       // Finish the current activity to prevent the user from going back
                       finish();
                   }
               });
               builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // Dismiss the dialog if the user chooses not to logout
                       dialog.dismiss();
                   }
               });
               builder.show();
               break;


           case R.id.nav_about:
               startActivity(new Intent(this,About_activity.class));
               overridePendingTransition(0,0);
               break;

       }
        return false;
    }

    protected void allocateActivityTitle(String titleString){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(titleString);
        }
    }
}