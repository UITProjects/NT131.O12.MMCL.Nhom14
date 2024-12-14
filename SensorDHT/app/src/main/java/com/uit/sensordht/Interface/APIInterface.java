package com.uit.sensordht.Interface;

import com.google.gson.JsonObject;
import com.uit.sensordht.Model.HistoryWeather;
import com.uit.sensordht.Model.Weather;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {
    @FormUrlEncoded
    @POST("https://server.uitprojects.com/registration")
    Call<String> createAccount(@Field("email") String email, @Field("password") String password, @Field("username") String username);
    @FormUrlEncoded
    @POST("https://server.uitprojects.com/authentication")
    Call<JsonObject> authentication(@Field("username") String username_primary, @Field("password") String password);
//    @FormUrlEncoded
    @GET("https://server.uitprojects.com/current_temp")
    Call<Weather> getCurrent_temp();
    @GET("https://server.uitprojects.com/history")
    Call<JsonObject> getHistory_weather(@Query("left") long left,
                                            @Query("right") long right,
                                            @Query("order") String order,
                                            @Query("limit") int limit);
}

