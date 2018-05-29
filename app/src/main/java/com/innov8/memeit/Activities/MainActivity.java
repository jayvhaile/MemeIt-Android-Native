package com.innov8.memeit.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.innov8.memegenerator.MemeGeneratorActivity;
import com.innov8.memeit.Fragments.HomeFragment;
import com.innov8.memeit.Fragments.MeFragment;
import com.innov8.memeit.Fragments.TrendingFragment;
import com.innov8.memeit.Fragments.FavoritesFragment;
import com.innov8.memeit.R;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;
import com.memeit.backend.dataclasses.User;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.main_viewpager)
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo: Biruk uncomment later.
//        if(!MemeItAuth.getInstance().isSignedIn()){
//            startActivity(new Intent(this,SignInActivity.class));
//            finish();
//        }else if(!MemeItAuth.getInstance().isUserDataSaved()){
//            startActivity(new Intent(this,SignUpDetailsActivity.class));
//            finish();
//        }
        MemeItAuth.getInstance().getUser(new OnCompleteListener<User>() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(MainActivity.this, "Current User: "+user.getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(MainActivity.this, "fuck: "  +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SpaceNavigationView spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_trending_up_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_favorite_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_account_circle_black_24dp));
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                startActivity(new Intent(MainActivity.this,MemeGeneratorActivity.class));
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                viewPager.setCurrentItem(itemIndex);
                if(itemIndex == 3) findViewById(R.id.toolbar2).setVisibility(View.GONE);
                else findViewById(R.id.toolbar2).setVisibility(View.VISIBLE);
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });

        fragments.add(new HomeFragment());
        fragments.add(new TrendingFragment());
        fragments.add(new FavoritesFragment());
        fragments.add(new MeFragment());

        // Setting viewpager adapter
        PagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
//
//        // Navigation drawer
//        final DuoDrawerLayout drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
//        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, drawerLayout, ((Toolbar) findViewById(R.id.uselessToolbar)),
//                R.string.navigation_drawer_open,
//                R.string.navigation_drawer_close);
//
//        drawerLayout.setDrawerListener(drawerToggle);
//        findViewById(R.id.toolbar_drawer_toggle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(drawerLayout.isDrawerOpen())drawerLayout.closeDrawer();
//                else drawerLayout.openDrawer();
//            }
//        });
//        drawerLayout.closeDrawer();
//        drawerToggle.syncState();
        new SlidingRootNavBuilder(this)
                .withSavedState(savedInstanceState) //If you call the method, layout will restore its opened/closed state
                .withContentClickableWhenMenuOpened(false)
                .withRootViewElevationPx(5)
                .withRootViewScale(0.5f)
                .withToolbarMenuToggle((Toolbar) findViewById(R.id.toolbar2))
                .withMenuLayout(R.layout.menu_drawer)
                .inject();
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
}

