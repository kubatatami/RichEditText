package com.github.kubatatami.richedittext;

import android.text.Layout;

import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

public class LineStyleTest extends BaseTest {

    public LineStyleTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsLeftCenterAndRightAlignment() {
        editText.alignmentClick(Layout.Alignment.ALIGN_NORMAL);
        writer.write(editText, "Left.\n");
        editText.alignmentClick(Layout.Alignment.ALIGN_CENTER);
        writer.write(editText, "Center.\n");
        editText.alignmentClick(Layout.Alignment.ALIGN_OPPOSITE);
        writer.write(editText, "Right.\n");
        checkHtml("");
    }

}
