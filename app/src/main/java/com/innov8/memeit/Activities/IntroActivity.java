package com.innov8.memeit.Activities;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.R;

import java.util.Arrays;
import java.util.List;

public class IntroActivity extends AppCompatActivity{
    public static final String NUMBER = "number";

    ViewPager viewPager;
    ImageView background_gradient;
    ViewPagerAdapter adapter;
    View[] dots;


    Context c;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        init();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                @DrawableRes int res;
                switch (position){
                    case 1:res = R.drawable.gradient_1;
                    case 2:res = R.drawable.gradient_2;
                    case 3:res = R.drawable.gradient_3;
                    case 4:res = R.drawable.gradient_4;
                    case 5:res = R.drawable.gradient_5;
                    default: res = R.drawable.gradient_1;
                }
                selectDot(position);
                CustomMethods.changeImageBackgroundWithAnim(c,background_gradient,BitmapFactory.decodeResource(getResources(),res));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);

    }

    private void init() {
        viewPager = findViewById(R.id.intro_pager);
        background_gradient = findViewById(R.id.intro_bg);
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view;
            switch (getArguments().getInt(NUMBER)){
                case 1: view = inflater.inflate(R.layout.slide_1, container, false);
                break;
                case 2: view = inflater.inflate(R.layout.slide_2, container, false);

                break;
                case 3: view = inflater.inflate(R.layout.slide_3, container, false);

                break;
                case 4: view = inflater.inflate(R.layout.slide_4, container, false);

                break;
                case 5: view = inflater.inflate(R.layout.slide_5, container, false);

                break;
                default:view = inflater.inflate(R.layout.slide_1, container, false);
            }
            return view;
        }

    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        List<? extends Fragment> fragments;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = Arrays.asList(
                    IntroFragment.getInstance(1),
                    IntroFragment.getInstance(2),
                    IntroFragment.getInstance(3),
                    IntroFragment.getInstance(4),
                    IntroFragment.getInstance(5)
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
