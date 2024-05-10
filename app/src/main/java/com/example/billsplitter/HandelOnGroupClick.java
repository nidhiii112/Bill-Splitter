package com.example.billsplitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

public class HandelOnGroupClick extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handel_on_group_click);

        /* get extra data(name of the group the user clicked on) from the intent that started this activity
         * Hence, we can load all the members and bills of the group the user clicked on in GroupList Activity*/

        Intent intent = getIntent();
       String gName = intent.getStringExtra(Group_List_Activity.EXTRA_TEXT_GNAME);

        TabLayout tabLayout = findViewById(R.id.tablayout_id);
        ViewPager viewPager = findViewById(R.id.viewpager_id);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.handleOnGroupClickToolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        setTitle(gName);

        // create adapter for viewpager and add all three fragments/tabs to this adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // pass along the group name so that the fragment can generate all the members of the group the user clicked on in GroupList activity
        adapter.addFragment((Fragment) MembersTabFragment.newInstance(gName),"Members");
        // pass along the group name so that the fragment can generate all the expenses of the group the user clicked on in GroupList activity
        adapter.addFragment((Fragment) ExpensesTabFragment.newInstance(gName),"Expenses");
        // pass along the group name so that the fragment can generate all the balances of the group the user clicked on in GroupList activity
        adapter.addFragment((Fragment) BalancesTabFragment.newInstance(gName),"Balances");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            // if user clicks on back button initiate finish to close activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    }
