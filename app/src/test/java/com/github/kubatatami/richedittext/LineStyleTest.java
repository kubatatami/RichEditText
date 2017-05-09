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
}
