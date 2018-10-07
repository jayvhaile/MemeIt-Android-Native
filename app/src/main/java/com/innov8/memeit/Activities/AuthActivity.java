package com.innov8.memeit.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.google.android.material.snackbar.Snackbar;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ScrollingImageDrawable;
import com.innov8.memeit.Fragments.LoginFragment;
import com.innov8.memeit.Fragments.SetupFragment;
import com.innov8.memeit.Fragments.SignUpFragment;
import com.innov8.memeit.KUtilsKt;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItClient;
import com.theartofdev.edmodo.cropper.CropImage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AuthActivity extends AppCompatActivity {
    public static final String STARTING_FRAGMENT_PARAM = "frag";
    public static final int FRAGMENT_LOGIN = 0;
    public static final int FRAGMENT_SIGNUP = 1;
    public static final int FRAGMENT_SETUP = 2;

    public SignUpFragment signUpFragment;
    public LoginFragment loginFragment;
    public SetupFragment setupFragment;
    CoordinatorLayout authCoordinate;
    private int currentFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomMethods.makeWindowSeamless(this);
        setContentView(R.layout.activity_auth);
        authCoordinate=findViewById(R.id.auth_coordinate);

        authCoordinate.setBackground(new ScrollingImageDrawable(this,R.drawable.many_pics_optimized,
                KUtilsKt.getScreenWidth(),
                KUtilsKt.getScreenHeight()));
        initFragments();
        if (getIntent() != null) {
            int i = getIntent().getIntExtra(STARTING_FRAGMENT_PARAM, FRAGMENT_LOGIN);
            setCurrentFragment(i);
        } else {
            setCurrentFragment(FRAGMENT_LOGIN);
        }

    }

    private Fragment getFragmentForInt(int i) {
        switch (i) {
            case FRAGMENT_LOGIN:
                return loginFragment;
            case FRAGMENT_SIGNUP:
                return signUpFragment;
            case FRAGMENT_SETUP:
                return setupFragment;
            default:
                throw new IllegalArgumentException("Index should have been either 0,1 or 2!");
        }
    }

    private void initFragments() {
        signUpFragment = new SignUpFragment();
        loginFragment = new LoginFragment();
        setupFragment = new SetupFragment();
    }

    public void setCurrentFragment(int i) {
        currentFrag = i;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.holder, getFragmentForInt(i))
                .commit();
    }

    /*This method returns the height of the navigation buttons*/
    public static int getSoftButtonsBarHeight(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            a.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            return realHeight > usableHeight ? realHeight - usableHeight : 0;
        }

        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItClient.Auth.GOOGLE_SIGN_IN_REQUEST_CODE) {
            getFragmentForInt(currentFrag).onActivityResult(requestCode, resultCode, data);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            getFragmentForInt(currentFrag).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showError(String error){
        Snackbar sb=Snackbar.make(authCoordinate,error,Snackbar.LENGTH_LONG);
        sb.show();
    }

}
