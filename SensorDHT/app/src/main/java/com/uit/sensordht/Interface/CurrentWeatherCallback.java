package com.uit.sensordht.Interface;

import com.uit.sensordht.Model.ItemWeather;
import com.uit.sensordht.Model.Weather;

public interface CurrentWeatherCallback {
    void onSuccess(Weather weather, ItemWeather temperature, ItemWeather humidity, int delay);
    void onFailure(String errorMessage);
}
