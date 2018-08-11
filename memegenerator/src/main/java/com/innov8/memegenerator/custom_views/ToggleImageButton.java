package com.innov8.memegenerator.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;

import com.innov8.memegenerator.R;

public class ToggleImageButton extends AppCompatImageButton {
    private boolean checked;
    private int checkedbackground;
    private int uncheckedbackground;


    public ToggleImageButton(Context context) {
        super(context);
        checkedbackground=Color.parseColor("#5555");
        uncheckedbackground=Color.TRANSPARENT;
        setChecked(false);
    }

    public ToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ToggleImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ToggleImageButton,
                0, 0);

        try {
            checked=a.getBoolean(R.styleable.ToggleImageButton_checked,false);
           checkedbackground=a.getColor(R.styleable.ToggleImageButton_checked_background,  Color.parseColor("#5555"));
           uncheckedbackground=a.getColor(R.styleable.ToggleImageButton_checked_background, Color.TRANSPARENT);
        } finally {
            a.recycle();
        }
        setChecked(checked);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if (checked){
            setBackgroundColor(checkedbackground);
        }else{
            setBackgroundColor(uncheckedbackground);
        }
    }
}
