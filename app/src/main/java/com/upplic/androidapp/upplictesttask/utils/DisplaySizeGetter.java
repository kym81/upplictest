package com.upplic.androidapp.upplictesttask.utils;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class DisplaySizeGetter {

    public static int getWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
