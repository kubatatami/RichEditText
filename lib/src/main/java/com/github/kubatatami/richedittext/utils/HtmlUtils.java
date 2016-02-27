package com.github.kubatatami.richedittext.utils;

import android.graphics.Color;

public class HtmlUtils {

    public static String getColor(int color) {
        String result = Integer.toHexString(color + 0x01000000);
        while (result.length() < 6) {
            result = "0" + result;
        }
        return "#" + result;
    }

    public static int parseColor(String styleValue) {
        if (styleValue.contains("#")) {
            return Color.parseColor(styleValue);
        } else if (styleValue.contains("rgb")) {
            try {
                styleValue = styleValue.replaceAll(" ", "");
                styleValue = styleValue.substring(styleValue.indexOf('(') + 1);
                styleValue = styleValue.substring(0, styleValue.indexOf(')'));
                String[] rgb = styleValue.split(",");
                return Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return 0;
    }
}
