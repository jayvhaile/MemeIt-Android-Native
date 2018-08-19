package com.innov8.memeit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.innov8.memegenerator.MemeChooser;
import com.innov8.memegenerator.MemeTemplateMaker;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomViews.BottomNavigation;
import com.innov8.memeit.Fragments.FavoritesFragment;
import com.innov8.memeit.Fragments.HomeFragment;
import com.innov8.memeit.Fragments.MemeListFragment;
import com.innov8.memeit.Fragments.ProfileFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigation bottom_nav;
    Toolbar mToolbar;


    private String titles[] = {"Home", "Trending", "Favorites"};

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

        CustomMethods.makeWindowTransparent(this);

        setContentView(R.layout.activity_main);
        viewPager=findViewById(R.id.main_viewpager);
        bottom_nav=findViewById(R.id.bottom_nav);
        mToolbar=findViewById(R.id.toolbar2);
        mToolbar =  findViewById(R.id.toolbar2);
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
                .withMenuLayout(R.layout.menu_drawer2)
                .inject();


//        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MemeItAuth.getInstance().signOut();
//                recreate();
//            }
//        });


    }
    private void initToolbar() {
        this.setSupportActionBar(this.mToolbar);
    }

    private void initBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        setTitle(0);
                        viewPager.setCurrentItem(0);
                        getSupportActionBar().show();
                        return true;
                    case R.id.menu_trending:
                        setTitle(1);
                        viewPager.setCurrentItem(1);
                        getSupportActionBar().show();
                        return true;
                    case R.id.menu_create:
                        startActivity(new Intent(MainActivity.this,MemeChooser.class));
                        return true;
                    case R.id.menu_favorites:
                        setTitle(2);
                        getSupportActionBar().show();
                        viewPager.setCurrentItem(2);
                        return true;
                    case R.id.menu_me:
                        getSupportActionBar().hide();
                        viewPager.setCurrentItem(3);
                        return true;
                }
                return false;
            }
        });
    }

    public void setTitle(int index) {
        mToolbar.setTitle(titles[index]);
    }

    public void setTitle(String name) {
        if (viewPager.getCurrentItem() == 3)
            mToolbar.setTitle(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_notif:
                startActivity(new Intent(this,MemeTemplateMaker.class));
        }
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
            Log.w("pos",pos + "");
            switch (pos) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return MemeListFragment.newInstance(new MemeListFragment.TrendingLoader(),
                            new MemeAdapter.Listed(MainActivity.this));
                case 2:
                    return new FavoritesFragment();
                case 3:
                    return ProfileFragment.newInstance();
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

