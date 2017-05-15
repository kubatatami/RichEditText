package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.styles.base.BinarySpanController;
import com.github.kubatatami.richedittext.styles.list.BulletListController;
import com.github.kubatatami.richedittext.styles.list.ListController;
import com.github.kubatatami.richedittext.styles.list.NumberListController;
import com.github.kubatatami.richedittext.writer.PasteWriter;
import com.github.kubatatami.richedittext.writer.SuggestionWriter;
import com.github.kubatatami.richedittext.writer.TypeWriter;
import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.Test;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.util.Arrays;
import java.util.Collection;

public class ListTest extends BaseTest {

    private final String tag;

    private final ListController controller;

    @ParameterizedRobolectricTestRunner.Parameters(name = "Writer ={0}, Tag = {1}, ControllerClass = {2}")
    public static Collection<Object[]> tags() {
        return Arrays.asList(new Object[][]{
                {new TypeWriter(), new NumberListController()},
                {new PasteWriter(), new NumberListController()},
                {new SuggestionWriter(), new NumberListController()},
                {new TypeWriter(), new BulletListController()},
                {new PasteWriter(), new BulletListController()},
                {new SuggestionWriter(), new BulletListController()}
        });
    }

    private static final String FIRST_ITEM = "First item\n";

    private static final String SECOND_ITEM = "Second item\n";

    private static final String THIRD_ITEM = "Third item\n";

    public ListTest(Writer writer, ListController controller) {
        super(writer);
        this.tag = controller.getTagName();
        this.controller = controller;
    }

    @Test
    public void exportedHtmlShouldContainsListWith3Items() {
        listClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        checkHtml(String.format("<%1$s><li>First item</li><li>Second item</li><li>Third item</li></%1$s>", tag));
    }

    @Test
    public void exportedHtmlShouldContainsListWith2Items() {
        listClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(FIRST_ITEM.length());
        writer.delete(editText);
        checkHtml(String.format("<%1$s><li>First itemSecond item</li><li>Third item</li></%1$s>", tag));
    }

    @Test
    public void exportedHtmlShouldContainsListWith2ItemsVer2() {
        listClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(FIRST_ITEM.length() + SECOND_ITEM.length());
        for (int i = 0; i < SECOND_ITEM.length(); i++) {
            writer.delete(editText);
        }
        checkHtml(String.format("<%1$s><li>First item</li><li>Third item</li></%1$s>", tag));
    }

    @Test
    public void exportedHtmlShouldContainsTwoLists() {
        listClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(FIRST_ITEM.length() + SECOND_ITEM.length() / 2);
        listClick();
        checkHtml(String.format("<%1$s><li>First item</li></%1$s>Second item<%1$s><li>Third item</li></%1$s>", tag));
    }

    @Test
    public void exportedHtmlShouldContainsOneLists() {
        listClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, "\n");
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(0, editText.length());
        listClick();
        checkHtml(String.format("<%1$s><li>First item</li><li>Second item</li><li>Third item</li></%1$s>", tag));
    }

    @Test
    public void exportedHtmlShouldNotContainAnyLists() {
        listClick();
        writer.write(editText, FIRST_ITEM);
        writer.write(editText, SECOND_ITEM);
        writer.write(editText, "\n");
        writer.write(editText, THIRD_ITEM);
        editText.setSelection(0, editText.length());
        listClick();
        listClick();
        checkHtml("First item<br/>Second item<br/>Third item<br/>");
    }

    @SuppressWarnings("unchecked")
    private void listClick() {
        editText.binaryClick((Class<? extends BinarySpanController<?>>) controller.getClass());
    }
}
