package com.github.kubatatami.richedittext.styles.base;

public class SpanInfo<T>{

    public int start;
    public int end;
    public int flags;
    public T span;

    public SpanInfo(int start, int end, int flags, T span) {
        this.start = start;
        this.end = end;
        this.flags = flags;
        this.span = span;
    }
}