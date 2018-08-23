package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.innov8.memeit.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class ToolBarBehavior extends CoordinatorLayout.Behavior<View> {
    private final static String TAG = "behavior";
    private float mFinalHeight;
    private Context mContext;
//    aaaa
    public ToolBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof ImageView;
    }
    private void init() {
        mFinalHeight = mContext.getResources().getDimension(R.dimen.toolbar_height);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,View child, View dependency) {
        if (dependency instanceof ImageView) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            float h= (int) (dependency.getY()+dependency.getHeight()/2);
            h=h<mFinalHeight?mFinalHeight:h;
            lp.height = (int) h;
            child.setLayoutParams(lp);
            return true;
        }
        return false;
    }

}
