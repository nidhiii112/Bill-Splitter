package com.example.billsplitter;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GroupViewModel extends AndroidViewModel {
    private GroupRepository repository;

    public GroupViewModel(@NonNull Application application) {
        super(application);
        repository = new GroupRepository(application);
    }

    public void insert(GroupEntity group) {
        repository.insert(group);
    }

    public void update(GroupEntity group) {
        repository.update(group);
    }

    public void delete(GroupEntity group) {
        repository.delete(group);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<GroupEntity>> getAllGroups() {
        return repository.getAllGroups();
    }

    // Update this method to work with Firebase
    public LiveData<String> getGroupCurrency(String gName) {
        return repository.getGroupCurrency(gName);
    }

    public void getGroupCurrencyNonLive(String gName, final GroupRepository.GroupCurrencyCallback callback) {
        GroupRepository.getGroupCurrencyNonLive(gName, new GroupRepository.GroupCurrencyCallback() {
            @Override
            public void onCurrencyReceived(String currency) {
                // Handle the currency data here
                callback.onCurrencyReceived(currency);
            }
        });

    }
}
