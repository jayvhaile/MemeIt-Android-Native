package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.innov8.memeit.Adapters.MemeAdapters.MemeAdapter;
import com.innov8.memeit.Loaders.FavoriteMemeLoader;
import com.innov8.memeit.Loaders.HomeMemeLoader;
import com.innov8.memeit.Loaders.TrendingMemeLoader;
import com.innov8.memeit.CustomViews.BottomNavigation;
import com.innov8.memeit.CustomViews.SearchToolbar;
import com.innov8.memeit.Fragments.MemeListFragment;
import com.innov8.memeit.Fragments.ProfileFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItClient;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.OnCompleted;
import com.minibugdev.drawablebadge.DrawableBadge;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    MyPagerAdapter pagerAdapter;
    private String titles[] = {"Home", "Trending", "Favorites"};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MemeItClient.Auth.INSTANCE.isSignedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        if (MemeItClient.Auth.INSTANCE.isUserDataSaved()) {
            initUI(savedInstanceState);
        } else {
            goToSignUpDetails();
        }
    }

    private void goToSignUpDetails() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra(AuthActivity.Companion.getSTARTING_MODE_PARAM(), AuthActivity.Companion.getMODE_PERSONALIZE());
        startActivity(intent);
        finish();
    }

    AppBarLayout appbar;

    private void initUI(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.main_viewpager);
        bottom_nav = findViewById(R.id.bottom_nav);
        appbar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar2);
        initToolbar();
        initBottomNav();
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(0);
        SlidingRootNavBuilder sbd = new SlidingRootNavBuilder(this)
                .withSavedState(savedInstanceState)
                .withContentClickableWhenMenuOpened(false)
                .withRootViewElevationPx(5)
                .withRootViewScale(0.5f)
                .withMenuLayout(R.layout.drawer_main); //todo fix the menu_drawer crashes due to the drawables on api level < 21
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sbd.withToolbarMenuToggle(mToolbar);
        }
        rootNav = sbd.inject();
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItClient.Auth.INSTANCE.signOut();
                recreate();
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        findViewById(R.id.menu_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
            }
        });
        findViewById(R.id.menu_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new AppInviteInvitation.IntentBuilder("MemeIt")
                        .setMessage("the world of meme")
                        .setDeepLink(Uri.parse("meme"))
                        .setCallToActionText("download")
                        .build();
                startActivityForResult(intent, 101);
            }
        });

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int selected = savedInstanceState.getInt("selected");
        bottom_nav.select(selected, true);
    }

    private void initToolbar() {
        this.setSupportActionBar(this.mToolbar);
    }

    private void initBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (searchItem != null && searchItem.isActionViewExpanded())
                    searchItem.collapseActionView();
                appbar.setExpanded(true, false);
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        setTitle(0);
                        viewPager.setCurrentItem(0, false);
                        getSupportActionBar().show();
                        return true;
                    case R.id.menu_trending:
                        setTitle(1);
                        viewPager.setCurrentItem(1, false);
                        getSupportActionBar().show();
                        return true;
                    case R.id.menu_create:
                        startActivity(new Intent(MainActivity.this, MemeChooser.class));
                        return true;
                    case R.id.menu_favorites:
                        setTitle(2);
                        getSupportActionBar().show();
                        viewPager.setCurrentItem(2, false);
                        return true;
                    case R.id.menu_me:
                        getSupportActionBar().hide();
                        viewPager.setCurrentItem(3, false);
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

    private SearchToolbar searchToolbar;
    MenuItem searchItem;
    MenuItem notifItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu, menu);

        searchItem = menu.findItem(R.id.menu_search);

        searchToolbar = (SearchToolbar) searchItem.getActionView();

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
        notifItem = menu.findItem(R.id.menu_notif);
        loadNotifCount();

        return super.onCreateOptionsMenu(menu);
    }

    private void loadNotifCount() {
        if (notifItem == null) return;
        MemeItUsers.INSTANCE.getNotifCount().enqueue(new OnCompleted<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Drawable d = new DrawableBadge.Builder(MainActivity.this)
                        .drawableResId(R.drawable.ic_notifications_black_24dp)
                        .maximumCounter(99)
                        .build()
                        .get(integer);
                notifItem.setIcon(d);
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_notif:
                startActivity(new Intent(this, NotificationActivity.class));
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifCount();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected", bottom_nav.getSelectedIndex());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {
        List<? extends Fragment> fragments;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = Arrays.asList(
                    MemeListFragment.Companion.newInstance(MemeAdapter.HOME_ADAPTER, new HomeMemeLoader()),
                    MemeListFragment.Companion.newInstance(MemeAdapter.LIST_ADAPTER, new TrendingMemeLoader()),
                    MemeListFragment.Companion.newInstance(MemeAdapter.LIST_ADAPTER, new FavoriteMemeLoader()),
                    ProfileFragment.Companion.newInstance()
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

