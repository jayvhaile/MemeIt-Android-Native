package com.innov8.memeit.customViews;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jv on 7/7/2018.
 */

public class BottomNavigationBehavior extends CoordinatorLayout.Behavior<BottomNavigation> {
    private int mDependencyOffset;
    private int mChildInitialOffset;

    private FloatingActionButton.Behavior a;
    public BottomNavigationBehavior() {
    }

    public BottomNavigationBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, BottomNavigation child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, BottomNavigation child, View dependency) {
        if (dependency instanceof AppBarLayout&&mDependencyOffset != dependency.getTop()) {
            mDependencyOffset = dependency.getTop();
            int offset = mChildInitialOffset - child.getTop() - mDependencyOffset;
            child.offsetTopAndBottom(offset);
            return true;
        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigation child, int layoutDirection) {
        mChildInitialOffset =child.getTop();
        return false;
    }
}
