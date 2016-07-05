package com.example.android.sunshine.app.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by cristhian on 4/07/16.
 */
public class TodayWeather {

    private String todayDate;
    private String todayMaxTemp;
    private String todayMinTemp;
    private Bitmap todayWeatherIcon;

    public TodayWeather(String todayDate, String todayMaxTemp, String todayMinTemp, Bitmap todayWeatherIcon) {
        this.todayDate = todayDate;
        this.todayMaxTemp = todayMaxTemp;
        this.todayMinTemp = todayMinTemp;
        this.todayWeatherIcon = todayWeatherIcon;
    }

    public String getTodayDate() {
        return todayDate;
    }

    public void setTodayDate(String todayDate) {
        this.todayDate = todayDate;
    }

    public String getTodayMaxTemp() {
        return todayMaxTemp;
    }

    public void setTodayMaxTemp(String todayMaxTemp) {
        this.todayMaxTemp = todayMaxTemp;
    }

    public String getTodayMinTemp() {
        return todayMinTemp;
    }

    public void setTodayMinTemp(String todayMinTemp) {
        this.todayMinTemp = todayMinTemp;
    }

    public Bitmap getTodayWeatherIcon() {
        return todayWeatherIcon;
    }

    public void setTodayWeatherIcon(Bitmap todayWeatherIcon) {
        this.todayWeatherIcon = todayWeatherIcon;
    }
}
