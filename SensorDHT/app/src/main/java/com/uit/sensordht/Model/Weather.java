package com.uit.sensordht.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("time")
    public long time;
    @SerializedName("temperature")
    public ItemWeather temperature;
    @SerializedName("humidity")
    public ItemWeather humidity;
    @SerializedName("delay")
    public int delay;

}


