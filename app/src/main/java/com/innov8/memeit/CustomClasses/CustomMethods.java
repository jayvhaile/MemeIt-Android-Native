package com.innov8.memeit.CustomClasses;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.innov8.memeit.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by Biruk on 5/11/2018.
 */

public class CustomMethods {

    // Set window content to draw behind the status and navigation bars.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setFullScreen(Activity a){
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

    public static Bitmap getBitmapFromUri(Uri imageUri, Context c){
        final InputStream imageStream;
        Bitmap selectedImage = BitmapFactory.decodeResource(c.getResources(),R.drawable.button);
        try {
            imageStream = c.getContentResolver().openInputStream(imageUri);
            selectedImage = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return selectedImage;
    }
}
