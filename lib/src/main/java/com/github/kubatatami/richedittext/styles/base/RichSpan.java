package com.github.kubatatami.richedittext.styles.base;

public interface RichSpan {

    int PRIORITY_NORMAL = 0;
    int PRIORITY_HIGH = 1;
    int PRIORITY_HIGHER = 2;

    int getPriority();
}
