package com.example.billsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AddEditBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText editTextItem;
    private EditText editTextCost;
    private String currency;
    private String gName;
    private String paidBy;
    private int memberId;
    private int requestCode;
    private int billId;
    private DatabaseReference billsRef;

    private void saveExpense() {
        String item = editTextItem.getText().toString();
        String cost = editTextCost.getText().toString();

        // Check if the item name or cost is empty
        if (item.trim().isEmpty() || cost.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a valid input", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get a reference to the bills node in the Firebase Database
        DatabaseReference billsRef = FirebaseDatabase.getInstance().getReference().child("bills").child(gName);

        if (requestCode == 1) { // 1 for Add Bill Activity
            // Round up the cost of the bill to 2 decimal places
            BigDecimal decimal = new BigDecimal(cost);
            BigDecimal res = decimal.setScale(2, RoundingMode.HALF_EVEN);

            // Generate a unique key for the new bill
            String billId = billsRef.push().getKey();
            if (billId != null) {
                // Set the bill data under the generated key
                BillEntity bill = new BillEntity(memberId, item, res.toString(), gName, paidBy);
                billsRef.child(billId).setValue(bill);
            }
        }

        if (requestCode == 2) { // 2 for Edit Bill Activity
            BillEntity bill = new BillEntity(memberId, item, cost, gName, paidBy);
            bill.setId(String.valueOf(billId));

            // Update the bill in the Firebase Realtime Database
            billsRef.child(String.valueOf(billId)).setValue(bill);
        }

        // Update the group currency in the Firebase Realtime Database
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("groups").child(gName);
        groupRef.child("groupCurrency").setValue(currency);

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bill);


        // set toolbar
        Toolbar toolbar = findViewById(R.id.addBillToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextItem = findViewById(R.id.addBillItemName);
        editTextCost = findViewById(R.id.addBillItemCost);


        editTextCost.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            public void afterTextChanged(Editable arg0) {
                String str = editTextCost.getText().toString();
                if (str.isEmpty()) return;
                String str2 = PerfectDecimal(str, 20, 2);

                if (!str2.equals(str)) {
                    editTextCost.setText(str2);
                    int pos = editTextCost.getText().length();
                    editTextCost.setSelection(pos);
                }
            }
        });

        // Get data from the intent that started this activity
        Intent intent = getIntent();
        if (intent != null) {
            gName = getIntent().getStringExtra(Group_List_Activity.EXTRA_TEXT_GNAME);
            requestCode = getIntent().getIntExtra("requestCode", 0);
            memberId = getIntent().getIntExtra("billMemberId", -1);
            billId = getIntent().getIntExtra("billId", -1);
            currency = getIntent().getStringExtra("groupCurrency");

            // Set up spinner for selecting currency
            Spinner spinner = findViewById(R.id.addBillItemCurrencySpinner);
            final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.currencySymbols, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
            int spinnerPositionCurrency = spinnerAdapter.getPosition(currency);
            spinner.setSelection(spinnerPositionCurrency); // set spinner default selection

            // Set up spinner for selecting paidBy Member
            final Spinner spinnerPaidBy = findViewById(R.id.addBillItemPaidBy);
            final com.example.billsplitter.AllMembersSpinnerAdapter allMembersSpinnerAdapter = new com.example.billsplitter.AllMembersSpinnerAdapter(this, new ArrayList<MemberEntity>());
            allMembersSpinnerAdapter.setDropDownViewResource(0);
            spinnerPaidBy.setAdapter(allMembersSpinnerAdapter);
            spinnerPaidBy.setOnItemSelectedListener(this);

            // Get all current members of the group from Firebase Realtime Database
            DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("members").child(gName);
            membersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<MemberEntity> memberEntities = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MemberEntity member = snapshot.getValue(MemberEntity.class);
                        if (member != null) {
                            memberEntities.add(member);
                        }
                    }
                    allMembersSpinnerAdapter.clear();
                    allMembersSpinnerAdapter.addAll(memberEntities);
                    allMembersSpinnerAdapter.notifyDataSetChanged();

                    if (requestCode == 2) {
                        MemberEntity member = new MemberEntity(paidBy, gName);
                        member.setId(String.valueOf(memberId));
                        int spinnerPositionPaidBy = allMembersSpinnerAdapter.getPosition(member);
                        spinnerPaidBy.setSelection(spinnerPositionPaidBy);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                }
            });

            if (intent.hasExtra("billId")) {
                // Only edit bill intent sends "billId" with it
                // Get data from the edit bill intent that started this activity
                setTitle("Edit expense");
                editTextItem.setText(intent.getStringExtra("billName")); // set default text received from the intent
                editTextCost.setText(intent.getStringExtra("billCost")); // set default text received from the intent
                paidBy = intent.getStringExtra("billPaidBy");
            } else {
                setTitle("Add an Expense");
            }}
    }

    public String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL){
        if(str.charAt(0) == '.') str = "0"+str;
        int max = str.length();

        StringBuilder rFinal = new StringBuilder();
        boolean after = false;
        int i = 0, up = 0, decimal = 0; char t;
        while(i < max){
            t = str.charAt(i);
            if(t != '.' && !after){
                up++;
                if(up > MAX_BEFORE_POINT) return rFinal.toString();
            }else if(t == '.'){
                after = true;
            }else{
                decimal++;
                if(decimal > MAX_DECIMAL)
                    return rFinal.toString();
            }
            rFinal.append(t);
            i++;
        }return rFinal.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_bill_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addBillToolbarMenu) {
            saveExpense();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.addBillItemCurrencySpinner:
                currency = parent.getItemAtPosition(position).toString();
                break;
            case R.id.addBillItemPaidBy:
                MemberEntity member = (MemberEntity) parent.getItemAtPosition(position);
                paidBy = member.getName();
                if (member.getId() != null && !member.getId().isEmpty()) {
                    try {
                        memberId = Integer.parseInt(member.getId());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        // Handle the case where member ID is not a valid integer
                        // For example, display an error message or assign a default value
                    }
                } else {
                    // Handle the case where member ID is null or empty
                    // You can assign a default value or show an error message
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
