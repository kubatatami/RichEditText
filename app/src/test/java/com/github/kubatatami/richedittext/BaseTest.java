package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.example.TestActivity;
import com.github.kubatatami.richedittext.writer.PasteWriter;
import com.github.kubatatami.richedittext.writer.SuggestionWriter;
import com.github.kubatatami.richedittext.writer.TypeWriter;
import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22, packageName = "com.github.kubatatami.richedittext.example")
public class BaseTest {

    protected RichEditText editText;

    @ParameterizedRobolectricTestRunner.Parameters(name = "Writer = {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new TypeWriter()}, {new PasteWriter()}, {new SuggestionWriter()}
        });
    }

    protected Writer writer;

    public BaseTest(Writer writer) {
        this.writer = writer;
    }

    @Before
    public void init() {
        ShadowLog.stream = System.out;
        TestActivity activity = Robolectric.setupActivity(TestActivity.class);
        editText = (RichEditText) activity.findViewById(com.github.kubatatami.richedittext.example.R.id.rich_edit_text);
        editText.setText("");
    }

    protected void checkHtml(String validHtml) {
        assertEquals(validHtml, editText.getHtml(false));
    }

    @After
    public void testExportImport() {
        String html = editText.getHtml(false);
        editText.setHtml(html);
        assertEquals(html, editText.getHtml(false));
    }

}
