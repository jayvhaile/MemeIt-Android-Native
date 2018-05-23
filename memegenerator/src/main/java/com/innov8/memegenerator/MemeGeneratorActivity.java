package com.innov8.memegenerator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.innov8.memegenerator.Fragments.LayoutOptionsFragment;
import com.innov8.memegenerator.Fragments.StickerOptionsFragment;
import com.innov8.memegenerator.Fragments.TextOptionsFragment;

import java.util.ArrayList;
import java.util.List;

public class MemeGeneratorActivity extends ParentActivity {

    ViewPager viewPager;
    List<Fragment> fragments = new ArrayList<>();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int i = item.getItemId();
            if (i == R.id.nav_layout) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (i == R.id.nav_text) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (i == R.id.nav_sticker) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_generator);

        viewPager= (ViewPager) findViewById(R.id.main_viewpager);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragments.add(new LayoutOptionsFragment());
        fragments.add(new TextOptionsFragment());
        fragments.add(new StickerOptionsFragment());

        // Setting viewpager adapter
        PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        super.setupToolbarAsDisplayHome("Meme Creator");
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meme_generator_toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i == R.id.next) {
            //todo add next action here
        }
        return false;
    }
 }




