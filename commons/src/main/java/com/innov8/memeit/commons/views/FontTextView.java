package com.innov8.memeit.commons.views;

import android.content.Context;
import android.util.AttributeSet;

import com.innov8.memeit.commons.models.TypefaceHandler;

/**
 * Created by Biruk on 5/11/2018.
 */

public class FontTextView extends androidx.appcompat.widget.AppCompatTextView {
    public  static final String asset = "fonts/avenir.ttf"/*"typefaceLoaders/avenir.ttf"*/;
    public FontTextView(Context context) {
        super(context);
        setCustomFont(context,asset);
    }
    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, asset);
    }
    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, asset);
    }
    public void setCustomFont(Context c, String asset){
        this.setTypeface(TypefaceHandler.CREATOR.byName("Avenir",getContext()).getTypeFace(getContext()));
    }
}
