package com.example.billsplitter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GroupViewHolder  extends RecyclerView.ViewHolder{


    private TextView groupNameTextView;

    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);
        // Initialize views
        groupNameTextView = itemView.findViewById(R.id.groupListDetailName);
    }

    public void bind(String groupName) {
        // Bind data to views
        groupNameTextView.setText(groupName);
    }

}
