package com.innov8.memeit.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.R;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class IntroActivity extends AppCompatActivity{
    public static final String NUMBER = "number";

    ViewPager viewPager;
    ViewPagerAdapter adapter;
    View[] dots;
    Context c;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomMethods.makeWindowSeamless(this);
        setContentView(R.layout.activity_intro);
        init();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(adapter);
        CustomMethods.makeBackgroundScrollAnimate(this, R.id.background_login_1, R.id.background_login_2);
        selectDot(0);
    }

    private void init() {
        findViewById(R.id.create_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, AuthActivity.class);
                intent.putExtra(AuthActivity.STARTING_FRAGMENT_PARAM, AuthActivity.FRAGMENT_SIGNUP);
                startActivity(intent);
            }
        });
        findViewById(R.id.intro_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, AuthActivity.class));

            }
        });
        viewPager = findViewById(R.id.intro_pager);
        c = this;
        dots = new View[]{
                findViewById(R.id.dot_1),
                findViewById(R.id.dot_2),
                findViewById(R.id.dot_3),
                findViewById(R.id.dot_4),
                findViewById(R.id.dot_5)
        };
    }

    public static class IntroFragment extends Fragment{

        public IntroFragment(){}

        public static IntroFragment getInstance(int number){
            IntroFragment fragment = new IntroFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(NUMBER,number);
            fragment.setArguments(bundle);
            return fragment;
        }
        String []titles;
        String []descriptions;
        int[] drawables;

        int pos;
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            pos=getArguments().getInt(NUMBER);
            titles = getContext().getResources().getStringArray(R.array.intro_slide_title);
            descriptions = getContext().getResources().getStringArray(R.array.intro_slide_description);
            drawables = getContext().getResources().getIntArray(R.array.intro_drawables);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.slide_1, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ImageView iv=view.findViewById(R.id.intro_image);
            TextView titleV=view.findViewById(R.id.intro_login);
            TextView descV=view.findViewById(R.id.intro_description);


            titleV.setText(titles[pos]);
            descV.setText(descriptions[pos]);
        }
    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        List<? extends Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = Arrays.asList(
                    IntroFragment.getInstance(0),
                    IntroFragment.getInstance(1),
                    IntroFragment.getInstance(2),
                    IntroFragment.getInstance(3),
                    IntroFragment.getInstance(4)
            );
        }

        @Override
        public Fragment getItem(int pos) {
            return fragments.get(pos);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
    private void selectDot(int position){
        for(int i = 0;i<dots.length;i++)
            dots[i].setAlpha(i==position ? 1f : 0.5f);
    }
}
