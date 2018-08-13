package com.innov8.memegenerator.models;

import android.content.Context;
import android.view.View;

public class SizeOption extends Option<Integer,View> {

    public SizeOption(Context context, String name) {
        super(context, name);
    }

    @Override
    protected void OnCreateView(View view) {
    }

    @Override
    public void updateOption(Integer o) {

    }

    @Override
    public int getViewLayoutId() {
        return 0;
    }
}
