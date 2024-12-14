package com.uit.sensordht.Interface;

public interface LoginUserCallback {
    void onSuccess(String message);
    void onFailure(String errorMessage);
}
