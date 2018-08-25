package com.innov8.memeit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.innov8.memegenerator.MemeChooser;
import com.innov8.memegenerator.MemeTemplateMaker;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.CustomClasses.Chip;
import com.innov8.memeit.CustomClasses.MemeLoader;
import com.innov8.memeit.CustomViews.BottomNavigation;
import com.innov8.memeit.Fragments.HomeFragment;
import com.innov8.memeit.Fragments.MemeListFragment;
import com.innov8.memeit.Fragments.ProfileFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    BottomNavigation bottom_nav;
    Toolbar mToolbar;
    ImageView openDrawer;
    SlidingRootNav rootNav;
    EditText searchQuery;
    Group searchLayout;
    ArrayList<Chip> tags = new ArrayList<>();
    SpinKitView searchLoading;
    boolean searchedBefore = false;
    ChipsInput chips;

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

        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.main_viewpager);
        bottom_nav = findViewById(R.id.bottom_nav);
        mToolbar = findViewById(R.id.toolbar2);
        chips = findViewById(R.id.tags);
        searchLayout = findViewById(R.id.searchbar);
        mToolbar = findViewById(R.id.toolbar2);
        openDrawer = findViewById(R.id.drawer_button);
        searchQuery = findViewById(R.id.search_query);
        searchLoading = findViewById(R.id.search_loading);
        initToolbar();
        initBottomNav();


        // Setting viewpager adapter
        PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        rootNav = new SlidingRootNavBuilder(this)
                .withSavedState(savedInstanceState) //If you call the method, layout will restore its opened/closed state
                .withContentClickableWhenMenuOpened(false)
                .withRootViewElevationPx(5)
                .withRootViewScale(0.5f)
                .withMenuLayout(R.layout.menu_drawer2)
                .inject();


        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItAuth.getInstance().signOut();
                recreate();
            }
        });
        findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootNav.openMenu(true);
            }
        });
        findViewById(R.id.notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MemeTemplateMaker.class));
            }
        });
        findViewById(R.id.bring_up_search_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSearch();
                searchQuery.setFocusableInTouchMode(true);
                searchQuery.requestFocus();
            }
        });
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.notifications:
//                startActivity(new Intent(getApplicationContext(),MemeTemplateMaker.class));
//                break;
//            case R.id.drawer_button:
//                rootNav.openMenu(true);
//                break;
//            case R.id.bring_up_search_bar:
//                initSearch();
//                break;
//            case R.id.logout:
//                MemeItAuth.getInstance().signOut();
//                break;
//        }
    }

    private void initSearch() {
        searchLayout.setVisibility(View.VISIBLE);
        chips.setVisibility(View.GONE);
        View.OnClickListener searchListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.search_button:
                        searchLoading.setVisibility(View.VISIBLE);
                        //todo: Jv and biruk: add the fetching of results here using @id/search_suggestions as a recyclerview, tags as the list of tags
                        break;
                    case R.id.close_searchbar:
                        searchQuery.setText("");
                        chips.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                        searchLoading.setVisibility(View.GONE);
                        tags.clear();
                        chips.setFilterableList(tags);
                        break;
                }
            }
        };
        if (!searchedBefore) {
            findViewById(R.id.search_button).setOnClickListener(searchListener);
            findViewById(R.id.close_searchbar).setOnClickListener(searchListener);
            searchQuery.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    if (endsInSpace(s.toString()) && startsWithHashtag(searchQuery)) {
                        Chip chip = new Chip().setLabel(fetchText(searchQuery));
                        chips.setVisibility(View.VISIBLE);
                        chips.addChip(chip);
                        searchQuery.setText("");
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            chips.addChipsListener(new ChipsInput.ChipsListener() {
                @Override
                public void onChipAdded(ChipInterface chipInterface, int i) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchQuery.setFocusableInTouchMode(true);
                            searchQuery.requestFocus();
                        }
                    },10);
                    tags.add((Chip) chipInterface);
                }

                @Override
                public void onChipRemoved(ChipInterface chipInterface, int i) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            searchQuery.setFocusableInTouchMode(true);
                            searchQuery.requestFocus();
                        }
                    },10);
                    tags.remove(getChipIndexOf((Chip) chipInterface));
                    if(tags.size() == 0){
                        chips.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence) {

                }
            });
            searchedBefore = true;
        }
    }
    public int getChipIndexOf(Chip chip){
        int ind = 0;
        for(int i = 0;i<tags.size();i++){
            if(tags.get(i).getLabel().equals(chip.getLabel())) ind = i;
        }
        return ind;
    }

    public boolean endsInSpace(String s) {
        try {
            return (s.charAt(s.length() - 1) + "").equals(" ");
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean startsWithHashtag(TextView textView) {
        return (textView.getText().toString().trim().charAt(0) + "").equals("#");
    }

    public String fetchText(TextView textView) {
        String text = textView.getText().toString().trim();
        return text.substring(1);
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
        ((TextView) findViewById(R.id.title)).setText(titles[index]);
    }

    public void setTitle(String name) {
        if (viewPager.getCurrentItem() == 3)
            mToolbar.setTitle(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menu_notif:
//                startActivity(new Intent(this,MemeTemplateMaker.class));
//        }
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
            Log.w("pos", pos + "");
            switch (pos) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, MemeLoader.TRENDING_LOADER);
                case 2:
                    return MemeListFragment.newInstance(MemeAdapter.LIST_ADAPTER, MemeLoader.FAVOURITE_LOADER);
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

