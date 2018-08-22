package com.innov8.memeit.CustomClasses;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import androidx.annotation.IdRes;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.innov8.memeit.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
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
    public static boolean isUsernameValid(String username) {
        //todo check if user name is valid (it should exclude such characters)
        return username!=null &&username.length()>=1;//todo change back to 5
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
    public static void makeWindowTransparent(Activity a) {
        a.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        a.getWindow().setStatusBarColor(Color.TRANSPARENT);
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



    public static String formatNumber(int num){
        if(num<1000){
          return String.valueOf(num);
        }else{
            float d=num/1000.0f;
            return String.format("%.2fk",d);
        }
    }
    public static String formatNumber(int num,String suffix){
        if(num<1000){
            return String.format("%d %s",num,suffix);
        }else{
            float d=num/1000.0f;
            return String.format("%.2fk %s",d,suffix);
        }

    }
    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }
    public static void toggleVisibilityAndAnimate(final View view){
        view.animate().setDuration(200).alpha(view.getVisibility()==View.VISIBLE ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(view.getVisibility()==View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }
}
