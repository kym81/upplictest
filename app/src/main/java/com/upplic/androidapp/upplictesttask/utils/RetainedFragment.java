package com.upplic.androidapp.upplictesttask.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RetainedFragment extends Fragment {

    private Object data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}