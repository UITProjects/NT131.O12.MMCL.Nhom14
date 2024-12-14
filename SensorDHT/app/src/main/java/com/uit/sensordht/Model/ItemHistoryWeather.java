package com.uit.sensordht.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ItemHistoryWeather {
    public long time;
    public float temperature;
    public float humidity;
    public ItemHistoryWeather(JsonArray array)
    {
        JsonObject data = array.get(0).getAsJsonObject();
        this.time = data.get("time").getAsLong();
        this.temperature = data.get("temperature").getAsFloat();
        this.humidity = data.get("humidity").getAsFloat();
    }
}
