package com.github.kubatatami.richedittext.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;

public class HtmlUtils {

    @SuppressLint("DefaultLocale")
    public static String getColor(int color) {
        if (Color.alpha(color) == 255) {
            return String.format("rgb(%d, %d, %d)", Color.red(color), Color.green(color), Color.blue(color));
        } else {
            return String.format("rgba(%d, %d, %d, %.2f)", Color.red(color), Color.green(color), Color.blue(color), getAlphaFloat(color));
        }
    }

    private static float getAlphaFloat(int color) {
        return ((float) Color.alpha(color)) / 255f;
    }

    public static int parseColor(String styleValue) {
        try {
            if (styleValue.contains("#")) {
                return Color.parseColor(styleValue);
            } else if (styleValue.contains("rgba")) {
                String[] values = getColorValues(styleValue);
                return Color.argb(
                        (int) (Float.parseFloat(values[3]) * 255f),
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]));
            } else if (styleValue.contains("rgb")) {
                String[] values = getColorValues(styleValue);
                return Color.rgb(
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @NonNull
    private static String[] getColorValues(String styleValue) {
        styleValue = styleValue.replaceAll(" ", "");
        styleValue = styleValue.substring(styleValue.indexOf('(') + 1);
        styleValue = styleValue.substring(0, styleValue.indexOf(')'));
        return styleValue.split(",");
    }
}
