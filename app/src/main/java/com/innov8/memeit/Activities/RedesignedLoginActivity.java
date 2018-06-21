package com.innov8.memeit.Activities;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class RedesignedLoginActivity extends AppCompatActivity {

    List<Fragment> fragments = new ArrayList<>();
    public static ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomMethods.makeWindowSeamless(this);
        setContentView(R.layout.activity_redesigned_login);
        ButterKnife.bind(this);
        CustomMethods.makeBackgroundScrollAnimate(this,R.id.background_login_1,R.id.background_login_2);

        viewPager = findViewById(R.id.login_viewpager);

        fragments.add(new LoginFragment());
        fragments.add(new SignUpFragment());
        fragments.add(new SetupFragment());

        PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    /*This method returs the height of the navigation buttons*/
    private static int getSoftButtonsBarHeight(Activity a) {
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
    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int pos) {
            return fragments.get(pos>3 ? 3 : pos);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public static class SetupFragment extends Fragment{
        public SetupFragment() {}

        View view;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.activity_setup_profile, container, false);

            EditText name = view.findViewById(R.id.name_setup);
            EditText username = view.findViewById(R.id.username_setup);
            EditText tags = view.findViewById(R.id.tags);



            CustomMethods.makeEditTextsAvenir(getActivity(),view,R.id.name_setup,R.id.tags_setup,R.id.username_setup);
            return view;
        }

    }

    public static class LoginFragment extends Fragment{
        public LoginFragment() {}

        View view;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_login, container, false);


            CustomMethods.makeEditTextsAvenir(getActivity(),view,R.id.signup_password,R.id.signup_username);

            final EditText email = view.findViewById(R.id.signup_username);
            final EditText password = view.findViewById(R.id.signup_password);

                    /* This sets the bottom padding that makes the views not go underneath the navigation bar */
            view.findViewById(R.id.rel).setPadding(
                    0,
                    (int) (16*getResources().getDisplayMetrics().density + 0.5f),
                    0,
                    (int) (10*getResources().getDisplayMetrics().density + 0.5f) + getSoftButtonsBarHeight(getActivity()));

            view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RedesignedLoginActivity.viewPager.setCurrentItem(1);
                }
            });
            view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CustomMethods.isEmailValid(email.getText().toString()) && password.getText().toString().length()>=8){
                        view.findViewById(R.id.spin_kit_login).setVisibility(View.VISIBLE);
                        //Todo: Handle auth here
                    }
                    else{

                    }
                }
            });

            return view;
        }

    }
    public static class SignUpFragment extends Fragment{
        public SignUpFragment() {}
        View view;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_signup, container, false);
            final EditText email = view.findViewById(R.id.signup_username);
            final EditText password = view.findViewById(R.id.signup_password);

            CustomMethods.makeEditTextsAvenir(getActivity(),view,R.id.signup_password,R.id.signup_username,R.id.signup_confirm);

                    /* This sets the bottom padding that makes the views not go underneath the navigation bar */
            view.findViewById(R.id.rel).setPadding(
                    0,
                    (int) (16*getResources().getDisplayMetrics().density + 0.5f),
                    0,
                    (int) (10*getResources().getDisplayMetrics().density + 0.5f) + getSoftButtonsBarHeight(getActivity()));
            view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RedesignedLoginActivity.viewPager.setCurrentItem(0);
                }
            });
            view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(2);
                    if(CustomMethods.isEmailValid(email.getText().toString()) && password.getText().toString().length()>=8){
                        view.findViewById(R.id.spin_kit_login).setVisibility(View.VISIBLE);
                        //Todo: Handle auth here
                    }
                    else{

                    }
                }
            });
            return view;
        }

    }
}
