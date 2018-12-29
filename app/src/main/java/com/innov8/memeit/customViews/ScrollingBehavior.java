package com.innov8.memeit.customViews;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jv on 7/7/2018.
 */

public class ScrollingBehavior extends CoordinatorLayout.Behavior {
    private Animator animator;

    public ScrollingBehavior() {
    }

    public ScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes& ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        if(dyConsumed>0) {//scrolling down
            restartAnimation(child,calcHideValue(coordinatorLayout,child));
        }else{
            restartAnimation(child,0f);
        }
    }

    private void restartAnimation(View target,float value){
        if(animator!=null){
            animator.cancel();
            animator=null;
        }
        animator= ObjectAnimator.ofFloat(target,View.TRANSLATION_Y,value)
                .setDuration(250);
        animator.start();
    }

    private float calcHideValue(ViewGroup parent,View target){
        return parent.getHeight()-target.getTop();
    }
}
