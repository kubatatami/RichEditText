package com.github.kubatatami.richedittext;

import android.content.Context;
import android.util.AttributeSet;

import com.github.kubatatami.richedittext.styles.binary.BoldSpanController;
import com.github.kubatatami.richedittext.styles.binary.ItalicSpanController;
import com.github.kubatatami.richedittext.styles.binary.StrikethroughSpanController;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;
import com.github.kubatatami.richedittext.styles.multi.ColorSpanController;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;

/**
 * Created by Kuba on 20/11/14.
 */
public class RichEditText extends BaseRichEditText {
    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        registerController(BoldSpanController.class, new BoldSpanController());
        registerController(ItalicSpanController.class, new ItalicSpanController());
        registerController(UnderlineSpanController.class, new UnderlineSpanController());
        registerController(StrikethroughSpanController.class, new StrikethroughSpanController());
        registerController(SizeSpanController.class, new SizeSpanController());
        registerController(ColorSpanController.class, new ColorSpanController());
        setHorizontallyScrolling(true);
        setHorizontalScrollBarEnabled(true);
    }

    public void boldClick() {
        binaryClick(BoldSpanController.class);
    }

    public void underlineClick() {
        binaryClick(UnderlineSpanController.class);
    }

    public void italicClick() {
        binaryClick(ItalicSpanController.class);
    }

    public void strikethroughClick() {
        binaryClick(StrikethroughSpanController.class);
    }

    public void sizeClick(SizeSpanController.Size size) {
        sizeClick(size.getSize());
    }

    public void sizeClick(float size) {
        multiClick(size, SizeSpanController.class);
    }

    public void colorClick(int color) {
        multiClick(color, ColorSpanController.class);
    }

    public void setOnSizeChangeListener(OnValueChangeListener<Float> onSizeChangeListener) {
        getModule(SizeSpanController.class).setOnValueChangeListener(onSizeChangeListener);
    }

    public void setOnColorChangeListener(OnValueChangeListener<Integer> onSizeChangeListener) {
        getModule(ColorSpanController.class).setOnValueChangeListener(onSizeChangeListener);
    }

    public void setOnBoldChangeListener(OnValueChangeListener<Boolean> onBoldChangeListener) {
        getModule(BoldSpanController.class).setOnValueChangeListener(onBoldChangeListener);
    }

    public void setOnItalicChangeListener(OnValueChangeListener<Boolean> onItalicChangeListener) {
        getModule(ItalicSpanController.class).setOnValueChangeListener(onItalicChangeListener);
    }

    public void setOnStrikethroughChangeListener(OnValueChangeListener<Boolean> onStrikethroughChangeListener) {
        getModule(StrikethroughSpanController.class).setOnValueChangeListener(onStrikethroughChangeListener);
    }

    public void setOnUnderlineChangeListener(OnValueChangeListener<Boolean> onUnderlineChangeListener) {
        getModule(UnderlineSpanController.class).setOnValueChangeListener(onUnderlineChangeListener);
    }

}
