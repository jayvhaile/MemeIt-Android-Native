package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.innov8.memegenerator.MemeEditorActivity;
import com.innov8.memegenerator.SimpleMemeGenerator;
import com.innov8.memeit.CustomViews.BottomNavigation;
import com.innov8.memeit.Fragments.FavoritesFragment;
import com.innov8.memeit.Fragments.HomeFragment;
import com.innov8.memeit.Fragments.MeFragment;
import com.innov8.memeit.Fragments.MemeListFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_viewpager)
    ViewPager viewPager;
    @BindView(R.id.bottom_nav)
    BottomNavigation bottom_nav;
    @BindView(R.id.toolbar2)
    Toolbar mToolbar;


    private String titles[]={"Home","Trending","Favorites","Me"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MemeItAuth.getInstance().isSignedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        MemeItAuth.getInstance().isUserDataSaved(new OnCompleteListener<Boolean>() {
            @Override
            public void onSuccess(Boolean saved) {
                if (saved) {
                    initUI(savedInstanceState);
                } else {
                    goToSignUpDetails();
                }
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(MainActivity.this, "error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToSignUpDetails() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra(AuthActivity.STARTING_FRAGMENT_PARAM, AuthActivity.FRAGMENT_SETUP);
        startActivity(intent);
        finish();
    }


    private void initUI(Bundle savedInstanceState) {

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initBottomNav();

        // Setting viewpager adapter
        PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        new SlidingRootNavBuilder(this)
                .withSavedState(savedInstanceState) //If you call the method, layout will restore its opened/closed state
                .withContentClickableWhenMenuOpened(false)
                .withRootViewElevationPx(5)
                .withRootViewScale(0.5f)
                .withToolbarMenuToggle((Toolbar) findViewById(R.id.toolbar2))
                .withMenuLayout(R.layout.menu_drawer)
                .inject();


    }
    private void initToolbar(){
        this.setSupportActionBar(this.mToolbar);
    }
    private void initBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.menu_trending:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.menu_create:
                        startActivity(new Intent(MainActivity.this, MemeEditorActivity.class));
                        return true;
                    case R.id.menu_favorites:
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.menu_me:
                        viewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu,menu);
        return true;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            //i changed this cuz storing the fragment in array prevents it
            // from being garbage collected which is bad for performance
            switch (pos) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return MemeListFragment.withLoader(new MemeListFragment.TrendingLoader());
                case 2:
                    return new FavoritesFragment();
                case 3:
                    return new MeFragment();
                default:
                    throw new IllegalArgumentException("should be 0-3");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}

