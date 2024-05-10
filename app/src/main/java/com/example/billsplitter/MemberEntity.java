package com.example.billsplitter;

public class MemberEntity {
    private String id; // Unique identifier for the member
    public String name;
    private String email;
    public int avatar;

    private String gName;
    // Default constructor required for Firebase Realtime Database
    public MemberEntity() {
    }

    // Constructor
    public MemberEntity(String id, String name, String email, int avatar, String gName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.gName = gName;
    }

    // Constructor without id (useful when adding new members)
    public MemberEntity(String name, String email, int avatar) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }

    // Getter and setter for group name
    public String getGroupName() {
        return gName;
    }

    public void setGroupName(String groupName) {
        this.gName = groupName;
    }

    public MemberEntity(String paidBy, String gName) {
    }

    public MemberEntity(MemberEntity memberName) {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    // Setter method for avatar resource
    public int getAvatar() {
        return avatar;
    }
    public void setMAvatar(int avatarResource) {
        this.avatar = avatarResource;
    }
}

