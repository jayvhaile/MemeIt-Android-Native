package com.innov8.memegenerator.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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
        handleActionBtnClick();
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        tf = Typeface.createFromAsset(context.getAssets(),asset);
        this.setTypeface(tf);
        handleActionBtnClick();
    }

    public FontEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tf = Typeface.createFromAsset(context.getAssets(),asset);
        this.setTypeface(tf);
        handleActionBtnClick();
    }
    private void handleActionBtnClick() {
        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                ((InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        v.getWindowToken(), 0);
                clearFocus();
                return false;
            }
        });
    }



    @Override public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
