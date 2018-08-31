package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.innov8.memeit.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ToolBarBehavior extends CoordinatorLayout.Behavior<View> {
    private final static String TAG = "behavior";
    private float mFinalHeight;
    private float mInitialHeight;
    private Context mContext;

    //    aaaa
    public ToolBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof ImageView;
    }

    private void init() {
        mInitialHeight = mContext.getResources().getDimension(R.dimen.toolbar_iheight);
        mFinalHeight = mContext.getResources().getDimension(R.dimen.toolbar_height);
    }

    private Drawable initialDrawable;
    private boolean color;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof ImageView) {
            if (initialDrawable == null) {
                initialDrawable = child.getBackground();
            }
            float b = (int) (dependency.getY() + dependency.getHeight() / 2);
            if (b > mFinalHeight) {
                child.setY(b - mInitialHeight);
            }
            return true;
        }
        return false;
    }

}
