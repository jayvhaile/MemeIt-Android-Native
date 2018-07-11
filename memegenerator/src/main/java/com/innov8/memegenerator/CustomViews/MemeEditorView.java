package com.innov8.memegenerator.CustomViews;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewGroupCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Haile on 5/19/2018.
 */

public class MemeEditorView extends ViewGroup {
    private float aspectRatio;


    public MemeEditorView(Context context) {
        super(context);
        init();
    }

    public MemeEditorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MemeEditorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setBackgroundColor(Color.BLACK);
    }


    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

}
