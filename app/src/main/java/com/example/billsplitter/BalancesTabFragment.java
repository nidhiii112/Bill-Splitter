package com.example.billsplitter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class BalancesTabFragment extends Fragment {
    public String gName; // group name
    private String currency;
    private List<MemberEntity> members = new ArrayList<>();
    private List<HashMap<String, Object>> results = new ArrayList<>();
    private BalancesTabViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TextView header;

    // Firebase Database reference
    private FirebaseDatabase mDatabase;

    static BalancesTabFragment newInstance(String gName) {
        Bundle args = new Bundle();
        args.putString("group_name", gName);
        BalancesTabFragment f = new BalancesTabFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.balance_fragment, container, false);
        if(getArguments() == null) {
            return view;
        }
        gName = getArguments().getString("group_name"); // get group name from bundle


        recyclerView = view.findViewById(R.id.balancesRecyclerView);
        emptyView = view.findViewById(R.id.no_data);
        header = view.findViewById(R.id.balancesHeader);
        recyclerView.setHasFixedSize(true);
        adapter = new BalancesTabViewAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        fetchGroupData(); // Fetch group data from Firebase

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            gName = getArguments().getString("group_name"); // get group name from bundle
            runCalculations();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchGroupData();
    }

    private void fetchGroupData() {
        FirebaseDatabase.getInstance().getReference().child("groups").child(gName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            currency = dataSnapshot.child("currency").getValue(String.class);
                            fetchMembers();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    private void fetchMembers() {
        FirebaseDatabase.getInstance().getReference().child("members").child(gName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                                MemberEntity member = memberSnapshot.getValue(MemberEntity.class);
                                if (member != null) {
                                    members.add(member);
                                }
                            }
                            runCalculations();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    private void calculateBalances(PriorityQueue<Balance> debtors, PriorityQueue<Balance> creditors) {
        Query billsQuery = mDatabase.getReference("bills").child(gName);
        billsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    BigDecimal sum = BigDecimal.ZERO; // Initialize sum here
                    for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                        MemberEntity member = memberSnapshot.getValue(MemberEntity.class);
                        if (member != null) {
                            // Fetch bills for each member
                            List<BillEntity> memberBills = memberSnapshot.child("bills").getValue(new GenericTypeIndicator<List<BillEntity>>() {});
                            BigDecimal sumOfAllBills = BigDecimal.ZERO;
                            if (memberBills != null) {
                                for (BillEntity memberBill : memberBills) {
                                    sumOfAllBills = sumOfAllBills.add(new BigDecimal(memberBill.getCost()));
                                }
                            }
                            BigDecimal eachPay = sum.divide(new BigDecimal(members.size()),2, RoundingMode.HALF_EVEN);
                            BigDecimal balance = eachPay.subtract(sumOfAllBills);
                            int compare = 1;
                            int compareNegate = -1;
                            if(balance.compareTo(new BigDecimal("-0.49")) == compareNegate) {
                                debtors.add(new Balance(balance.negate(), member.getName()));
                            } else if (balance.compareTo(new BigDecimal("0.49")) == compare) {
                                creditors.add(new Balance(balance, member.getName()));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void calculateTransactions() {
        results.clear(); // remove previously calculated transactions before calculating again
        PriorityQueue<Balance> debtors = new PriorityQueue<>(members.size(), new BalanceComparator()); // debtors are members of the group who are owed money
        PriorityQueue<Balance> creditors = new PriorityQueue<>(members.size(), new BalanceComparator()); // creditors are members who have to pay money to the group

        calculateBalances(debtors, creditors);

        // Algorithm to calculate transactions
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Balance rich = creditors.peek(); // get the largest creditor
            Balance poor = debtors.peek(); // get the largest debtor
            if (rich == null || poor == null) {
                return;
            }
            String richName = rich.name;
            BigDecimal richBalance = rich.balance;
            creditors.remove(rich); // remove the creditor from the queue

            String poorName = poor.name;
            BigDecimal poorBalance = poor.balance;
            debtors.remove(poor); // remove the debtor from the queue

            BigDecimal min = richBalance.min(poorBalance);

            // calculate the amount to be sent from creditor to debtor
            richBalance = richBalance.subtract(min);
            poorBalance = poorBalance.subtract(min);

            HashMap<String, Object> values = new HashMap<>(); // record the transaction details in a HashMap
            values.put("sender", richName);
            values.put("recipient", poorName);
            values.put("amount", currency.charAt(5) + min.toString());

            results.add(values);

            // Consider a member as settled if he has an outstanding balance between 0.00 and 0.49 else add him to the queue again
            int compare = 1;
            if (poorBalance.compareTo(new BigDecimal("0.49")) == compare) {
                debtors.add(new Balance(poorBalance, poorName));
            }

            if (richBalance.compareTo(new BigDecimal("0.49")) == compare) {
                creditors.add(new Balance(richBalance, richName));
            }
        }

        // Update UI with results
        updateUI();
    }

    private void updateUI() {
        if (results.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            header.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.storeToList(results); // update the recycler view with the new results
        }
    }

    private void runCalculations() {
        if (!members.isEmpty()) {
            calculateTransactions();
        } else {
            results.clear();
            updateUI();
        }
    }
}
