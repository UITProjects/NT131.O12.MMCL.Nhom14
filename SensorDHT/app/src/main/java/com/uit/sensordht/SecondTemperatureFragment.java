package com.uit.sensordht;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.uit.sensordht.API.APIManager;
import com.uit.sensordht.Interface.CurrentWeatherCallback;
import com.uit.sensordht.Interface.HistoryWeatherCallback;
import com.uit.sensordht.Model.GlobalVars;
import com.uit.sensordht.Model.ItemWeather;
import com.uit.sensordht.Model.Weather;
import com.uit.sensordht.Model.XAxisTimeFormatter;

import java.util.Timer;
import java.util.TimerTask;

public class SecondTemperatureFragment extends DialogFragment {
    LineChart lineChart;
    int setValue = 0;
    private LineData data;
    private XAxis xAxis;
    private YAxis yAxisLeft, yAxisRight;
    TimerTask timerTask;
    final int period = 1000;

    Timer timer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
            @Override
            public void onSuccess(Weather weather, ItemWeather temperature, ItemWeather humidity, int day) {
                GlobalVars.currentTime = weather.time;
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
        InitViews(view);
        InitEvent();
        LiveChartData();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second_temperature, container, false);
    }
    private void InitViews(View v)
    {
        lineChart = v.findViewById(R.id.lineChartTemperature);
    }
    private void InitEvent() {

    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Real-time Data");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawCircles(false);
        set.setLineWidth(4f);
        set.setColor(R.drawable.bg_temperature);
        set.setDrawValues(true);
        set.setDrawCircles(true);
        return set;
    }
    private void setupChart() {
        data = new LineData();
        lineChart.setData(data);
        //Y-Axis
        yAxisRight = lineChart.getAxisRight();
        yAxisLeft = lineChart.getAxisLeft();
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(20f);
        yAxisLeft.setAxisMaximum(40f);

        //X-axis
        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.RED);
        xAxis.setLabelCount(10, false);
        xAxis.setValueFormatter(new XAxisTimeFormatter());

    }
    private void LiveChartData()
    {
        setupChart();
        startTimer();
    }
    private void startTimer() {
        DelayHolder delayHolder = new DelayHolder(1000); // Class để giữ giá trị delay
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    APIManager.fnGetCurrentWeather(new CurrentWeatherCallback() {
                        @Override
                        public void onSuccess(Weather weather, ItemWeather temperature, ItemWeather humidity, int delay) {
                            float newValue = temperature.current;
                            delayHolder.setDelay(delay); // Cập nhật giá trị delay từ hàm onSuccess
                            LineDataSet dataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                            if (dataSet == null) {
                                dataSet = createSet();
                                data.addDataSet(dataSet);
                            }
                            xAxis.setAxisMaximum(GlobalVars.currentTime + setValue);
                            data.addEntry(new Entry(dataSet.getEntryCount(), newValue), 0);

                            data.notifyDataChanged();
                            xAxis.setDrawGridLinesBehindData(true);

                            setValue += 5;
                            lineChart.notifyDataSetChanged();
                            lineChart.fitScreen();
                            lineChart.setVisibleXRangeMaximum(10);
                            lineChart.invalidate();
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
                });
            }
        };

        timer.schedule(timerTask, 0, delayHolder.getDelay()); // Sử dụng giá trị delay từ holder
    }

    // Class để giữ giá trị delay
    private static class DelayHolder {
        private int delay;

        DelayHolder(int delay) {
            this.delay = delay;
        }

        int getDelay() {
            return delay;
        }

        void setDelay(int delay) {
            this.delay = delay;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
