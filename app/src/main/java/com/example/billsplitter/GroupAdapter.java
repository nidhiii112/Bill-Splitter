package com.example.billsplitter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.Reference;
import java.util.List;
import java.util.zip.Deflater;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {


    public boolean multiSelect;
    public Deflater actionMode;
    public Reference<Object> selectedItems;
    private Context context;
    private List<String> groupNames;
    private OnGroupClickListener onGroupClickListener;
    public GroupAdapter(Context context, List<String> groupNames) {
        this.context = context;
        this.groupNames = groupNames;
    }
        public interface OnGroupClickListener {
            void onGroupClick(int position);
        }
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.onGroupClickListener = listener;
    }


        @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_list_detail, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String groupName = groupNames.get(position);
        holder.bind(groupName);
        holder.itemView.setOnClickListener(new View.OnClickListener() { // Step 4
            @Override
            public void onClick(View v) {
                if (onGroupClickListener != null) {
                    onGroupClickListener.onGroupClick(position);
                }
                Log.d("jdnjkdfvds",groupName);
                String grpname=groupName;
                SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("groupname",grpname);
                editor.apply();
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupNames.size();
    }
    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView groupNameTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupListDetailName);
        }

        public void bind(String groupName) {
            groupNameTextView.setText(groupName);
        }
    }
}
