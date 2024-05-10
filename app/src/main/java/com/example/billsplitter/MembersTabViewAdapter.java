package com.example.billsplitter;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MembersTabViewAdapter extends RecyclerView.Adapter<MembersTabViewAdapter.MemberDetailViewHolder> {
    private DatabaseReference databaseReference;
    private List<MemberEntity> list = new ArrayList<>();
    boolean multiSelect = false;
    List<MemberEntity> selectedItems = new ArrayList<>();
    private String gName;
    private Application application;
    private MembersTabFragment thisOfMemberFragment;
    private OnItemClickListener listener;
    ActionMode actionMode;
//constructor
    MembersTabViewAdapter(String gName, Application application, MembersTabFragment thisOfMemberFragment) {
        this.gName = gName;
        this.application = application;
        this.thisOfMemberFragment = thisOfMemberFragment;

        // Check if gName is not null before initializing Firebase Realtime Database reference
        if (gName != null && !gName.isEmpty()) { databaseReference = FirebaseDatabase.getInstance().getReference().child("members").child(gName);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    StringBuilder namesBuilder = new StringBuilder();
                    for (DataSnapshot dataSnapshots : dataSnapshot.getChildren()) {
                        if (dataSnapshots.exists() && dataSnapshots.getValue() != null) {
                            String contactNameWithUniqueId = dataSnapshots.getValue().toString();
                            // Split the string using "=" character and take only the second part
                            String[] parts = contactNameWithUniqueId.split("=");
                            if (parts.length == 2) {
                                String contactName = parts[1].trim(); // Trim to remove any leading/trailing spaces
                                // Now contactName contains only the name without the unique ID
                                Log.d("dhbkjb", contactName);
//                                adapters.add(contactName);
                            }
                        }
                    }
                    // Remove the trailing comma and space
//                    String names = namesBuilder.toString().trim();
//                    if (names.endsWith(",")) {
//                        names = names.substring(0, names.length() - 2);
//                    }
                    // Now 'names' contains all the contact names separated by commas
                    // You can do whatever you want with this string, such as displaying it in a TextView
//                    Log.d("Names from Firebase:", names);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Log.e("Firebase Error", "Database error: " + databaseError.getMessage());
                }
            });


            Log.d("hedkssf",databaseReference.toString());
        } else {
            // Handle case where gName is null or empty
            // For example, you can log an error message or provide a default value
            // In this example, I'm setting a default value to "default_group"
            databaseReference = FirebaseDatabase.getInstance().getReference().child("members").child("default_group");
        }

        // Callbacks for ActionMode
        ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
            // method is called right after the user does a long click on any item
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                multiSelect = true;
                menu.add("Delete");
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Delete selected items
                for (MemberEntity member : selectedItems) {
                    list.remove(member);
                    deleteFromDatabase(member);
                }
                mode.finish(); // Finish action mode
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                multiSelect = false;
                selectedItems.clear(); // Clear selected items list
                notifyDataSetChanged();
            }
        };

    }

    class MemberDetailViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private RelativeLayout relativeLayout;
        private ImageView imageView;

        MemberDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            // store all references from our layout for future use
            textView = itemView.findViewById(R.id.memberDetailName); // get the textView view component reference from member_detail.xml and attach it to our holder
            relativeLayout = itemView.findViewById(R.id.memberDetail);
            imageView = itemView.findViewById(R.id.memberDetailAvatar); // member avatar image reference
        }

        void update(final MemberEntity member) {

            /* if the user clicks on back button while an item was selected(gray colour), notifyDataSetChanged is called, hence update() is called again for every viewHolder. So, at this point
               we need to make sure that the item that was selected(gray colour) previously, needs to be white(unselected) now. */
            if (selectedItems.contains(member)) {
                relativeLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }

            // attach a long click listener to itemView
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // at this point the user has successfully initiated a long click and hence we need to activate ActionMode now to handle multiple select and delete items
                    ActionMode.Callback actionModeCallbacks = null;
                    ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks); // activate ActionMode and let actionModeCallback handle actions now
                    selectItem(member); // here member is the initially selected item after the long click event
                    return true;
                }
            });
        }

        void selectItem(MemberEntity member) {
            if (multiSelect) {
                if (selectedItems.contains(member)) { // if  we select a member that is already selected(light gray), deselect it(change colour to white) and remove from selectedItems list
                    selectedItems.remove(member);
                    relativeLayout.setBackgroundColor(Color.WHITE);
                } else { // else add the member to our selection list and change colour to light gray
                    selectedItems.add(member);
                    relativeLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

    }

    // Create new viewHolder (invoked by the layout manager). Note that this method is called for creating every MemberDetailViewHolder required for our recycler view items
    @NonNull
    @Override
    public MemberDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_detail, parent, false);
        return new MemberDetailViewHolder(v); // pass the inflated view, create a holder with textView and avatar components attached to it and return
    }

    // note that this method is called for every MemberDetailViewHolder
    @Override
    public void onBindViewHolder(@NonNull MemberDetailViewHolder holder, int position) {
        final MemberDetailViewHolder hold = holder;

        Log.d("jndksjnff",list.toString());
        holder.textView.setText(list.get(position).name);
//        Log.d("kkdhbfskdjb",list.get(position).name.toString());// set member name to holder
        holder.imageView.setImageResource(list.get(position).avatar); // set member avatar to holder
        holder.update(list.get(position));

        final int pos = position;

        // attach a on click listener to the MemberDetailViewHolder
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Note that if the user is in multiSelect mode, the function for individual click on any item(multiSelect off) shouldn't be initiated*/
                if(multiSelect) {  // if multiSelect is on and user clicks on any other item, run selectItem function for that item
                    hold.selectItem(list.get(pos));
                }
                if(listener != null && !multiSelect) { // if multiSelect is Off, clicking on any item should initiate edit member intent
                    listener.onItemClick(list.get(pos)); // onItemClick method defined in MembersTabFragment[line 69]
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void storeToList(List<MemberEntity> memberEntities) {
        list = memberEntities;
        Log.d("jflsdnf",list.toString());
        notifyDataSetChanged();
    }

    private void deleteFromDatabase(MemberEntity member) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("members")
                .child(member.getName()) // Assuming 'gName' is the group name under which members are stored
                .child(member.getId()); // Assuming 'id' is the unique identifier of the member
        databaseReference.removeValue(); // Remove the member from the database
    }

    public interface OnItemClickListener {
        void onItemClick(MemberEntity member);
    }

    // store a reference(as a private variable) to the OnItemClickListener object passed on as a parameter
    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
