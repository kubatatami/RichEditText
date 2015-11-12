package com.github.kubatatami.richedittext;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import com.github.kubatatami.richedittext.styles.binary.BoldSpanController;
import com.github.kubatatami.richedittext.styles.binary.ItalicSpanController;
import com.github.kubatatami.richedittext.styles.binary.StrikeThroughSpanController;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;
import com.github.kubatatami.richedittext.styles.multi.AlignmentSpanController;
import com.github.kubatatami.richedittext.styles.multi.ColorSpanController;
import com.github.kubatatami.richedittext.styles.multi.LinkSpanController;
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
        registerController(StrikeThroughSpanController.class, new StrikeThroughSpanController());
        registerController(SizeSpanController.class, new SizeSpanController());
        registerController(ColorSpanController.class, new ColorSpanController());
        registerController(AlignmentSpanController.class, new AlignmentSpanController());
        registerController(LinkSpanController.class, new LinkSpanController());
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

    public void strikeThroughClick() {
        binaryClick(StrikeThroughSpanController.class);
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

    public void alignmentClick(Layout.Alignment alignment) {
        multiClick(alignment, AlignmentSpanController.class);
    }

    public void addLink(String url) {
        addLink(url, url);
    }

    public void addLink(String name, String url) {
        getText().replace(getSelectionStart(), getSelectionEnd(), name);
        getModule(LinkSpanController.class).add(url, getText(), getSelectionStart() - name.length(), getSelectionStart());
    }

    public void addOnAlignmentChangeListener(OnValueChangeListener<Layout.Alignment> onAlignmentChangeListener) {
        getModule(AlignmentSpanController.class).addOnValueChangeListener(onAlignmentChangeListener);
    }

    public void addOnSizeChangeListener(OnValueChangeListener<Float> onSizeChangeListener) {
        getModule(SizeSpanController.class).addOnValueChangeListener(onSizeChangeListener);
    }

    public void addOnColorChangeListener(OnValueChangeListener<Integer> onSizeChangeListener) {
        getModule(ColorSpanController.class).addOnValueChangeListener(onSizeChangeListener);
    }

    public void addOnBoldChangeListener(OnValueChangeListener<Boolean> onBoldChangeListener) {
        getModule(BoldSpanController.class).addOnValueChangeListener(onBoldChangeListener);
    }

    public void addOnItalicChangeListener(OnValueChangeListener<Boolean> onItalicChangeListener) {
        getModule(ItalicSpanController.class).addOnValueChangeListener(onItalicChangeListener);
    }

    public void addOnStrikethroughChangeListener(OnValueChangeListener<Boolean> onStrikethroughChangeListener) {
        getModule(StrikeThroughSpanController.class).addOnValueChangeListener(onStrikethroughChangeListener);
    }

    public void addOnUnderlineChangeListener(OnValueChangeListener<Boolean> onUnderlineChangeListener) {
        getModule(UnderlineSpanController.class).addOnValueChangeListener(onUnderlineChangeListener);
    }

}
