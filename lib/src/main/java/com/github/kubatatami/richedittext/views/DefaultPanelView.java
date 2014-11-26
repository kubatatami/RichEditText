package com.github.kubatatami.richedittext.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.R;
import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;
import com.larswerkman.holocolorpicker.ColorPicker;

import java.lang.reflect.Field;

/**
 * Created by Kuba on 26/11/14.
 */
public class DefaultPanelView extends LinearLayout {

    ToggleButton boldButton, italicButton, underlineButton, strikethroughButton;
    ToggleButton leftButton, centerButton, rightButton;
    Button undoButton, redoButton;
    Spinner fontSizeSpinner;
    ArrayAdapter<SizeSpanController.Size> adapter;
    ColorPicker colorPicker;
    boolean ignoreSizeEvent, ignoreColorEvent;


    public DefaultPanelView(Context context) {
        super(context);
        init();
    }

    public DefaultPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DefaultPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DefaultPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.default_panel, this);
    }

    public void connectWithRichEditText(final RichEditText richEditText){

        boldButton = (ToggleButton) findViewById(R.id.bold_button);
        italicButton = (ToggleButton) findViewById(R.id.italic_button);
        underlineButton = (ToggleButton) findViewById(R.id.underline_button);
        strikethroughButton = (ToggleButton) findViewById(R.id.strikethrough_button);

        leftButton = (ToggleButton) findViewById(R.id.left_button);
        centerButton = (ToggleButton) findViewById(R.id.center_button);
        rightButton = (ToggleButton) findViewById(R.id.right_button);


        fontSizeSpinner = (Spinner) findViewById(R.id.font_size_spinner);
        colorPicker = (ColorPicker) findViewById(R.id.color_picker);

        undoButton = (Button) findViewById(R.id.undo_button);
        redoButton = (Button) findViewById(R.id.redo_button);


        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.boldClick();
            }
        });
        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.italicClick();
            }
        });
        underlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.underlineClick();
            }
        });
        strikethroughButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.strikethroughClick();
            }
        });
        Spannable spannableString = new SpannableString("U");
        spannableString.setSpan(new UnderlineSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        underlineButton.setText(spannableString);
        underlineButton.setTextOn(spannableString);
        underlineButton.setTextOff(spannableString);

        spannableString = new SpannableString("S");
        spannableString.setSpan(new StrikethroughSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        strikethroughButton.setText(spannableString);
        strikethroughButton.setTextOn(spannableString);
        strikethroughButton.setTextOff(spannableString);


        adapter = new ArrayAdapter<SizeSpanController.Size>(getContext(), android.R.layout.simple_spinner_item,
                android.R.id.text1, SizeSpanController.Size.values());
        fontSizeSpinner.setAdapter(adapter);
        fontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!ignoreSizeEvent && !first) {
                    richEditText.sizeClick(adapter.getItem(position));
                }
                first = false;
                ignoreSizeEvent = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.undo();
            }
        });
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.redo();
            }
        });

        richEditText.setOnBoldChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean bold) {
                boldButton.setChecked(bold);
            }
        });
        richEditText.setOnItalicChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean italic) {
                italicButton.setChecked(italic);
            }
        });
        richEditText.setOnUnderlineChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean underline) {
                underlineButton.setChecked(underline);
            }
        });
        richEditText.setOnStrikethroughChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean strikethroug) {
                strikethroughButton.setChecked(strikethroug);
            }
        });
        richEditText.addOnHistoryChangeListener(new HistoryModule.OnHistoryChangeListener() {
            @Override
            public void onHistoryChange(int undoSteps, int redoSteps) {
                undoButton.setEnabled(undoSteps > 0);
                redoButton.setEnabled(redoSteps > 0);
                undoButton.setText("<-(" + undoSteps + ")");
                redoButton.setText("->(" + redoSteps + ")");
            }
        });
        richEditText.setOnSizeChangeListener(new BaseRichEditText.OnValueChangeListener<Float>() {
            @Override
            public void onValueChange(Float size) {
                ignoreSizeEvent = true;
                for (int i = 0; i < adapter.getCount(); i++) {
                    SizeSpanController.Size sizeEnum = adapter.getItem(i);
                    if (sizeEnum.getSize() == size) {
                        fontSizeSpinner.setSelection(i);
                        return;
                    }
                }

            }
        });
        richEditText.setOnColorChangeListener(new BaseRichEditText.OnValueChangeListener<Integer>() {
            @Override
            public void onValueChange(Integer value) {
                ignoreColorEvent = true;
                colorPicker.setNewCenterColor(value);
            }
        });
        richEditText.setOnAlignmentChangeListener(new BaseRichEditText.OnValueChangeListener<Layout.Alignment>() {

            @Override
            public void onValueChange(Layout.Alignment value) {
                setChecked(leftButton, value!=null && value.equals(Layout.Alignment.ALIGN_NORMAL));
                setChecked(centerButton, value!=null && value.equals(Layout.Alignment.ALIGN_CENTER));
                setChecked(rightButton, value!=null && value.equals(Layout.Alignment.ALIGN_OPPOSITE));
            }
        });
        leftButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked(centerButton, false);
                setChecked(rightButton, false);
                richEditText.alignmentClick(Layout.Alignment.ALIGN_NORMAL);
                setChecked(buttonView, true);
            }
        });
        centerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked(leftButton, false);
                setChecked(rightButton, false);
                richEditText.alignmentClick(Layout.Alignment.ALIGN_CENTER);
                setChecked(buttonView, true);
            }
        });
        rightButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setChecked(leftButton, false);
                setChecked(centerButton, false);
                richEditText.alignmentClick(Layout.Alignment.ALIGN_OPPOSITE);
                setChecked(buttonView, true);
            }
        });
        richEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, adapter.getItem(0).getSize());
        richEditText.setHistoryLimit(20);
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            boolean first = true;

            @Override
            public void onColorChanged(int i) {
                if (!ignoreColorEvent && !first) {
                    richEditText.colorClick(i);
                }
                first = false;
                ignoreColorEvent = false;
            }
        });

    }

    protected void setChecked(CompoundButton checkBox, boolean checked) {
        try {
            Field field = CompoundButton.class.getDeclaredField("mBroadcasting");
            field.setAccessible(true);
            field.setBoolean(checkBox, true);
            checkBox.setChecked(checked);
            field.setBoolean(checkBox, false);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
