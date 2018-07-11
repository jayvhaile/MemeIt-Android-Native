package com.innov8.memegenerator.CustomViews;

import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.Map;

/**
 * Created by Jv on 7/11/2018.
 */

public abstract class Layout {
    Map<Integer,MemeImage> images;

    public abstract int getImageCount();

    public abstract void onLayout(int order);
}
