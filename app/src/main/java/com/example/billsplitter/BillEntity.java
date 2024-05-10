package com.example.billsplitter;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BillEntity implements Serializable {
    private String id;
    private int memberId;
    private String item;
    private String paidBy;
    private String cost;
    private String gName;

    // Required empty constructor for Firebase
    public BillEntity() {}

    public BillEntity(int mid, String item, String cost, String gName, String paidBy) {
        this.memberId = mid;
        this.item = item;
        this.cost = cost;
        this.gName = gName;
        this.paidBy = paidBy;
    }

    // Exclude annotation to prevent 'id' from being serialized to Firebase
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMid() {
        return memberId;
    }

    public void setMid(int mid) {
        this.memberId = mid;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getGName() {
        return gName;
    }

    public void setGName(String gName) {
        this.gName = gName;
    }

    public int getMemberId() {

        return 0;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("item", item);
        map.put("cost", cost);
        // Add other properties here
        return map;
    }
}
