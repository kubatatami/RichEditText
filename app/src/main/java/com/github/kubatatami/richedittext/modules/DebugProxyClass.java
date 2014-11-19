package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

import com.github.kubatatami.richedittext.RichEditText;
import com.github.kubatatami.richedittext.styles.multi.SizeSpanController;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DebugProxyClass implements InvocationHandler {

    protected RichEditText richEditText;
    protected final SizeSpanController sizeStyle = new SizeSpanController();

    protected DebugProxyClass(RichEditText richEditText) {
        this.richEditText = richEditText;
    }

    public static Editable getEditable(RichEditText richEditText){
        return (Editable) Proxy.newProxyInstance(
                Editable.class.getClassLoader(),
                new Class[]{Editable.class}, new DebugProxyClass(richEditText));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Editable baseEditable = richEditText.getEditableText();
        if (method.getName().equals("setSpan")) {
            String stInfo = getExternalStacktrace(Thread.currentThread().getStackTrace());
            if (stInfo != null) {
                Log.i("setSpan", args[0].getClass().getSimpleName() + " " + getValue(args[0]) + " "
                        + args[1] + ":" + args[2] + " " + getFlagsAsString((Integer) args[3]) + stInfo);
            }
        } else if (method.getName().equals("removeSpan")) {
            String stInfo = getExternalStacktrace(Thread.currentThread().getStackTrace());
            int spanStart = baseEditable.getSpanStart(args[0]);
            int spanEnd = baseEditable.getSpanEnd(args[0]);
            int spanFlags = baseEditable.getSpanEnd(args[0]);
            if (stInfo != null) {
                Log.i("removeSpan", args[0].getClass().getSimpleName() + " " + getValue(args[0])
                        + " " + spanStart + ":" + spanEnd + " " + getFlagsAsString(spanFlags) + stInfo);
            }
        }
        return method.invoke(baseEditable, args);
    }

    protected String getExternalStacktrace(StackTraceElement[] stackTrace) {
        String packageName = RichEditText.class.getPackage().getName();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().contains(packageName) && !element.getClassName().equals(DebugProxyClass.class.getName())) {
                return " from " +
                        element.getClassName() +
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")";
            }
        }
        return null;
    }

    protected String getValue(Object span) {
        if (span instanceof AbsoluteSizeSpan) {
            return sizeStyle.getValueFromSpan((AbsoluteSizeSpan) span) + "";
        } else {
            return "";
        }
    }

    protected String getFlagsAsString(int flags) {
        String type;
        switch (flags) {
            case Spanned.SPAN_INCLUSIVE_EXCLUSIVE:
                type = "SPAN_INCLUSIVE_EXCLUSIVE";
                break;
            case Spanned.SPAN_INCLUSIVE_INCLUSIVE:
                type = "SPAN_INCLUSIVE_INCLUSIVE";
                break;
            case Spanned.SPAN_EXCLUSIVE_EXCLUSIVE:
                type = "SPAN_EXCLUSIVE_EXCLUSIVE";
                break;
            case Spanned.SPAN_EXCLUSIVE_INCLUSIVE:
                type = "SPAN_EXCLUSIVE_INCLUSIVE";
                break;
            case Spanned.SPAN_INCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING:
                type = "Spanned.SPAN_INCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING";
                break;
            default:
                type = "";
                break;
        }
        return type;
    }

}