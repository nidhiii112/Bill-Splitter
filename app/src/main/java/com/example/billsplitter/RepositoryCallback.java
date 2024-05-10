package com.example.billsplitter;

public interface RepositoryCallback<T> {
    void onSuccess(T data);
    void onError(Exception e);
}
