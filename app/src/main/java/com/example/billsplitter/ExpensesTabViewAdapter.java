package com.example.billsplitter;

import android.app.Application;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExpensesTabViewAdapter extends RecyclerView.Adapter<ExpensesTabViewAdapter.ExpenseDetailViewHolder> {
    private OnItemClickListener listener;
    private List<BillEntity> list = new ArrayList<>();
    boolean multiSelect = false; //true if user has selected any item
    List<BillEntity> selectedItems = new ArrayList<>();
    ActionMode actionMode;
    private String gName;
    private String currency;
    private Application application;
    private ExpensesTabFragment thisOfExpenseFragment;
    private DatabaseReference databaseReference;

    ExpensesTabViewAdapter(String gName, Application application, ExpensesTabFragment thisOfExpenseFragment) {
        this.gName = gName;
        this.application = application;
        this.thisOfExpenseFragment = thisOfExpenseFragment;
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child("bills").child(gName);
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            actionMode = mode;
            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        // method is called when user clicks on "Delete" option in the menu

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (BillEntity bill : selectedItems) {
                list.remove(bill);
                deleteFromDatabase(bill);
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    public void setExpenses(List<BillEntity> expenses) {
    }

    // ViewHolder for each item in RecyclerView
    class ExpenseDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewItem;
        private TextView textViewCost;
        private TextView textViewCurrency;
        private TextView textViewPaidBy;
        private RelativeLayout relativeLayout;

        ExpenseDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.expenseDetailItem);
            textViewCost = itemView.findViewById(R.id.expenseDetailCost);
            textViewCurrency = itemView.findViewById(R.id.expenseDetailCurrency);
            textViewPaidBy = itemView.findViewById(R.id.expenseDetailPaidBy);
            relativeLayout = itemView.findViewById(R.id.expenseDetail);
        }

        void update(final BillEntity bill) {
            if (selectedItems.contains(bill)) {
                relativeLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((AppCompatActivity) v.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(bill);
                    return true;
                }
            });
        }

        void selectItem(BillEntity bill) {
            if (multiSelect) {
                if (selectedItems.contains(bill)) {
                    selectedItems.remove(bill);
                    relativeLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(bill);
                    relativeLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }
    }

    @NonNull
    @Override
    public ExpenseDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_detail, parent, false);
        return new ExpenseDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseDetailViewHolder holder, int position) {
        final BillEntity bill = list.get(position);
        StringBuilder justText = new StringBuilder();
        justText.append("Paid By: ");
        holder.textViewItem.setText(bill.getItem());
        holder.textViewCost.setText(bill.getCost());
        holder.textViewPaidBy.setText(justText.append(bill.getPaidBy()));
        holder.textViewCurrency.setText(Character.toString(currency.charAt(5)));
        holder.update(bill);

        // attach a click listener to the ExpenseDetailViewHolder
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiSelect) {
                    holder.selectItem(bill);
                }
                if (listener != null && !multiSelect) {
                    listener.onItemClick(bill);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void storeToList(List<BillEntity> billEntities, String currency) {
        list = billEntities;
        this.currency = currency;
        notifyDataSetChanged();
    }

    private void deleteFromDatabase(BillEntity bill) {
        databaseReference.child(bill.getId()).removeValue();
    }

    public interface OnItemClickListener {
        void onItemClick(BillEntity bill);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
