package com.example.billsplitter;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberDetailViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    private RelativeLayout relativeLayout;
    ImageView imageView;
    private List<MemberEntity> selectedItems;
    private boolean multiSelect;

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Implement onCreateActionMode
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            // Implement onPrepareActionMode
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // Implement onActionItemClicked
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // Implement onDestroyActionMode
        }
    };
    public MemberDetailViewHolder(@NonNull View itemView, List<MemberEntity> selectedItems) {
        super(itemView);
        // Initialize selectedItems list
        this.selectedItems = selectedItems;
        // Initialize views
        textView = itemView.findViewById(R.id.memberDetailName);
        relativeLayout = itemView.findViewById(R.id.memberDetail);
        imageView = itemView.findViewById(R.id.memberDetailAvatar);
    }


    // Method to update the view with member data
    public void update(final MemberEntity member, boolean isSelect) {
        // Update views based on member data
        textView.setText(member.getName());
        imageView.setImageResource(member.getAvatar());

        // Set background color based on whether the item is selected
        if (selectedItems.contains(member)) {
            relativeLayout.setBackgroundColor(Color.LTGRAY);
        } else {
            relativeLayout.setBackgroundColor(Color.WHITE);
        }

        // Attach a long click listener to initiate multi-selection
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Handle long click event
                // Start action mode and select the current item
                ((AppCompatActivity) v.getContext()).startSupportActionMode(actionModeCallbacks);
                selectItem(member);
                return true;
            }
        });
    }

    // Method to handle item selection
    public void selectItem(MemberEntity member) {
        if (multiSelect && selectedItems != null) {
            if (selectedItems.contains(member)) {
                selectedItems.remove(member);
                relativeLayout.setBackgroundColor(Color.WHITE); // Deselect item
            } else {
                selectedItems.add(member);
                relativeLayout.setBackgroundColor(Color.LTGRAY); // Select item
            }
        }
    }
}

