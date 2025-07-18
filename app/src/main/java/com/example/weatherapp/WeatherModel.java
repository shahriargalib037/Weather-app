package com.example.weatherapp;

public class WeatherModel {
    private String time,temp,icon,windspeed;

    public WeatherModel(String time, String temp, String icon, String windspeed) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
        this.windspeed = windspeed;
    }

    public String getTime() {
        return time;
    }

    public String getTemp() {
        return temp;
    }

    public String getIcon() {
        return icon;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }
}
