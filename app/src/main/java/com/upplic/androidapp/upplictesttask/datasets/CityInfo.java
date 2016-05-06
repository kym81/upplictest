package com.upplic.androidapp.upplictesttask.datasets;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.upplic.androidapp.upplictesttask.utils.AppConstants;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CityInfo implements Parcelable {
    private static final String LOG_TAG = "QM_" + CityInfo.class.getSimpleName();

    private String name;
    private String description;
    private int count;
    private boolean selected;

    public CityInfo(CityInfo item) {
        this.name = item.name;
        this.description = item.description;
        this.count = item.count;
        this.selected = item.selected;
    }

    public CityInfo(String name, String description, int count, boolean selected) {
        this.name = name;
        this.description = description;
        this.count = count;
        this.selected = selected;
    }

    public CityInfo(HashMap<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            switch (key) {
                case "name":
                    this.name = (String) entry.getValue();
                    break;
                case "description":
                    this.description = (String) entry.getValue();
                    break;
                case "count":
                    this.count = (int) entry.getValue();
                    break;
                case "selected":
                    this.selected = (int) entry.getValue() == 1;
                    break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean is_selected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return getName() + ": " + getDescription() + ": " + getCount() + ": " + is_selected();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getDescription());
        dest.writeInt(getCount());
        dest.writeInt(is_selected() ? 1 : 0);
    }

    private CityInfo(Parcel in) {
        this.setName(in.readString());
        this.setDescription(in.readString());
        this.setCount(in.readInt());
        this.setSelected(in.readInt() == 1);
    }

    public static final Creator<CityInfo> CREATOR = new Creator<CityInfo>() {
        public CityInfo createFromParcel(Parcel in) {
            return new CityInfo(in);
        }

        public CityInfo[] newArray(int size) {
            return new CityInfo[size];
        }
    };

    public static HashMap<String, Integer> getFieldsMap() {
        HashMap<String, Integer> fieldsMap = new HashMap<>();
        fieldsMap.put("name", AppConstants.READER_NEXT_STRING);
        fieldsMap.put("description", AppConstants.READER_NEXT_STRING);
        fieldsMap.put("count", AppConstants.READER_NEXT_INT);
        fieldsMap.put("selected", AppConstants.READER_NEXT_INT);

        return fieldsMap;
    }

}
