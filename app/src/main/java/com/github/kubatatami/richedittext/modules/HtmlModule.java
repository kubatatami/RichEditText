package com.github.kubatatami.richedittext.modules;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.util.Log;

import com.github.kubatatami.richedittext.styles.base.SpanController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Kuba on 20/11/14.
 */
public class HtmlModule {



    public String getHtml(Editable text, Map<Class<?>, SpanController<?>> spanControllerMap) {
        List<HtmlTag> htmlTagsList = new ArrayList<HtmlTag>();
        final Editable editable = new SpannableStringBuilder(text);
        for (SpanController<?> controller : spanControllerMap.values()) {
            List<?> spans = controller.filter(editable.getSpans(0,editable.length(),controller.getClazz()));

            for(Object span : spans){
                htmlTagsList.add(new HtmlTag(editable.getSpanStart(span), controller.beginTag(span),false));
                htmlTagsList.add(new HtmlTag(editable.getSpanEnd(span),  controller.endTag(),true));
                editable.removeSpan(span);
            }
        }

        Collections.sort(htmlTagsList);

        for(HtmlTag htmlTags : htmlTagsList){
            editable.insert(htmlTags.start,htmlTags.tag);
        }
        String result =  editable.toString().replaceAll("\n","</br>");
        Log.i("html", result);
        return result;
    }

    class HtmlTag implements Comparable<HtmlTag>{
        int start;
        String tag;
        boolean end;

        HtmlTag(int start, String tag, boolean end) {
            this.start = start;
            this.tag = tag;
            this.end = end;
        }

        @Override
        public int compareTo(HtmlTag another) {
            int result = Integer.valueOf(another.start).compareTo(start);
            if(result==0) {
                result = Boolean.valueOf(end).compareTo(another.end);
            }
            return result;
        }
    }

}
