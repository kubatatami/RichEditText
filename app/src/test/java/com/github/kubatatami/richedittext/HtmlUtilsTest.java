package com.github.kubatatami.richedittext;

import android.graphics.Color;

import com.github.kubatatami.richedittext.utils.HtmlUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22, packageName = "com.github.kubatatami.richedittext.example")
public class HtmlUtilsTest {

    @Test
    public void testSolid() {
        test(Color.GRAY);
    }

    @Test
    public void testAlpha() {
        test(Color.argb(51, 0, 0, 0));
    }

    private void test(int color) {
        String html = HtmlUtils.getColor(color);
        assertEquals(HtmlUtils.parseColor(html), color);
    }
}