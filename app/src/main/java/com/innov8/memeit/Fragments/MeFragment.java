package com.innov8.memeit.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.innov8.memeit.Fragments.ProfileFragments.FollowersFragment;
import com.innov8.memeit.Fragments.ProfileFragments.FollowingFragment;
import com.innov8.memeit.Fragments.ProfileFragments.MemesFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;


public class MeFragment extends Fragment {


    View view;
    TabLayout tabLayout;
    ViewPager pager;
    ViewPagerAdapter adapter;
    List<Fragment> fragments;

    TextView nameV;
    ImageView profileV;
    public MeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_me, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        pager = (ViewPager) view.findViewById(R.id.profile_viewpager);
        fragments = new ArrayList<>();
        adapter = new ViewPagerAdapter(getFragmentManager());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new MemesFragment(), "Memes");
        adapter.addFragment(new FollowersFragment(), "Followers");
        adapter.addFragment(new FollowingFragment(), "Following");
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);

        pager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        nameV=(TextView) view.findViewById(R.id.name);
        profileV=view.findViewById(R.id.profile_image);

        loadData();


        return view;
    }

    private void loadData(){
        MemeItUsers.getInstance().getMyUserDetail(new OnCompleteListener<User>() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(getActivity(), "success:"+user.getName(), Toast.LENGTH_SHORT).show();

                nameV.setText(user.getName());
                if(!TextUtils.isEmpty(user.getImageUrl())){
                    Glide.with(MeFragment.this)
                            .load(user.getImageUrl())
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileV);
                }
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getActivity(), "failer: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
