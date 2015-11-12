package com.github.kubatatami.richedittext.styles.base;

public class SpanInfo<T> {

    public final int start;

    public final int end;

    public final int flags;

    public final T span;

    public SpanInfo(int start, int end, int flags, T span) {
        this.start = start;
        this.end = end;
        this.flags = flags;
        this.span = span;
    }
}