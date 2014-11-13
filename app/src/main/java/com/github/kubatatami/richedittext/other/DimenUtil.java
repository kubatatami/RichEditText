package com.github.kubatatami.richedittext.other;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Kuba on 12/11/14.
 */
public class DimenUtil {

    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public static float convertDpToPixel(float dp){
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}
