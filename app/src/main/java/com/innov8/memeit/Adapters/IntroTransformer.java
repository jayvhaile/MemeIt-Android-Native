package com.innov8.memeit.Adapters;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class IntroTransformer implements ViewPager.PageTransformer {

//    @Override
//    protected void onTransform(View view, float position) {
//        final float height = view.getHeight();
//        final float width = view.getWidth();
//        final float scale = min(position < 0 ? 1f : Math.abs(1f - position), 0.5f);
//
//        view.setScaleX(scale);
//        view.setScaleY(scale);
//        view.setPivotX(width * 0.5f);
//        view.setPivotY(height * 0.5f);
//        view.setTranslationX(position < 0 ? width * position : -width * position * 0.25f);
//    }
//
//    private static final float min(float val, float min) {
//        return val < min ? min : val;
//    }
private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1 || position > 1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
            return;
        }

        if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            // Fade the page out.
            view.setAlpha(1 + position);
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            return;

        }

        if (position > 0.5 && position <= 1) { // (0,1]
            // Fade the page out.
            view.setAlpha(0);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);
            return;
        }
        if (position > 0.3 && position <= 0.5) { // (0,1]
            // Fade the page out.
            view.setAlpha(1);

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * position);

            float scaleFactor = MIN_SCALE;
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            return;
        }
        if (position <= 0.3) { // (0,1]
            // Fade the page out.
            view.setAlpha(1);
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * position);

            // Scale the page down (between MIN_SCALE and 1)
            float v = (float) (0.3 - position);
            v = v >= 0.25f ? 0.25f : v;
            float scaleFactor = MIN_SCALE + v;
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }
}