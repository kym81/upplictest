package com.upplic.androidapp.upplictesttask.datasets;

import com.upplic.androidapp.upplictesttask.utils.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class TestJsonObject {
    String time;
    long milliseconds_since_epoch;
    String date;

    public TestJsonObject(HashMap<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "time":
                    this.time = (String) entry.getValue();
                    break;
                case "milliseconds_since_epoch":
                    this.milliseconds_since_epoch = (long) entry.getValue();
                    break;
                case "date":
                    this.date = (String) entry.getValue();
                    break;
            }
        }
    }

    public TestJsonObject(String time, long millis, String date) {
        this.time = time;
        this.milliseconds_since_epoch = millis;
        this.date = date;
    }

    public static HashMap<String, Integer> getFieldsMap() {
        HashMap<String, Integer> fieldsMap = new HashMap<>();
        fieldsMap.put("time", AppConstants.READER_NEXT_STRING);
        fieldsMap.put("milliseconds_since_epoch", AppConstants.READER_NEXT_LONG);
        fieldsMap.put("date", AppConstants.READER_NEXT_STRING);

        return fieldsMap;
    }

    @Override
    public String toString() {
        return time + ": " + milliseconds_since_epoch + ": " + date;
    }
}