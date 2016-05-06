package com.upplic.androidapp.upplictesttask.utils;

import com.upplic.androidapp.upplictesttask.datasets.CityInfo;

import java.util.Comparator;

public class CityInfoComparator implements Comparator<CityInfo> {

    public enum Field {
        NAME, COUNT
    }

    private final Field field;

    public CityInfoComparator(Field field) {
        this.field = field;
    }

    @Override
    public int compare(CityInfo lhs, CityInfo rhs) {
        if (null == lhs)
            return 1;

        if (null == rhs)
            return -1;

        int comparison = 0;
        switch (field) {
            case NAME:
                comparison = lhs.getName().compareTo(rhs.getName());
                break;
            case COUNT:
                comparison = lhs.getCount() - rhs.getCount();
                break;
        }

        return comparison;
    }
}
