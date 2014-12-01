package com.github.kubatatami.richedittext.styles.base;

public class SpanInfo<T> {

    public int start;
    public int end;
    public int flags;
    public int textLength;
    public T span;

    public SpanInfo(int start, int end,int textLength, int flags, T span) {
        this.start = start;
        this.end = end;
        this.textLength = textLength;
        this.flags = flags;
        this.span = span;
    }
}