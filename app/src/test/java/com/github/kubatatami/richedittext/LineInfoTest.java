package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.modules.LineInfo;
import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineInfoTest extends BaseWriterTest {

    public LineInfoTest(Writer writer) {
        super(writer);
    }

    @Test
    public void lineInfoShouldReturnValidStartAndEnd() {
        LineInfo lineInfo = getLineInfo();
        assertEquals(0, lineInfo.start);
        assertEquals(0, lineInfo.end);
    }

    @Test
    public void lineInfoShouldReturnValidStartAndEndVer2() {
        writer.write(editText, "Test text.");
        LineInfo lineInfo = getLineInfo();
        assertEquals(0, lineInfo.start);
        assertEquals(10, lineInfo.end);
    }

    @Test
    public void lineInfoShouldReturnValidStartAndEndVer3() {
        writer.write(editText, "Test text.\nSecond line.");
        LineInfo lineInfo = getLineInfo();
        assertEquals(11, lineInfo.start);
        assertEquals(23, lineInfo.end);
    }

    private LineInfo getLineInfo() {
        StyleSelectionInfo styleSelectionInfo = StyleSelectionInfo.getStyleSelectionInfo(editText);
        return LineInfo.getLineInfo(editText.getText(), styleSelectionInfo);
    }

}
