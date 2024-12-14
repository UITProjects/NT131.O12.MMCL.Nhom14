package com.uit.sensordht.API;

import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.uit.sensordht.Interface.APIInterface;
import com.uit.sensordht.Interface.CreateUserCallback;
import com.uit.sensordht.Interface.CurrentWeatherCallback;
import com.uit.sensordht.Interface.HistoryWeatherCallback;
import com.uit.sensordht.Interface.LoginUserCallback;
import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.Model.HistoryWeather;
import com.uit.sensordht.Model.ItemHistoryWeather;
import com.uit.sensordht.Model.ItemWeather;
import com.uit.sensordht.Model.User;
import com.uit.sensordht.Model.Weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIManager {
    static final APIClient apiClient = new APIClient();
    static final APIInterface interfaceAPI = apiClient.getRetrofitInstance().create(APIInterface.class);
//    static final APIInterface login = apiClient.getRetrofitInstance().create(APIInterface.class);
public static void fnGetHistoryWeather(long timestamp, HistoryWeatherCallback callback) {
    Call<JsonObject> call = interfaceAPI.getHistory_weather(0, timestamp, "desc", 1);
    call.enqueue(new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            if (response.isSuccessful()) {
                JsonObject data = response.body();
                JsonArray dataArray = data.get("data").getAsJsonArray();
                callback.onSuccess(dataArray);
            } else {
                try {
                    String errorBody = response.errorBody().string();
                    callback.onFailure(errorBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            callback.onFailure(t.getMessage());
        }
    });
}
    public static void fnGetCurrentWeather(CurrentWeatherCallback callback)
    {
        Call<Weather> call = interfaceAPI.getCurrent_temp();
        call.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if(response.isSuccessful())
                {
                    Weather data = response.body();
                    ItemWeather temperature = data.temperature;
                    ItemWeather humidity = data.humidity;
                    int delayTime = data.delay;
                    Log.d("API CALL", String.valueOf(delayTime));
                    callback.onSuccess(data, temperature, humidity, delayTime);
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("CALL API", "Error response: " + errorBody);
                        callback.onFailure(errorBody);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Log.e("CALL API", "Network error: " + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }
    public static void fnLogin(String username, String password, LoginUserCallback callback)
    {
        Call<JsonObject> call = interfaceAPI.authentication(username, password);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful())
                {
                    Log.d("API CALL", "Login Success");
                    callback.onSuccess("Login Success");
                }
                else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("CALL API", "Error response: " + errorBody);
                        callback.onFailure(errorBody);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CALL API", "Network error: " + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }
    public static void fnCreateAccount(String email, String password, String username, CreateUserCallback callback)
    {
        Call<String> call = interfaceAPI.createAccount(email, password, username);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful())
                {
                    Log.d("CALL API", "Create Account Success");
                    callback.onSuccess("Create Account Success");
                }
                else
                {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("CALL API", "Error response: " + errorBody);
                        callback.onFailure(errorBody);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("CALL API", "Network error: " + t.getMessage());
                callback.onFailure(t.getMessage());
            }
        });
    }

}
