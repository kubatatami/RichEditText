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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.kubatatami.richedittext.BaseRichEditText;
import com.github.kubatatami.richedittext.R;
import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.modules.HistoryModule;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba on 26/11/14.
 */
public class DefaultPanelView extends RelativeLayout {

    private final static int ANIM_DURATION = 250;

    private ToggleImageButton boldButton;

    private ToggleImageButton italicButton;

    private ToggleImageButton underlineButton;

    private ToggleImageButton strikethroughButton;

    private ToggleImageButton leftButton;

    private ToggleImageButton centerButton;

    private ToggleImageButton rightButton;

    private ImageView undoButton;

    private ImageView redoButton;

    private TextView fontSizeSpinner;

    private TextView fontSizeText;

    private TextView fontColorText;

    private ArrayAdapter<SizeSpanController.Size> adapter;

    private CircleView colorValue;

    private View colorPanel;

    private ImageView fontSizeValueLeftArrow;

    private ImageView fontSizeValueRightArrow;


    private boolean visible = false;

    private ColorPanelVisibility colorPanelVisibility = ColorPanelVisibility.INVISIBLE;

    private boolean changeState = false;

    private InputMethodManager inputManager;

    private final Handler handler = new Handler();

    private RichEditText richEditText;

    private int currentSizeItem = 0;

    private int grayColor;

    private int blackColor;

    private ViewPropertyAnimator animator;

    private View mainPanel;


    private final List<BaseRichEditText.OnValueChangeListener<Layout.Alignment>> onAlignmentClickListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<Float>> onSizeClickListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<ColorPanelVisibility>> onColorPanelShowListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<Integer>> onColorClickListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<Boolean>> onBoldClickListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<Boolean>> onItalicClickListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<Boolean>> onStrikeThroughClickListeners = new ArrayList<>();

    private final List<BaseRichEditText.OnValueChangeListener<Boolean>> onUnderlineClickListeners = new ArrayList<>();


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

    private void init() {
        inflate(getContext(), R.layout.default_panel, this);
        mainPanel = findViewById(R.id.main_panel);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        colorPanel = inflater.inflate(R.layout.color_panel, this, false);

        inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        grayColor = getResources().getColor(R.color.gray);
        blackColor = Color.BLACK;
        setVisibility(View.GONE);
    }

    private void hideAdditionalView() {
        hideAdditionalView(richEditText);
    }


    private void hideAdditionalView(TextView currentTextView) {
        if (getChildCount() > 2) {
            removeViewAt(1);
        }

        if (colorPanelVisibility.equals(ColorPanelVisibility.INVISIBLE)) {
            setShowSoftInputOnFocus(currentTextView, true);
        }

        setColorPanelVisibility(ColorPanelVisibility.INVISIBLE);
        mainPanel.setVisibility(View.VISIBLE);
    }

    public void showAdditionalView(boolean anim, View view) {
        showAdditionalView(anim, view, richEditText);
    }

    public void showAdditionalView(boolean anim, View view, boolean alignTopAndBottom) {
        showAdditionalView(anim, view, richEditText, alignTopAndBottom);
    }

    public void showAdditionalView(boolean anim, View view, TextView currentTextView) {
        showAdditionalView(anim, view, currentTextView, true);
    }


    public void showAdditionalView(boolean anim, View view, TextView currentTextView, boolean alignTopAndBottom) {
        hideAdditionalView(currentTextView);
        LayoutParams layoutParams = view.getLayoutParams() == null ?
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) :
                new LayoutParams(view.getLayoutParams().width, view.getLayoutParams().height);
        if (alignTopAndBottom) {
            layoutParams.addRule(ALIGN_TOP, R.id.main_panel);
            mainPanel.setVisibility(View.INVISIBLE);
        }
        layoutParams.addRule(ALIGN_BOTTOM, R.id.main_panel);
        view.setLayoutParams(layoutParams);
        addView(view, 1);
        toggle(anim, true);
        setShowSoftInputOnFocus(currentTextView, false);
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
            if (visible) {
                setShowSoftInputOnFocus(getCurrentTextView(), false);
            }
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

