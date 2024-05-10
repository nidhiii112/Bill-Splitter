package com.example.billsplitter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExpensesTabFragment extends Fragment {
    private String gName;
    private ExpensesTabViewAdapter adapter;
    private List<BillEntity> bills = new ArrayList<>(); // maintain a list of all the existing bills of the group from the database
    private List<MemberEntity> members = new ArrayList<>(); // maintain a list of all the existing members of the group from the database
    private BillViewModel billViewModel;

    private DatabaseReference databaseReference;
    private StringBuilder currency = new StringBuilder();

    static ExpensesTabFragment newInstance(String gName) {
        Bundle args = new Bundle();
        args.putString("group_name", gName);
        ExpensesTabFragment f = new ExpensesTabFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_fragment, container, false);
        if (getArguments() == null) {
            return view;
        }
        gName = getArguments().getString("group_name");

        // Reference to the database node for bills in the specific group
        databaseReference = FirebaseDatabase.getInstance().getReference().child("expenses").child(gName);

        // prepare recycler view for displaying all expenses of the group
        RecyclerView recyclerView = view.findViewById(R.id.expensesRecyclerView);
        recyclerView.setHasFixedSize(true);
        if(getActivity() != null) {
            adapter = new ExpensesTabViewAdapter(gName, getActivity().getApplication(), this);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        // if data in database(BillEntity) changes, call the onChanged() below
        billViewModel = new ViewModelProvider(this, new BillViewModelFactory(requireActivity().getApplication(), databaseReference, gName)).get(BillViewModel.class);
        billViewModel.getAllBills().observe(getViewLifecycleOwner(), new Observer<List<BillEntity>>() {
            @Override
            public void onChanged(List<BillEntity> billEntities) {
                GroupViewModel groupViewModel = new ViewModelProvider(ExpensesTabFragment.this).get(GroupViewModel.class);
                groupViewModel.getGroupCurrencyNonLive(gName, new GroupRepository.GroupCurrencyCallback() {
                    @Override
                    public void onCurrencyReceived(String currency) {
                        // Once currency is received, update UI with bills and currency
                        if (currency != null) {
                            StringBuilder currencyBuilder = new StringBuilder(currency); // Create a new StringBuilder for currency

                            // Replace the previous currency in the adapter with the new one
                            adapter.storeToList(billEntities, currencyBuilder.toString());
                            bills = billEntities;
                        } else {
                            // Handle the case when currency is null
                        }
                    }
                });
            }
        });

        // Attach a listener to fetch expenses data from Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<BillEntity> expenses = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BillEntity expense = snapshot.getValue(BillEntity.class);
                    if (expense != null) {
                        expenses.add(expense);
                    }
                }
                adapter.setExpenses(expenses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("ExpensesTabFragment", "Failed to read expenses.", databaseError.toException());
            }
        });

        // Floating action button to add a new expense
        FloatingActionButton addFloating = view.findViewById(R.id.expensesFragmentAdd);
        addFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddEditBillActivity.class);
                intent.putExtra(Group_List_Activity.EXTRA_TEXT_GNAME, gName);
                intent.putExtra("requestCode", 1);
                getActivity().startActivityFromFragment(ExpensesTabFragment.this, intent, 1);
            }
        });

        // Listener for item click to edit an expense
        adapter.setOnItemClickListener(new ExpensesTabViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BillEntity bill) {
                Intent intent = new Intent(getActivity(), AddEditBillActivity.class);
                intent.putExtra("billId", bill.getId());
                intent.putExtra("billPaidBy", bill.getPaidBy());
                intent.putExtra("billCost", bill.getCost());
                intent.putExtra("billMemberId", bill.getMid());
                intent.putExtra("billName", bill.getItem());
                intent.putExtra(Group_List_Activity.EXTRA_TEXT_GNAME, bill.getGName());
                intent.putExtra("requestCode", 2);
                getActivity().startActivityFromFragment(ExpensesTabFragment.this, intent, 2);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.expenses_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAllBills) {
            if (item.getItemId() == R.id.deleteAllBills) {
                if (getActivity() != null) {
                    if (!bills.isEmpty()) { // condition prevents initiating a deleteAll operation if there are no bills to delete
                        deleteAllBills();
                        Toast.makeText(getActivity(), "All Expenses Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Nothing To Delete", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onPause() {
        if (adapter.multiSelect) {
            adapter.actionMode.finish();
            adapter.multiSelect = false;
            adapter.selectedItems.clear();
            adapter.notifyDataSetChanged();
        }
        super.onPause();

    }
    // Method to delete all bills for the group
    private void deleteAllBills() {
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "All Expenses Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to delete expenses", Toast.LENGTH_SHORT).show();
            }
        });
    }
}