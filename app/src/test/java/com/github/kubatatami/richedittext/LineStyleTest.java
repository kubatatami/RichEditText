package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

import static android.text.Layout.Alignment.ALIGN_CENTER;
import static android.text.Layout.Alignment.ALIGN_NORMAL;
import static android.text.Layout.Alignment.ALIGN_OPPOSITE;

public class LineStyleTest extends BaseTest {

    public LineStyleTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsLeftCenterAndRightAlignment() {
        editText.alignmentClick(ALIGN_NORMAL);
        writer.write(editText, "Left.\n");

        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "Center.\n");

        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "Right.\n");

        checkHtml("<div style=\"text-align: left;\">Left.</div><div style=\"text-align: center;\">Center.</div><div style=\"text-align: right;\">Right.<br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsLeftCenterAndRightAlignmentVer2() {
        writer.write(editText, "Left.");
        editText.alignmentClick(ALIGN_NORMAL);
        writer.write(editText, "\n");

        writer.write(editText, "Center.");
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "\n");

        writer.write(editText, "Right.");
        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "\n");

        checkHtml("<div style=\"text-align: left;\">Left.</div><div style=\"text-align: center;\">Center.</div><div style=\"text-align: right;\">Right.<br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsLeftCenterAndRightAlignmentVer3() {
        writer.write(editText, "Left.\n");

        writer.write(editText, "Center.");
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "\n");

        writer.write(editText, "Right.");
        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "\n");

        checkHtml("Left.<br/><div style=\"text-align: center;\">Center.</div><div style=\"text-align: right;\">Right.<br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsLeftAndCenterAlignment() {
        writer.write(editText, "Left.");
        editText.alignmentClick(ALIGN_NORMAL);
        writer.write(editText, "\n");

        writer.write(editText, "Center.");
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "\n");

        writer.write(editText, "Right.");
        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "\n");

        editText.setSelection(14);
        writer.delete(editText);

        checkHtml("<div style=\"text-align: left;\">Left.</div><div style=\"text-align: center;\">Center.Right.<br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsLeftAlignment() {
        writer.write(editText, "Left.");
        editText.alignmentClick(ALIGN_NORMAL);
        writer.write(editText, "\n");

        writer.write(editText, "Center.");
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "\n");

        writer.write(editText, "Right.");
        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "\n");

        editText.setSelection(14);
        writer.delete(editText);
        editText.setSelection(6);
        writer.delete(editText);

        checkHtml("<div style=\"text-align: left;\">Left.Center.Right.<br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsLeftAlignmentVer2() {
        writer.write(editText, "Left.");
        editText.alignmentClick(ALIGN_NORMAL);
        writer.write(editText, "\n");

        writer.write(editText, "Center.");
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "\n");

        writer.write(editText, "Right.");
        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "\n");

        editText.setSelection(6);
        writer.delete(editText);
        editText.setSelection(13);
        writer.delete(editText);

        checkHtml("<div style=\"text-align: left;\">Left.Center.Right.<br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsLeftCenterAndRightAlignmentWithSpaces() {
        editText.alignmentClick(ALIGN_NORMAL);
        writer.write(editText, " Left.\n");

        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, " Center.\n");

        editText.alignmentClick(ALIGN_OPPOSITE);
        writer.write(editText, "Right. \n");

        checkHtml("<div style=\"text-align: left;\">&nbsp;Left.</div><div style=\"text-align: center;\">&nbsp;Center.</div><div style=\"text-align: right;\">Right. <br/></div>");
    }

    @Test
    public void exportedHtmlShouldContainsCenterAlignment() {
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "Long text.");
        editText.setSelection(4);
        writer.write(editText, "\n");

        checkHtml("<div style=\"text-align: center;\">Long<br/> text.</div>");
    }


    @Test
    public void exportedHtmlShouldContainsCenterAlignmentVer2() {
        editText.alignmentClick(ALIGN_CENTER);
        writer.write(editText, "Line 1.\n");
        writer.write(editText, "Line 2.\n");
        writer.write(editText, "Line 3.\n");
        editText.selectAll();
        editText.setSelection(10);

        checkHtml("<div style=\"text-align: center;\">Line 1.</div><div style=\"text-align: center;\">Line 2.</div><div style=\"text-align: center;\">Line 3.</div>");
    }
}