    private TextView getCurrentTextView() {
        if (getContext() instanceof Activity) {
            View view = ((Activity) getContext()).getCurrentFocus();
            if (view != null && view instanceof TextView) {
                return (TextView) view;
            }
        }
        return richEditText;
    }

    private void show(final boolean anim) {
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
        setShowSoftInputOnFocus(getCurrentTextView(), false);
    }

    private void hide(final boolean anim) {
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
        setShowSoftInputOnFocus(getCurrentTextView(), true);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setShowSoftInputOnFocus(final TextView textView, boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setShowSoftInputOnFocus(show);
        } else {
            if (show) {
                textView.setOnTouchListener(null);
            } else {
                textView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        textView.onTouchEvent(event);
                        inputManager.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    }
                });
            }
        }
    }


    private void showPanelView(boolean anim) {
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

    public void showUndoRedo(boolean show) {
        undoButton.setVisibility(show ? View.VISIBLE : View.GONE);
        redoButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    public void connectWithRichEditText(final RichEditText richEditText) {
        this.richEditText = richEditText;
        boldButton = (ToggleImageButton) findViewById(R.id.bold_button);
        italicButton = (ToggleImageButton) findViewById(R.id.italic_button);
        underlineButton = (ToggleImageButton) findViewById(R.id.underline_button);
        strikethroughButton = (ToggleImageButton) findViewById(R.id.strikethrough_button);

        leftButton = (ToggleImageButton) findViewById(R.id.left_button);
        centerButton = (ToggleImageButton) findViewById(R.id.center_button);
        rightButton = (ToggleImageButton) findViewById(R.id.right_button);


        fontSizeSpinner = (TextView) findViewById(R.id.font_size_value);
        colorValue = (CircleView) findViewById(R.id.color_picker_value);

        undoButton = (ImageView) findViewById(R.id.undo_button);
        redoButton = (ImageView) findViewById(R.id.redo_button);
        fontSizeValueLeftArrow = (ImageView) findViewById(R.id.font_size_value_left_arrow);
        fontSizeValueRightArrow = (ImageView) findViewById(R.id.font_size_value_right_arrow);

        fontSizeText = (TextView) findViewById(R.id.font_size_text);
        fontColorText = (TextView) findViewById(R.id.font_color_text);

        boldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.boldClick();
                for (BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener : onBoldClickListeners) {
                    onValueChangeListener.onValueChange(boldButton.isChecked());
                }
            }
        });
        italicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.italicClick();
                for (BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener : onItalicClickListeners) {
                    onValueChangeListener.onValueChange(italicButton.isChecked());
                }
            }
        });
        underlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.underlineClick();
                for (BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener : onUnderlineClickListeners) {
                    onValueChangeListener.onValueChange(underlineButton.isChecked());
                }
            }
        });
        strikethroughButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                richEditText.strikeThroughClick();
                for (BaseRichEditText.OnValueChangeListener<Boolean> onValueChangeListener : onStrikeThroughClickListeners) {
                    onValueChangeListener.onValueChange(strikethroughButton.isChecked());
                }
            }
        });

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                android.R.id.text1, SizeSpanController.Size.values());

        fontSizeValueLeftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSizeItem > 0) {
                    currentSizeItem--;
                    setFontSize();
                    richEditText.sizeClick(adapter.getItem(currentSizeItem));
                    for (BaseRichEditText.OnValueChangeListener<Float> onValueChangeListener : onSizeClickListeners) {
                        onValueChangeListener.onValueChange(adapter.getItem(currentSizeItem).getSize());
                    }
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
                    for (BaseRichEditText.OnValueChangeListener<Float> onValueChangeListener : onSizeClickListeners) {
                        onValueChangeListener.onValueChange(adapter.getItem(currentSizeItem).getSize());
                    }
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

        richEditText.addOnBoldChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean bold) {
                boldButton.setChecked(bold);
            }
        });
        richEditText.addOnItalicChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean italic) {
                italicButton.setChecked(italic);
            }
        });
        richEditText.addOnUnderlineChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
            @Override
            public void onValueChange(Boolean underline) {
                underlineButton.setChecked(underline);
            }
        });
        richEditText.addOnStrikethroughChangeListener(new BaseRichEditText.OnValueChangeListener<Boolean>() {
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
        richEditText.addOnSizeChangeListener(new BaseRichEditText.OnValueChangeListener<Float>() {
            @Override
            public void onValueChange(Float size) {
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
        richEditText.addOnColorChangeListener(new BaseRichEditText.OnValueChangeListener<Integer>() {
            @Override
            public void onValueChange(Integer value) {
//                colorPicker.setNewCenterColor(value);
                colorValue.setColor(value);
            }
        });
        richEditText.addOnAlignmentChangeListener(new BaseRichEditText.OnValueChangeListener<Layout.Alignment>() {

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
                for (BaseRichEditText.OnValueChangeListener<Layout.Alignment> onValueChangeListener : onAlignmentClickListeners) {
                    onValueChangeListener.onValueChange(Layout.Alignment.ALIGN_NORMAL);
                }
                richEditText.alignmentClick(Layout.Alignment.ALIGN_NORMAL);
                setChecked(buttonView, true);
            }
        });
        centerButton.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                setChecked(leftButton, false);
                setChecked(rightButton, false);
                for (BaseRichEditText.OnValueChangeListener<Layout.Alignment> onValueChangeListener : onAlignmentClickListeners) {
                    onValueChangeListener.onValueChange(Layout.Alignment.ALIGN_CENTER);
                }
                richEditText.alignmentClick(Layout.Alignment.ALIGN_CENTER);
                setChecked(buttonView, true);
            }
        });
        rightButton.setOnCheckedChangeListener(new ToggleImageButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked) {
                setChecked(leftButton, false);
                setChecked(centerButton, false);
                for (BaseRichEditText.OnValueChangeListener<Layout.Alignment> onValueChangeListener : onAlignmentClickListeners) {
                    onValueChangeListener.onValueChange(Layout.Alignment.ALIGN_OPPOSITE);
                }
                richEditText.alignmentClick(Layout.Alignment.ALIGN_OPPOSITE);
                setChecked(buttonView, true);
            }
        });
        richEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, adapter.getItem(0).getSize());
        richEditText.setHistoryLimit(20);
        colorValue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrimaryColors();
            }
        });
    }

    private void showPrimaryColors() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        showAdditionalView(false, colorPanel, false);
        ViewGroup colorPanelList = (ViewGroup) colorPanel.findViewById(R.id.color_picker_list);
        colorPanel.findViewById(R.id.color_picker_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAdditionalView();
            }
        });
        colorPanelList.removeAllViews();
        int colors[] = getResources().getIntArray(R.array.colors);
        for (int color : colors) {
            CircleView circleView = (CircleView) inflater.inflate(R.layout.circle_view, colorPanelList, false);
            circleView.setColor(color);
            colorPanelList.addView(circleView);
            circleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSecondaryColors(((CircleView) v).getColor());
                }
            });
        }
        setColorPanelVisibility(ColorPanelVisibility.PRIMARY);
    }

    private void showSecondaryColors(int baseColor) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        showAdditionalView(false, colorPanel, false);
        ViewGroup colorPanelList = (ViewGroup) colorPanel.findViewById(R.id.color_picker_list);
        colorPanel.findViewById(R.id.color_picker_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrimaryColors();
            }
        });
        colorPanelList.removeAllViews();
        int colors = 8;
        for (int i = 1; i <= colors; i++) {
            CircleView circleView = (CircleView) inflater.inflate(R.layout.circle_view, colorPanelList, false);
            int r = Color.red(baseColor);
            int g = Color.green(baseColor);
            int b = Color.blue(baseColor);
            r = (int) ((float) (255 - r) * ((float) i / (float) (colors + 2)) + r);
            g = (int) ((float) (255 - g) * ((float) i / (float) (colors + 2)) + g);
            b = (int) ((float) (255 - b) * ((float) i / (float) (colors + 2)) + b);
            circleView.setColor(Color.rgb(r, g, b));
            colorPanelList.addView(circleView);
            circleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideAdditionalView();
                    setColorPanelVisibility(ColorPanelVisibility.INVISIBLE);
                    for (BaseRichEditText.OnValueChangeListener<Integer> onValueChangeListener : onColorClickListeners) {
                        onValueChangeListener.onValueChange(((CircleView) v).getColor());
                    }
                    colorValue.setColor(((CircleView) v).getColor());
                    richEditText.colorClick(((CircleView) v).getColor());
                }
            });
        }
        setColorPanelVisibility(ColorPanelVisibility.SECONDARY);

    }

    private void setColorPanelVisibility(ColorPanelVisibility visibility) {
        colorPanelVisibility = visibility;
        for (BaseRichEditText.OnValueChangeListener<ColorPanelVisibility> onValueChangeListener : onColorPanelShowListeners) {
            onValueChangeListener.onValueChange(visibility);
        }
    }

    public boolean onBack(boolean anim) {
        if (colorPanelVisibility.equals(ColorPanelVisibility.PRIMARY)) {
            hideAdditionalView();
            return true;
        }
        if (colorPanelVisibility.equals(ColorPanelVisibility.SECONDARY)) {
            showPrimaryColors();
            return true;
        } else if (visible) {
            toggle(anim, false);
            return true;
        } else {
            return false;
        }
    }

    public void setFontSizeText(int text) {
        fontSizeText.setText(text);
    }

    public void setFontColorText(int text) {
        fontColorText.setText(text);
    }

    private void setFontSize() {
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


    private void setChecked(ToggleImageButton checkBox, boolean checked) {
        try {
            Field field = ToggleImageButton.class.getDeclaredField("isBroadCasting");
            field.setAccessible(true);
            field.setBoolean(checkBox, true);
            checkBox.setChecked(checked);
            field.setBoolean(checkBox, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public enum ColorPanelVisibility {
        INVISIBLE,
        PRIMARY,
        SECONDARY
    }


    public void addOnAlignmentClickListener(BaseRichEditText.OnValueChangeListener<Layout.Alignment> onAlignmentClickListener) {
        onAlignmentClickListeners.add(onAlignmentClickListener);
    }

    public void addOnSizeClickListener(BaseRichEditText.OnValueChangeListener<Float> onSizeClickListener) {
        onSizeClickListeners.add(onSizeClickListener);
    }

    public void addOnColorPanelShowListener(BaseRichEditText.OnValueChangeListener<ColorPanelVisibility> onColorPanelShowListener) {
        onColorPanelShowListeners.add(onColorPanelShowListener);
    }

    public void addOnColorClickListener(BaseRichEditText.OnValueChangeListener<Integer> onColorClickListener) {
        onColorClickListeners.add(onColorClickListener);
    }

    public void addOnBoldClickListener(BaseRichEditText.OnValueChangeListener<Boolean> onBoldClickListener) {
        onBoldClickListeners.add(onBoldClickListener);
    }

    public void addOnItalicClickListener(BaseRichEditText.OnValueChangeListener<Boolean> onItalicClickListener) {
        onItalicClickListeners.add(onItalicClickListener);
    }

    public void addOnStrikeThroughClickListener(BaseRichEditText.OnValueChangeListener<Boolean> onStrikeThroughClickListener) {
        onStrikeThroughClickListeners.add(onStrikeThroughClickListener);
    }

    public void addOnUnderlineClickListener(BaseRichEditText.OnValueChangeListener<Boolean> onUnderlineClickListener) {
        onUnderlineClickListeners.add(onUnderlineClickListener);
    }
}
