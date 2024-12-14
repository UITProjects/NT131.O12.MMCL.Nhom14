package com.uit.sensordht.Model;

import com.google.gson.annotations.SerializedName;

public class ItemWeather {
    @SerializedName("current")
    public float current;
    @SerializedName("min")
    public float min;
    @SerializedName("max")
    public float max;
}
