package com.uit.sensordht.Interface;

import com.google.gson.JsonArray;

public interface HistoryWeatherCallback {
    void onSuccess(JsonArray data);
    void onFailure(String errorMessage);
}
