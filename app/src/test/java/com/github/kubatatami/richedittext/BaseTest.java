package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.example.TestActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, packageName = "com.github.kubatatami.richedittext.example")
public class BaseTest {

    RichEditText editText;

    @Before
    public void init() {
        ShadowLog.stream = System.out;
        TestActivity activity = Robolectric.setupActivity(TestActivity.class);
        activity.setLivePreview(false);
        editText = activity.findViewById(com.github.kubatatami.richedittext.example.R.id.rich_edit_text);
        editText.setText("");
        editText.setSelection(0);
    }

    void checkHtml(String validHtml) {
        assertEquals(validHtml, editText.getHtml(false));
    }

    @After
    public void testExportImport() {
        testStandalone();
        testNonStandalone();
    }

    private void testStandalone() {
        String html = editText.getHtml(true);
        editText.setHtml(html, null);
        assertEquals(html, editText.getHtml(true));
    }

    private void testNonStandalone() {
        String html = editText.getHtml(false);
        String cssStyle = editText.getCssStyle();
        editText.setHtml(html, cssStyle);
        assertEquals(html, editText.getHtml(false));
    }
}
