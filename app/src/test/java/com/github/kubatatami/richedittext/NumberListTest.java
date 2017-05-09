package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;

public class NumberListTest extends BaseTest {

    private static final String FIRST_ITEM = "First item\n";

    private static final String SECOND_ITEM = "Second item\n";

    private static final String THIRD_ITEM = "Third item\n";

    public NumberListTest(Writer writer) {
        super(writer);
    }

    @Test
    public void exportedHtmlShouldContainsNumberListWith3Items() {
        editText.numberListClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        checkHtml("<ol><li>First item</li><li>Second item</li><li>Third item</li></ol>");
    }

    @Test
    public void exportedHtmlShouldContainsNumberListWith2Items() {
        editText.numberListClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(FIRST_ITEM.length());
        writer.delete(editText);
        checkHtml("<ol><li>First itemSecond item</li><li>Third item</li></ol>");
    }

    @Test
    public void exportedHtmlShouldContainsNumberListWith2ItemsVer2() {
        editText.numberListClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(FIRST_ITEM.length() + SECOND_ITEM.length());
        for (int i = 0; i < SECOND_ITEM.length(); i++) {
            writer.delete(editText);
        }
        checkHtml("<ol><li>First item</li><li>Third item</li></ol>");
    }

}
