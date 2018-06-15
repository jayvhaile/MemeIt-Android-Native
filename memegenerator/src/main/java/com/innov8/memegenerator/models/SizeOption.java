package com.innov8.memegenerator.models;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

import com.innov8.memegenerator.R;

public class SizeOption extends Option<Integer,View> {
    private SeekBar bar;

    public SizeOption(Context context, String name) {
        super(context, name);
    }

    @Override
    protected void OnCreateView(View view) {
        bar=(SeekBar) view.findViewById(R.id.seekBar);
    }

    @Override
    public void updateOption(Integer o) {

    }

    @Override
    public int getViewLayoutId() {
        return 0;
    }
}
