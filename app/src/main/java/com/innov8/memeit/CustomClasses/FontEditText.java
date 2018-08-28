package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Biruk on 6/30/2018.
 */

public class FontEditText extends androidx.appcompat.widget.AppCompatEditText {
    public static final String asset = "fonts/avenir.ttf"/*"typefaceLoaders/avenir.ttf"*/;
    Typeface tf;
    public FontEditText(Context context) {
        super(context);
        tf = Typeface.createFromAsset(context.getAssets(),asset);
        this.setTypeface(tf);
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        tf = Typeface.createFromAsset(context.getAssets(),asset);
        this.setTypeface(tf);
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tf = Typeface.createFromAsset(context.getAssets(),asset);
        this.setTypeface(tf);
    }

}
