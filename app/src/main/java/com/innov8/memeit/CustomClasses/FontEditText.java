package com.innov8.memeit.CustomClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Biruk on 6/30/2018.
 */

public class FontEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String asset = "fonts/avenir.ttf"/*"fonts/avenir.ttf"*/;
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
