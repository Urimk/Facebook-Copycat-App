package com.example.facebookapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Time {
        private int year, month, day, hour, minute;


        public Time(){
                // constructor for current time
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1; // Month starts at zero
                day = calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
        }

        public Time(int year, int month, int day, int hour, int minute){
                // constructor for known time
                this.year = year;
                this.month = month;
                this.day = day;
                this.hour = hour;
                this.minute = minute;
        }

        public Time(JSONObject time) {
                try {
                        this.year = time.getInt("year");
                        this.month = time.getInt("month");
                        this.day = time.getInt("day");
                        this.hour = time.getInt("hour");
                        this.minute = time.getInt("minute");
                }
                catch (JSONException exception) {
                        // problem with json object return default time
                        this.year = 1990;
                        this.month = 1;
                        this.day = 1;
                        this.hour = 12;
                        this.minute = 0;

                }
        }

        public Time(Time time) {
                this.year = time.year;
                this.month = time.month;
                this.day = time.day;
                this.hour = time.hour;
                this.minute = time.minute;
        }

        public int getYear() {
                return year;
        }

        public int getMonth() {
                return month;
        }

        public int getDay() {
                return day;
        }

        public int getHour() {
                return hour;
        }

        public int getMinute() {
                return minute;
        }

        @Override
        public String toString() {
                return this.day  + "/" + this.month + "/"
                + this.year;
        }
}
