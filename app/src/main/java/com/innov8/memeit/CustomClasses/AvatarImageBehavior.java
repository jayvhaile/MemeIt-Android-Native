package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;
import com.innov8.memeit.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

@SuppressWarnings("unused")
public class AvatarImageBehavior extends CoordinatorLayout.Behavior<ImageView> {

    private final static float MIN_AVATAR_PERCENTAGE_SIZE = 0.3f;
    private final static int EXTRA_FINAL_AVATAR_PADDING = 80;

    private final static String TAG = "behavior";
    private Context mContext;

    private float mCustomStartXPosition;
    private float mCustomFinalYPosition;
    private float mCustomStartYPosition;
    private float mCustomStartToolbarPosition;
    private float mCustomStartHeight;
    private float mCustomFinalHeight;

    private float mAvatarMaxSize;
    private float mFinalLeftAvatarPadding;
    private float mStartPosition;
    private float mAppBarStartBottom;

    private int mFinalYPosition;
    private int mStartWidth;
    private int mStartHeight;
    private int mFinalXPosition;
    private float mChangeBehaviorPoint;
    private float mtoolbarh;

    public AvatarImageBehavior(Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageBehavior);
            mCustomStartXPosition = a.getDimension(R.styleable.AvatarImageBehavior_startXPosition, 0);
            mCustomStartYPosition = a.getDimension(R.styleable.AvatarImageBehavior_startYPosition, 0);
            mCustomFinalYPosition = a.getDimension(R.styleable.AvatarImageBehavior_finalYPosition, 0);
            mCustomStartToolbarPosition = a.getDimension(R.styleable.AvatarImageBehavior_startToolbarPosition, 0);
            mCustomStartHeight = a.getDimension(R.styleable.AvatarImageBehavior_startHeight, 0);
            mCustomFinalHeight = a.getDimension(R.styleable.AvatarImageBehavior_finalHeight, 0);

            a.recycle();
        }

        mAvatarMaxSize = mContext.getResources().getDimension(R.dimen.image_width);
        mtoolbarh = mContext.getResources().getDimension(R.dimen.toolbar_height)*2;

        mFinalLeftAvatarPadding = context.getResources().getDimension(
                R.dimen.spacing_normal);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {

        if (dependency instanceof AppBarLayout) {
            maybeInitProperties(child, dependency);
            AppBarLayout apb = (AppBarLayout) dependency;
            float expandedPercentageFactor = ((dependency.getBottom())- mtoolbarh) / mAppBarStartBottom;

            float mStartXPosition=mCustomStartXPosition+(mStartWidth/2f);
            if (expandedPercentageFactor < mChangeBehaviorPoint) {
               // child.setColorFilter(Color.argb(50, 255, 0, 0));
                float heightFactor = (mChangeBehaviorPoint - expandedPercentageFactor) / mChangeBehaviorPoint;

                float distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
                        * heightFactor) + (child.getHeight() / 2);
                float distanceYToSubtract = ((mCustomStartYPosition - mCustomFinalYPosition)
                        * (1f - expandedPercentageFactor)) + (child.getHeight() / 2);

                child.setX(mStartXPosition - distanceXToSubtract);
                child.setY(mCustomStartYPosition - distanceYToSubtract);

                float heightToSubtract = ((mStartHeight - mCustomFinalHeight) * heightFactor);

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                lp.width = (int) (mStartHeight - heightToSubtract);
                lp.height = (int) (mStartHeight - heightToSubtract);
                child.setLayoutParams(lp);
            } else {
                //child.setColorFilter(Color.argb(50, 0, 0, 255));
                float distanceYToSubtract = ((mCustomStartYPosition - mFinalYPosition)
                        * (1f - expandedPercentageFactor)) + (mStartHeight / 2);

                child.setX(mStartXPosition - child.getWidth() / 2);
                child.setY(mCustomStartYPosition - distanceYToSubtract);

                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                lp.width = mStartHeight;
                lp.height = mStartHeight;
                child.setLayoutParams(lp);
            }
            return true;
        }
        return false;
    }

    private void maybeInitProperties(ImageView child, View dependency) {


        if (mFinalYPosition == 0)
            mFinalYPosition = (dependency.getHeight() / 2);
        if (mStartWidth == 0)
            mStartWidth = child.getWidth();
        if (mStartHeight == 0)
            mStartHeight = child.getHeight();


        if (mFinalXPosition == 0)
            mFinalXPosition = mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + ((int) mCustomFinalHeight / 2);

        if (mAppBarStartBottom == 0){
            mAppBarStartBottom = (dependency.getBottom()-dependency.getTop())- mtoolbarh;
        }

        if (mChangeBehaviorPoint == 0) {
            mChangeBehaviorPoint = 0.99f;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
