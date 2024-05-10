package com.example.billsplitter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MembersTabFragment extends Fragment {

    private static final int CONTACT_PICK_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private MemberViewModel memberViewModel;
    private String gName; // group name
    private MembersTabViewAdapter adapter;
    private DatabaseReference contactsRef;
    ListView listView;
    Button btn;
    RecyclerView recyclerView;
    final ArrayList<String> listdata = new ArrayList<>();
    final ArrayList<String> listname = new ArrayList<>();
    final ArrayList<String> splitDataList = new ArrayList<>();
    String names;
    String contactNameWithUniqueId;
    private MyAdapter mAdapter;
    private List<Contact> contactList;

    private List<MemberEntity> members = new ArrayList<>(); // maintain a list of all the existing members of the group from the database



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Create an instance of MemberRepository
        MemberRepository memberRepository = new MemberRepository();

        // Create a new MemberEntity object
        MemberEntity newMember = new MemberEntity(/* provide member details here */);

       // Call the insertMember method to add the new member to the database
        memberRepository.insertMember(newMember);
        contactsRef = FirebaseDatabase.getInstance().getReference("contacts");

    }

    static MembersTabFragment newInstance(String gName) {
        Bundle args = new Bundle();
        args.putString("group_name", gName);
        MembersTabFragment f = new MembersTabFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.members_fragment, container, false);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        String grpname=sharedPreferences.getString("groupname", "default value");
        Log.d("shdbasdjsncns",grpname);

        try {
            final ArrayList<String> lists=new ArrayList<>();
            final ArrayAdapter adapters=new ArrayAdapter<String>(getContext(),R.layout.list,lists);
//           listView.setAdapter(adapters);
            DatabaseReference refrence= FirebaseDatabase.getInstance().getReference().child("contacts").child(grpname);
            refrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    lists.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            String contactNameWithUniqueId = dataSnapshot.getValue().toString();
                            Log.d("udfhskh",contactNameWithUniqueId);
                            listdata.add(contactNameWithUniqueId);
                            // Split the string using "=" character and take only the second part
                            String[] parts = contactNameWithUniqueId.split("=");
                            if (parts.length == 2) {
                                String contactName = parts[1].trim(); // Trim to remove any leading/trailing spaces
                                // Now contactName contains only the name without the unique ID
//                               lists.add(contactName);
                                Log.d("dhbkjb", contactName);
//                                adapters.add(contactName);
                            }
                        }
                    }
                    Log.d("jsdncsd",listdata.toString());

                    recyclerView = view.findViewById(R.id.membersRecyclerView);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    Log.d("hsdgkjasndd",listname.toString());
                    mAdapter = new MyAdapter(listdata);
                    recyclerView.setAdapter(mAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            Log.d("ehfkewjf",e.toString());
        }


        if (getArguments() == null) {
            return view;
        }


        final ArrayList<String>lists=new ArrayList<>();
        final ArrayAdapter adapters=new ArrayAdapter<String>(getContext(),R.layout.member_detail,lists);
//        listView.setAdapter(adapters);
        DatabaseReference refrence=FirebaseDatabase.getInstance().getReference().child("members");
//        refrence.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                lists.clear();
//                for (DataSnapshot snapshot1:snapshot.getChildren()){
//                    lists.add(snapshot1.getValue().toString());
//                }
//                adapters.notifyDataSetChanged();
//                Log.d("ehfksdbfsd",lists.get(1));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//
//            }
//        });

//        fetchDataFromFirebase();
        gName = getArguments().getString("group_name"); // get group name from bundle

        // prepare recycler view for displaying all members of the group
//        RecyclerView recyclerView = view.findViewById(R.id.membersRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        adapter = new MembersTabViewAdapter(gName, getActivity().getApplication(), this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setAdapter(adapter);

        // LiveData for observing changes in database
        memberViewModel = new ViewModelProvider(this, new MemberViewModelFactory(getActivity().getApplication(), gName)).get(MemberViewModel.class);
        memberViewModel.getAllMembers().observe(getViewLifecycleOwner(), new Observer<List<MemberEntity>>() {
            @Override
            public void onChanged(List<MemberEntity> memberEntities) {
//                adapter.storeToList(memberEntities);
            }
        });
        // FloatingActionButton click listener to add a new member from contacts
        FloatingActionButton addFloating = view.findViewById(R.id.membersFragmentAdd);
        addFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    openContactPicker();
                }
            }
        });

        return view;
    }

    // Handle the result of requesting contacts permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openContactPicker();
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Open contact picker to select a contact
    private void openContactPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, CONTACT_PICK_REQUEST_CODE);
    }

    // Handle the result of selecting a contact
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICK_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    cursor.close();

                    // Store the contact name in Firebase under the group's members
                    addContactToFirebase(gName, contactName);
                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve contact information", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Invalid contact selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Add contact details to Firebase under the specific group
    private void addContactToFirebase(String groupName, String contactName) {
        DatabaseReference groupContactsRef = contactsRef.child(groupName).push();
        groupContactsRef.setValue(contactName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Contact added successfully
                        // Now, update the RecyclerView
                        memberViewModel.insertMember(new MemberEntity(contactName, gName));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to add contact", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.members_fragment_menu, menu);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.deleteAllMembers) {
                if (!members.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Delete All Members")
                            .setMessage("Are you sure you want to delete all members?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    memberViewModel.deleteAll(gName);
                                    Toast.makeText(requireContext(), "All Members Deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                    return true;
                } else {
                    Toast.makeText(requireContext(), "Nothing To Delete", Toast.LENGTH_SHORT).show();
                }
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onPause() {
            // Close ActionMode if the user decides to leave the fragment while multiSelect is ON
//            if (adapter.multiSelect) {
//                adapter.actionMode.finish();
//                adapter.multiSelect = false;
//                adapter.selectedItems.clear();
//                adapter.notifyDataSetChanged();
//            }
            super.onPause();
        }
    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("members").child(gName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder namesBuilder = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String contactName = snapshot.getValue(String.class);
                    // Append the name to the StringBuilder
                    namesBuilder.append(contactName).append(", ");
                }
                // Remove the trailing comma and space
                String names = namesBuilder.toString().trim();
                if (names.endsWith(",")) {
                    names = names.substring(0, names.length() - 2);
                }
                // Now 'names' contains all the contact names separated by commas
                // You can do whatever you want with this string, such as displaying it in a TextView
                Log.d("Names from Firebase:", names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


}



