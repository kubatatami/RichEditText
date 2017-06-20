package com.github.kubatatami.richedittext;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BinaryFontStyleTest.class,
        HtmlUtilsTest.class,
        InseparableTest.class,
        LineInfoTest.class,
        LineStyleTest.class,
        ListTest.class,
        MultiStyleTest.class,
        StyleSelectionInfoTest.class,
        TransformTest.class,
        WriterTest.class,
})
public class AllTests {

}