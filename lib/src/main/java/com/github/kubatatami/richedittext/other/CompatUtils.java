package com.github.kubatatami.richedittext.other;

import android.os.Build;
import android.widget.TextView;

import java.lang.reflect.Field;

public class CompatUtils {

    public static float getLineSpacingMultiplier(TextView textView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return textView.getLineSpacingMultiplier();
        }
        try {
            Field field = TextView.class.getDeclaredField("mSpacingMult");
            field.setAccessible(true);
            return field.getFloat(textView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static float getLineSpacingExtra(TextView textView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return textView.getLineSpacingExtra();
        }
        try {
            Field field = TextView.class.getDeclaredField("mSpacingAdd");
            field.setAccessible(true);
            return field.getFloat(textView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
