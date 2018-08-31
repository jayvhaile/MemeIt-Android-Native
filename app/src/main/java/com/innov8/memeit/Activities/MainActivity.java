package com.innov8.memeit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.innov8.memegenerator.MemeChooser;
import com.innov8.memegenerator.MemeTemplateMaker;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.CustomClasses.MemeLoader;
import com.innov8.memeit.CustomViews.BottomNavigation;
import com.innov8.memeit.CustomViews.ChipSearchToolbar;
import com.innov8.memeit.Fragments.MemeListFragment;
import com.innov8.memeit.Fragments.ProfileFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigation bottom_nav;
    Toolbar mToolbar;
    SlidingRootNav rootNav;
    ArrayList<String> tags = new ArrayList<>();
    MyPagerAdapter pagerAdapter;
    private String titles[] = {"Home", "Trending", "Favorites"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MemeItAuth.getInstance().isSignedIn(this)) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        MemeItAuth.getInstance().isUserDataSaved(this,new OnCompleteListener<Boolean>() {
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
                Snackbar.make(mToolbar.getRootView(),"Something went wrong",Snackbar.LENGTH_SHORT).show();
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
        viewPager = findViewById(R.id.main_viewpager);
        bottom_nav = findViewById(R.id.bottom_nav);
        mToolbar = findViewById(R.id.toolbar2);
        mToolbar = findViewById(R.id.toolbar2);
        initToolbar();
        initBottomNav();


        // Setting viewpager pagerAdapter
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        rootNav = new SlidingRootNavBuilder(this)
                .withSavedState(savedInstanceState)
                .withContentClickableWhenMenuOpened(false)
                .withRootViewElevationPx(5)
                .withRootViewScale(0.5f)
                .withMenuLayout(R.layout.menu_drawer2)
                .withToolbarMenuToggle(mToolbar)
                .inject();


        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItAuth.getInstance().signOut(MainActivity.this);
                recreate();
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }


    private void initToolbar() {
        this.setSupportActionBar(this.mToolbar);
    }

    private void initBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(searchItem.isActionViewExpanded())
                    searchItem.collapseActionView();
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
                        startActivity(new Intent(MainActivity.this, MemeChooser.class));
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
        getSupportActionBar().setTitle(titles[index]);
    }

    public void setTitle(String name) {
        if (viewPager.getCurrentItem() == 3)
            mToolbar.setTitle(name);
    }


    private void showSearch() {

        MemeListFragment frag = (MemeListFragment) pagerAdapter.fragments.get(viewPager.getCurrentItem());
        frag.setSearchMode(true);


    }

    private void closeSearch() {

        MemeListFragment frag = (MemeListFragment) pagerAdapter.fragments.get(viewPager.getCurrentItem());
        frag.setSearchMode(false);

    }


    private ChipSearchToolbar searchToolbar;
    MenuItem searchItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu, menu);

        searchItem = menu.findItem(R.id.menu_search);

        searchToolbar = (ChipSearchToolbar) searchItem.getActionView();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showSearch();
                return searchToolbar.onMenuItemActionExpand(item);
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                closeSearch();
                return searchToolbar.onMenuItemActionCollapse(item);
            }
        });
        searchToolbar.setOnSearch(new Function2<String, String[], Unit>() {
            @Override
            public Unit invoke(String s, String[] strings) {
                MemeListFragment frag = (MemeListFragment) pagerAdapter.fragments.get(viewPager.getCurrentItem());
                frag.search(s, strings);
                return null;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_notif:
                startActivity(new Intent(this, MemeTemplateMaker.class));
        }
        return true;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        List<? extends Fragment> fragments;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = Arrays.asList(
                    MemeListFragment.newInstance(MemeAdapter.HOME_ADAPTER, MemeLoader.HOME_LOADER),
                    MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, MemeLoader.TRENDING_LOADER),
                    MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, MemeLoader.FAVOURITE_LOADER),
                    ProfileFragment.newInstance()
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
}

