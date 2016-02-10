package com.github.kubatatami.richedittext.other;

import android.support.annotation.NonNull;

public class StringUtils {

    @NonNull
    public static String join(String[] values, String delimiter) {
        int i = 0;
        final StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(value);
            if (i < values.length - 1) {
                sb.append(delimiter);
            }
            i++;
        }
        return sb.toString();
    }

}
