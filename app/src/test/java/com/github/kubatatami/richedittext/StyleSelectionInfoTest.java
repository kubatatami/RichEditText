package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StyleSelectionInfoTest extends BaseTest {

    public StyleSelectionInfoTest(Writer writer) {
        super(writer);
    }

    @Test
    public void lineInfoShouldReturnValidStartAndEnd() {
        StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(editText);
        assertEquals(0, styleSelectionInfo.selectionStart);
        assertEquals(0, styleSelectionInfo.selectionEnd);
        assertEquals(0, styleSelectionInfo.realSelectionStart);
        assertEquals(0, styleSelectionInfo.realSelectionEnd);
    }

    @Test
    public void lineInfoShouldReturnValidStartAndEndVer2() {
        writer.write(editText, "Test text.");
        StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(editText);
        assertEquals(10, styleSelectionInfo.selectionStart);
        assertEquals(10, styleSelectionInfo.selectionEnd);
        assertEquals(10, styleSelectionInfo.realSelectionStart);
        assertEquals(10, styleSelectionInfo.realSelectionEnd);
    }

    @Test
    public void lineInfoShouldReturnValidStartAndEndVer3() {
        writer.write(editText, "Test text.");
        editText.setSelection(9);
        StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(editText);
        assertEquals(5, styleSelectionInfo.selectionStart);
        assertEquals(10, styleSelectionInfo.selectionEnd);
        assertEquals(9, styleSelectionInfo.realSelectionStart);
        assertEquals(9, styleSelectionInfo.realSelectionEnd);
    }

}
