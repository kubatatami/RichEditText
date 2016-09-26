package com.github.kubatatami.richedittext;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import com.github.kubatatami.richedittext.modules.StyleSelectionInfo;
import com.github.kubatatami.richedittext.properties.LineHeight;
import com.github.kubatatami.richedittext.styles.binary.BoldSpanController;
import com.github.kubatatami.richedittext.styles.binary.ItalicSpanController;
import com.github.kubatatami.richedittext.styles.binary.StrikeThroughSpanController;
import com.github.kubatatami.richedittext.styles.binary.UnderlineSpanController;
import com.github.kubatatami.richedittext.styles.line.AlignmentSpanController;
import com.github.kubatatami.richedittext.styles.list.BulletListController;
import com.github.kubatatami.richedittext.styles.list.NumberListController;
import com.github.kubatatami.richedittext.styles.multi.BackgroundColorSpanController;
import com.github.kubatatami.richedittext.styles.multi.ColorSpanController;
import com.github.kubatatami.richedittext.styles.multi.LinkSpanController;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;
import com.github.kubatatami.richedittext.styles.multi.TypefaceSpanController;

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

    private void init() {
        initBinary();
        initMulti();
        initList();
        registerProperty(new LineHeight());
    }

    private void initList() {
        NumberListController numberListController = new NumberListController();
        BulletListController bulletListController = new BulletListController();
        numberListController.setOppositeController(bulletListController);
        bulletListController.setOppositeController(numberListController);
        registerController(NumberListController.class, numberListController);
        registerController(BulletListController.class, bulletListController);
    }

    private void initMulti() {
        registerController(SizeSpanController.class, new SizeSpanController());
        registerController(ColorSpanController.class, new ColorSpanController());
        registerController(BackgroundColorSpanController.class, new BackgroundColorSpanController());
        registerController(AlignmentSpanController.class, new AlignmentSpanController());
        registerController(LinkSpanController.class, new LinkSpanController());
        registerController(TypefaceSpanController.class, new TypefaceSpanController());
    }

    private void initBinary() {
        registerController(BoldSpanController.class, new BoldSpanController());
        registerController(ItalicSpanController.class, new ItalicSpanController());
        registerController(UnderlineSpanController.class, new UnderlineSpanController());
        registerController(StrikeThroughSpanController.class, new StrikeThroughSpanController());
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

    public void numberListClick() {
        binaryClick(NumberListController.class);
    }

    public void bulletListClick() {
        binaryClick(BulletListController.class);
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

    public void backgroundColorClick(int color) {
        multiClick(color, BackgroundColorSpanController.class);
    }

    public void alignmentClick(Layout.Alignment alignment) {
        multiClick(alignment, AlignmentSpanController.class);
    }

    public void typefaceClick(TypefaceSpanController.Font font) {
        multiClick(font.getFontName(), TypefaceSpanController.class);
    }

    public void typefaceClick(String font) {
        multiClick(font, TypefaceSpanController.class);
    }

    public void addLink(String alt, String url) {
        int start = getSelectionStart();
        int stop = getSelectionEnd();
        LinkSpanController.Link link = new LinkSpanController.Link(url, alt);
        if (start == stop) {
            getText().replace(getSelectionStart(), getSelectionEnd(), url);
            start = getSelectionStart() - url.length();
            stop = getSelectionStart();
        }
        getModule(LinkSpanController.class).add(link, getText(), start, stop);
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

    public void addOnBackgroundColorChangeListener(OnValueChangeListener<Integer> onSizeChangeListener) {
        getModule(BackgroundColorSpanController.class).addOnValueChangeListener(onSizeChangeListener);
    }

    public void addOnBoldChangeListener(OnValueChangeListener<Boolean> onBoldChangeListener) {
        getModule(BoldSpanController.class).addOnValueChangeListener(onBoldChangeListener);
    }

    public void addOnNumberListChangeListener(OnValueChangeListener<Boolean> onNumberListChangeListener) {
        getModule(NumberListController.class).addOnValueChangeListener(onNumberListChangeListener);
    }

    public void addOnBulletListChangeListener(OnValueChangeListener<Boolean> onBulletListChangeListener) {
        getModule(BulletListController.class).addOnValueChangeListener(onBulletListChangeListener);
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

    public void addOnFontChangeListener(OnValueChangeListener<String> onFontChangeListener) {
        getModule(TypefaceSpanController.class).addOnValueChangeListener(onFontChangeListener);
    }

    public void addOnLinkChangeListener(OnValueChangeListener<LinkSpanController.Link> onLinkChangeListener) {
        getModule(LinkSpanController.class).addOnValueChangeListener(onLinkChangeListener);
    }

    public String getOverallFont() {
        return getModule(TypefaceSpanController.class).getCurrentValue(this, getAllSelectionInfo());
    }

    public String getCurrentFont() {
        return getModule(TypefaceSpanController.class).getCurrentValue(this, StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setSize(float size) {
        getModule(SizeSpanController.class).perform(size, getText(), getAllSelectionInfo());
    }

    public Float getOverallSize() {
        return getModule(SizeSpanController.class).getCurrentValue(this, getAllSelectionInfo());
    }

    public Float getCurrentSize() {
        return getModule(SizeSpanController.class).getCurrentValue(this, StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setColor(int color) {
        getModule(ColorSpanController.class).perform(color, getText(), getAllSelectionInfo());
    }

    public Integer getOverallColor() {
        return getModule(ColorSpanController.class).getCurrentValue(this, getAllSelectionInfo());
    }

    public Integer getCurrentColor() {
        return getModule(ColorSpanController.class).getCurrentValue(this, StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setFontBackgroundColor(int color) {
        getModule(BackgroundColorSpanController.class).perform(color, getText(), getAllSelectionInfo());
    }

    public Integer getOverallBackgroundColor() {
        return getModule(BackgroundColorSpanController.class).getCurrentValue(this, getAllSelectionInfo());
    }

    public Integer getCurrentBackgroundColor() {
        return getModule(BackgroundColorSpanController.class).getCurrentValue(this, StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setAlignment(Layout.Alignment alignment) {
        getModule(AlignmentSpanController.class).perform(alignment, getText(), getAllSelectionInfo());
    }

    public Layout.Alignment getOverallAlignment() {
        return getModule(AlignmentSpanController.class).getCurrentValue(this, getAllSelectionInfo());
    }

    public Layout.Alignment getCurrentAlignment() {
        return getModule(AlignmentSpanController.class).getCurrentValue(this, StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setBold(boolean bold) {
        if (bold) {
            getModule(BoldSpanController.class).selectStyle(getText(), getAllSelectionInfo());
        } else {
            getModule(BoldSpanController.class).clearStyles(getText(), getAllSelectionInfo());
        }
    }

    public boolean getOverallNumberList() {
        return getModule(NumberListController.class).getCurrentValue(getText(), getAllSelectionInfo());
    }

    public boolean getCurrentNumberList() {
        return getModule(NumberListController.class).getCurrentValue(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public boolean getOverallBulletList() {
        return getModule(BulletListController.class).getCurrentValue(getText(), getAllSelectionInfo());
    }

    public boolean getCurrentBulletList() {
        return getModule(BulletListController.class).getCurrentValue(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public boolean getOverallBold() {
        return getModule(BoldSpanController.class).getCurrentValue(getText(), getAllSelectionInfo());
    }

    public boolean getCurrentBold() {
        return getModule(BoldSpanController.class).getCurrentValue(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setItalic(boolean italic) {
        if (italic) {
            getModule(ItalicSpanController.class).selectStyle(getText(), getAllSelectionInfo());
        } else {
            getModule(ItalicSpanController.class).clearStyles(getText(), getAllSelectionInfo());
        }
    }

    public boolean getOverallItalic() {
        return getModule(ItalicSpanController.class).getCurrentValue(getText(), getAllSelectionInfo());
    }

    public boolean getCurrentItalic() {
        return getModule(ItalicSpanController.class).getCurrentValue(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setUnderline(boolean underline) {
        if (underline) {
            getModule(UnderlineSpanController.class).selectStyle(getText(), getAllSelectionInfo());
        } else {
            getModule(UnderlineSpanController.class).clearStyles(getText(), getAllSelectionInfo());
        }
    }

    public boolean getOverallUnderline() {
        return getModule(UnderlineSpanController.class).getCurrentValue(getText(), getAllSelectionInfo());
    }

    public boolean getCurrentUnderline() {
        return getModule(UnderlineSpanController.class).getCurrentValue(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void setStrikeThrough(boolean strikeThrough) {
        if (strikeThrough) {
            getModule(StrikeThroughSpanController.class).selectStyle(getText(), getAllSelectionInfo());
        } else {
            getModule(StrikeThroughSpanController.class).clearStyles(getText(), getAllSelectionInfo());
        }
    }

    public boolean getOverallStrikeThrough() {
        return getModule(StrikeThroughSpanController.class).getCurrentValue(getText(), getAllSelectionInfo());
    }

    public boolean getCurrentStrikeThrough() {
        return getModule(StrikeThroughSpanController.class).getCurrentValue(getText(), StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public LinkSpanController.Link getCurrentLink() {
        return getModule(LinkSpanController.class).getCurrentValue(this, StyleSelectionInfo.getStyleSelectionInfo(this));
    }

    public void changeCurrentLinkText(String text) {
        changeLinkText(getSelectionStart(), text);
    }

    public void changeLinkText(int pos, String text) {
        LinkSpanController.RichURLSpan[] spans = getText().getSpans(pos, pos, LinkSpanController.RichURLSpan.class);
        if (spans.length == 0) {
            throw new IllegalStateException("No link span at this position.");
        } else if (spans.length > 1) {
            throw new IllegalStateException("More than one link span at this position.");
        }
        changeTextOnSpan(text, spans[0]);
    }

    public String getCurrentLinkText() {
        LinkSpanController.Link link = getCurrentLink();
        if (link == null) {
            throw new IllegalStateException("No current link.");
        }
        return getSpanText(link.getSpan());
    }

    public void setAutoUrlFix(boolean autoUrlFix) {
        getModule(LinkSpanController.class).setAutoUrlFix(autoUrlFix);
    }

    public void setLinkInseparable(boolean inseparable) {
        getModule(LinkSpanController.class).setInseparable(inseparable);
    }

    public void clearAllListeners() {
        clearOnFocusChangeListeners();
        clearOnHistoryChangeListeners();
        clearValueChangeListeners();
    }

}
