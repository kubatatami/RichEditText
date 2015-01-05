package com.github.kubatatami.richedittext.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
public class DefaultPanelView extends RelativeLayout {

    protected final static int ANIM_DURATION = 250;

    protected ToggleButton boldButton, italicButton, underlineButton, strikethroughButton;
    protected ToggleImageButton leftButton, centerButton, rightButton;
    protected ImageView undoButton, redoButton;
    protected View colorOk;
    protected TextView fontSizeSpinner;
    protected ArrayAdapter<SizeSpanController.Size> adapter;
    protected ColorPicker colorPicker;
    protected View colorValue, colorPanel;
    protected ImageView fontSizeValueLeftArrow, fontSizeValueRightArrow;
    protected boolean ignoreSizeEvent, ignoreColorEvent, visible = false, colorPanelVisible;
    protected boolean changeState = false;
    protected InputMethodManager inputManager;
    protected Handler handler = new Handler();
    protected RichEditText richEditText;
    protected int currentSizeItem = 0;
    protected int grayColor, blackColor;
    protected ViewPropertyAnimator animator;
    protected View mainPanel;

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
        inflate(getContext(), R.layout.default_panel, this);
        mainPanel = findViewById(R.id.main_panel);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colorPanel = inflater.inflate(R.layout.color_panel, this, false);

        inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        grayColor = getResources().getColor(R.color.gray);
        blackColor = Color.BLACK;
        setVisibility(View.GONE);
    }

    public void hideAdditionalView() {
        hideAdditionalView(richEditText);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void hideAdditionalView(TextView currentTextView) {
        if (getChildCount() > 2) {
            removeViewAt(1);
        }
        colorPanelVisible = false;
        mainPanel.setVisibility(View.VISIBLE);

        currentTextView.setShowSoftInputOnFocus(true);
    }

    public void showAdditionalView(boolean anim, View view) {
        showAdditionalView(anim, view, richEditText);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void showAdditionalView(boolean anim, View view, TextView currentTextView) {
        hideAdditionalView(currentTextView);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_TOP, R.id.main_panel);
        layoutParams.addRule(ALIGN_BOTTOM, R.id.main_panel);
        view.setLayoutParams(layoutParams);
        addView(view, 1);
        toggle(anim, true);
        mainPanel.setVisibility(View.INVISIBLE);
        currentTextView.setShowSoftInputOnFocus(false);
    }

    public void showPanel(boolean anim) {
        hideAdditionalView();
        toggle(anim, true);
    }

    public void toggle(boolean anim) {
        toggle(anim, !visible);
    }

    public void toggle(final boolean anim, final boolean show) {
        if (this.visible == show || changeState) {
            return;
        }

        changeState = true;
        if (!show) {
            hide(anim);
        } else {
            show(anim);
        }
        this.visible = show;
    }

    protected TextView getCurrentTextView() {
        if (getContext() instanceof Activity) {
            View view = ((Activity) getContext()).getCurrentFocus();
            if (view != null && view instanceof TextView) {
                return (TextView) view;
            }
        }
        return richEditText;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void show(final boolean anim) {
        if (animator != null) {
            animator.cancel();
        }
        boolean hide = inputManager.hideSoftInputFromWindow(getWindowToken(), 0);
        if (!hide) {
            showPanelView(anim);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPanelView(anim);
                }
            }, ANIM_DURATION);
        }
        getCurrentTextView().setShowSoftInputOnFocus(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void hide(final boolean anim) {
        if (animator != null) {
            animator.cancel();
        }
        int newTop = (((View) getParent()).getMeasuredHeight());
        if (anim) {
            animator = animate().y(newTop).setDuration(ANIM_DURATION);
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    changeState = false;
                    setVisibility(View.GONE);
                    animator.setListener(null);
                    inputManager.showSoftInput(getCurrentTextView(), 0);
                }
            });
            animator.start();
        } else {
            inputManager.showSoftInput(getCurrentTextView(), 0);
            setY(newTop);
            setVisibility(View.GONE);
            changeState = false;
        }
        getCurrentTextView().setShowSoftInputOnFocus(true);
    }


    protected void showPanelView(boolean anim) {
        setVisibility(View.VISIBLE);
        int newTop;
        if (getMeasuredHeight() == 0) {
            newTop = 0;
        } else {
            newTop = ((View) getParent()).getMeasuredHeight() - getMeasuredHeight();
        }

        if (anim) {
            animator = animate().y(newTop).setDuration(ANIM_DURATION);
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    changeState = false;
                }
            });
            animator.start();
        } else {
            setY(newTop);
            changeState = false;
        }
    }


    public void connectWithRichEditText(final RichEditText richEditText) {
        this.richEditText = richEditText;
        boldButton = (ToggleButton) findViewById(R.id.bold_button);
        italicButton = (ToggleButton) findViewById(R.id.italic_button);
        underlineButton = (ToggleButton) findViewById(R.id.underline_button);
        strikethroughButton = (ToggleButton) findViewById(R.id.strikethrough_button);

        leftButton = (ToggleImageButton) findViewById(R.id.left_button);
        centerButton = (ToggleImageButton) findViewById(R.id.center_button);
        rightButton = (ToggleImageButton) findViewById(R.id.right_button);


        fontSizeSpinner = (TextView) findViewById(R.id.font_size_value);
        colorValue = findViewById(R.id.color_picker_value);

        undoButton = (ImageView) findViewById(R.id.undo_button);
        redoButton = (ImageView) findViewById(R.id.redo_button);
        fontSizeValueLeftArrow = (ImageView) findViewById(R.id.font_size_value_left_arrow);
        fontSizeValueRightArrow = (ImageView) findViewById(R.id.font_size_value_right_arrow);


        colorPicker = (ColorPicker) colorPanel.findViewById(R.id.color_picker);
        colorOk = colorPanel.findViewById(R.id.color_ok);


        colorPicker.setShowOldCenterColor(false);
        colorPicker.addSaturationBar((com.larswerkman.holocolorpicker.SaturationBar) colorPanel.findViewById(R.id.color_saturation_bar));
        colorPicker.addValueBar((com.larswerkman.holocolorpicker.ValueBar) colorPanel.findViewById(R.id.color_value_bar));


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

        fontSizeValueLeftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSizeItem > 0) {
                    currentSizeItem--;
                    setFontSize();
                    richEditText.sizeClick(adapter.getItem(currentSizeItem));
                }
            }
        });

        fontSizeValueRightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSizeItem < adapter.getCount() - 1) {
                    currentSizeItem++;
                    setFontSize();
                    richEditText.sizeClick(adapter.getItem(currentSizeItem));
                }
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
                undoButton.setColorFilter(undoSteps > 0 ? blackColor : grayColor);
                redoButton.setColorFilter(redoSteps > 0 ? blackColor : grayColor);
            }
        });
        richEditText.setOnSizeChangeListener(new BaseRichEditText.OnValueChangeListener<Float>() {
            @Override
            public void onValueChange(Float size) {
                ignoreSizeEvent = true;
                for (int i = 0; i < adapter.getCount(); i++) {
                    SizeSpanController.Size sizeEnum = adapter.getItem(i);
                    if (sizeEnum.getSize() == size) {
                        currentSizeItem = i;
                        setFontSize();
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
                colorValue.setBackgroundColor(value);
            }
        });
        richEditText.setOnAlignmentChangeListener(new BaseRichEditText.OnValueChangeListener<Layout.Alignment>() {

            @Override
            public void onValueChange(Layout.Alignment value) {
                setChecked(leftButton, value != null && value.equals(Layout.Alignment.ALIGN_NORMAL));
                setChecked(centerButton, value != null && value.equals(Layout.Alignment.ALIGN_CENTER));
                setChecked(rightButton, value != null && value.equals(Layout.Alignment.ALIGN_OPPOSITE));
            }
        });
        leftButton.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                setChecked(centerButton, false);
                setChecked(rightButton, false);
                richEditText.alignmentClick(Layout.Alignment.ALIGN_NORMAL);
                setChecked(buttonView, true);
            }
        });
        centerButton.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                setChecked(leftButton, false);
                setChecked(rightButton, false);
                richEditText.alignmentClick(Layout.Alignment.ALIGN_CENTER);
                setChecked(buttonView, true);
            }
        });
        rightButton.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
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
                    colorValue.setBackgroundColor(i);
                }
                first = false;
                ignoreColorEvent = false;
            }
        });
        colorValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdditionalView(false, colorPanel);
                colorPanelVisible = true;
            }
        });
        colorOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAdditionalView();
                colorPanelVisible = false;
            }
        });
    }

    public boolean onBack(boolean anim) {
        if (colorPanelVisible) {
            hideAdditionalView();
            return true;
        } else if (visible) {
            toggle(anim, false);
            return true;
        } else {
            return false;
        }
    }

    protected void setFontSize() {
        fontSizeSpinner.setText(adapter.getItem(currentSizeItem).getSize() + "");
        if (currentSizeItem == 0) {
            fontSizeValueLeftArrow.setColorFilter(grayColor);
            fontSizeValueRightArrow.setColorFilter(blackColor);
        } else if (currentSizeItem + 1 == adapter.getCount()) {
            fontSizeValueLeftArrow.setColorFilter(blackColor);
            fontSizeValueRightArrow.setColorFilter(grayColor);
        } else {
            fontSizeValueLeftArrow.setColorFilter(blackColor);
            fontSizeValueRightArrow.setColorFilter(blackColor);
        }
    }


    protected void setChecked(ToggleImageButton checkBox, boolean checked) {
        try {
            Field field = ToggleImageButton.class.getDeclaredField("isBroadCasting");
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
