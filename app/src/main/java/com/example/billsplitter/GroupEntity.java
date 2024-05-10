package com.example.billsplitter;

import com.google.firebase.database.Exclude;

public class GroupEntity {
    private String id; // Unique ID for the group
    private String groupName;
    private String groupCurrency;

    public GroupEntity() {
        // Default constructor required for Firebase
    }

    public GroupEntity(String groupName, String groupCurrency) {
        this.groupName = groupName;
        this.groupCurrency = groupCurrency;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCurrency() {
        return groupCurrency;
    }

    public void setGroupCurrency(String groupCurrency) {
        this.groupCurrency = groupCurrency;
    }
}
