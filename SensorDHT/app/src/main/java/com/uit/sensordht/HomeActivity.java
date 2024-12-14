package com.uit.sensordht;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.CurrentWeatherCallback;
import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.Model.ItemWeather;
import com.uit.sensordht.Model.Weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {
    TextView tvHumidity, tvTemperature, tvTime, tvMinHumidity, tvMaxHumidity, tvMinTemperature, tvMaxTemperature;
    ConstraintLayout clTemperature, clHumidity;
    public Fragment secondFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTime = findViewById(R.id.tvCurrentTime);
        tvMinHumidity = findViewById(R.id.tvLowHumidity);
        tvMaxHumidity = findViewById(R.id.tvHighHumidity);
        clTemperature = findViewById(R.id.clTemperature);
        clHumidity = findViewById(R.id.clHumidity);
        tvMinTemperature = findViewById(R.id.tvMin);
        tvMaxTemperature = findViewById(R.id.tvMax);
        APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
            @Override
            public void onSuccess(Weather weather, ItemWeather temperatureData, ItemWeather humidityData, int delayTime) {
                long timestamp = (weather.time) * 1000;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
//                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int month = calendar.get(Calendar.MONTH) + 1; // Tháng trong Calendar bắt đầu từ 0
                int year = calendar.get(Calendar.YEAR);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int day = calendar.get(Calendar.DATE);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale.ENGLISH);
                String formattedDateTime = sdf.format(calendar.getTime());
                Log.d("DateTime", "Day of week: " + dayOfWeek);
                Log.d("DateTime", "Month: " + month);
                Log.d("DateTime", "Year: " + year);
                Log.d("DateTime", "Hour: " + hour);
                Log.d("DateTime", "Minute: " + minute);
                Log.d("DateTime", "Second: " + second);
                Log.d("DateTime", "Formatted date time: " + formattedDateTime);
                String humidity = String.join(" ", String.valueOf(humidityData.current), getResources().getString(R.string.percent));
                String highHumidity = String.join(" ", String.valueOf(humidityData.max), getResources().getString(R.string.percent));
                String lowHumidity = String.join(" ", String.valueOf(humidityData.min), getResources().getString(R.string.percent));
                String temperature = String.join(" ", String.valueOf(temperatureData.current), getResources().getString(R.string.celsius));
                String minTemperature = String.join(" ", String.valueOf(temperatureData.min), getResources().getString(R.string.celsius));
                String maxTemperature = String.join(" ", String.valueOf(temperatureData.max), getResources().getString(R.string.celsius));
                tvHumidity.setText(humidity);
                tvTemperature.setText(temperature);
                tvTime.setText(formattedDateTime);
                tvMaxHumidity.setText(highHumidity);
                tvMinHumidity.setText(lowHumidity);
                tvMinTemperature.setText(minTemperature);
                tvMaxTemperature.setText(maxTemperature);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        clTemperature.setOnClickListener(v ->  {
            APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
                @Override
                public void onSuccess(Weather weather, ItemWeather temperature, ItemWeather humidity, int delayTime) {
                    GlobalVars.currentTime = weather.time;
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
            showDialogBarTemperature();
        });
        clHumidity.setOnClickListener(v -> {
            APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
                @Override
                public void onSuccess(Weather weather, ItemWeather temperature, ItemWeather humidity, int delayTime) {
                    GlobalVars.currentTime = weather.time;
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
            showDialogBarHumidity();
        });
    }
    public void showDialogBarTemperature() {
        // Trong Fragment hoặc Activity
        TemperatureDialogFragment temperatureDialogFragment = TemperatureDialogFragment.newInstance();
        temperatureDialogFragment.show(getSupportFragmentManager(), "temperature_dialog");

    }
    public void showDialogBarHumidity() {
        // Trong Fragment hoặc Activity
        HumidityDialogFragment humidityDialogFragment = HumidityDialogFragment.newInstance();
        humidityDialogFragment.show(getSupportFragmentManager(), "temperature_dialog");

    }

    public void replaceFragmentRightToLeft(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);

        ft.replace(R.id.fl_temperature, fragment);
        ft.commit();
    }
    public void replaceFragmentLeftToRight(Fragment fragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        ft.replace(R.id.fl_temperature, fragment);
        ft.commit();
    }
    private void InitVars()
    {
    }
}