package com.github.kubatatami.richedittext;

import com.github.kubatatami.richedittext.writer.PasteWriter;
import com.github.kubatatami.richedittext.writer.SuggestionWriter;
import com.github.kubatatami.richedittext.writer.TypeWriter;
import com.github.kubatatami.richedittext.writer.Writer;

import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;

import java.util.Arrays;
import java.util.Collection;

@RunWith(ParameterizedRobolectricTestRunner.class)
public class BaseWriterTest extends BaseTest {

    @ParameterizedRobolectricTestRunner.Parameters(name = "Writer = {0}")
    public static Collection<Object[]> writers() {
        return Arrays.asList(new Object[][]{
                {new TypeWriter()}, {new PasteWriter()}, {new SuggestionWriter()}
        });
    }

    Writer writer;

    BaseWriterTest(Writer writer) {
        this.writer = writer;
    }

}
