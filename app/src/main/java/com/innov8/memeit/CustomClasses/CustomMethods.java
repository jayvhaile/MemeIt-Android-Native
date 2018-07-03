package com.innov8.memeit.CustomClasses;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.innov8.memeit.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by Biruk on 5/11/2018.
 */

public class CustomMethods {

    // Set window content to draw behind the status and navigation bars.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setFullScreen(Activity a) {
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static Bitmap getBitmapFromUri(Uri imageUri, Context c) {
        final InputStream imageStream;
        Bitmap selectedImage = BitmapFactory.decodeResource(c.getResources(), R.drawable.button);
        try {
            imageStream = c.getContentResolver().openInputStream(imageUri);
            selectedImage = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return selectedImage;
    }

    /**
     * This method makes the activity draw its content behind the status bar giving more space
     *
     * @param a
     */
    public static void makeActivityDrawBehindStatusBar(Activity a) {
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            a.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * This method gives the login activity that scrolling effect
     *
     * @param first,second : The consecutive imageviews that follow each other and loop
     *                     to create an animation.
     */

    public static void makeBackgroundScrollAnimate(Activity a, @IdRes int first, @IdRes int second) {
        final ImageView backgroundOne = (ImageView) a.findViewById(first);
        final ImageView backgroundTwo = (ImageView) a.findViewById(second);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    public static void makeWindowSeamless(Activity a) {
        a.requestWindowFeature(Window.FEATURE_NO_TITLE);
        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * This method gives the login activity that scrolling effect
     *
     * @param edittexts : The edittexts that need to be font-ized.
     */
    public static void makeEditTextsAvenir(Activity a, View v, int... edittexts) {
        final String asset = "fonts/avenir.ttf";
        for (int res : edittexts) {
            ((EditText) v.findViewById(res))
                    .setTypeface(
                            Typeface.createFromAsset(a.getAssets(), asset)
                    );
        }
    }

    public static float convertDPtoPX(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;

    }

    public static float convertPXtoDP(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }


    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView =
                (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass()
                    .getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item =
                        (BottomNavigationItemView) menuView.getChildAt
                                (i);
                item.setShiftingMode(false);
// set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode");
        }
    }

}
