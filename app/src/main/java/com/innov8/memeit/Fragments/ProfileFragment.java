package com.innov8.memeit.Fragments;


import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.Activities.MainActivity;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.Fragments.ProfileFragments.FollowersFragment;
import com.innov8.memeit.Fragments.ProfileFragments.FollowingFragment;
import com.innov8.memeit.Fragments.ProfileFragments.MemesFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;
import com.memeit.backend.utilis.onComplete;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ProfileFragment extends Fragment {


    View view;
    TabLayout tabLayout;
    ViewPager pager;
    ViewPagerAdapter adapter;
    List<Fragment> fragments;

    //TextView nameV;
    TextView followerV;
    TextView followingV;
    TextView memeCountV;
    SimpleDraweeView profileV;
    Button followBtnV;


    String userID;

    public static ProfileFragment newInstance(String uid) {
        ProfileFragment pf = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid);
        pf.setArguments(bundle);
        return pf;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public ProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            userID = getArguments().getString("uid", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.layout_user_profile, container, false);

        tabLayout = view.findViewById(R.id.profile_tabs);
        pager = view.findViewById(R.id.profile_viewpager);
        adapter = new ViewPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);


        followerV = view.findViewById(R.id.profile_followers_count);
        followingV = view.findViewById(R.id.profile_following_count);
        memeCountV = view.findViewById(R.id.profile_meme_count);
        profileV = view.findViewById(R.id.profile_image);
        followBtnV = view.findViewById(R.id.profile_follow_btn);
        followBtnV.setVisibility(isMe() ? GONE : VISIBLE);

        followBtnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItUsers.getInstance().followUser(userID, new OnCompleteListener<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(getContext(), "failed follow: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        RoundingParams rp = new RoundingParams();
        rp.setBorder(Color.WHITE, 10);
        rp.setRoundAsCircle(true);
        profileV.setHierarchy(GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setRoundingParams(rp)
                .build());
        loadData();

        return view;
    }

    private void loadData() {
        OnCompleteListener<User> onCompleteListener = new OnCompleteListener<User>() {
            @Override
            public void onSuccess(User user) {
                //((MainActivity) getActivity()).setTitle(user.getName());
                if (!TextUtils.isEmpty(user.getImageUrl())) {
                    ImageUtils.loadImageFromCloudinaryTo(profileV, user.getImageUrl());
                }
                followerV.setText(CustomMethods.formatNumber(user.getFollowerCount()));
                followingV.setText(CustomMethods.formatNumber(user.getFollowingCount()));
                memeCountV.setText(CustomMethods.formatNumber(user.getPostCount()));
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getActivity(), "failer: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        if (isMe())
            MemeItUsers.getInstance().getMyUserDetail(new onComplete<User>() {
                @Override
                public void onResponceFromCache(User user) {
                    Toast.makeText(getContext(), "from Cache", Toast.LENGTH_SHORT).show();
                    setDataToView(user);

                }

                @Override
                public void onResponceFromNetwork(User user) {
                    Toast.makeText(getContext(), "from Network", Toast.LENGTH_SHORT).show();
                    setDataToView(user);
                }

                @Override
                public void onFailed(String error, boolean fromCache) {
                    Toast.makeText(getActivity(), "failer: fromCache: "+fromCache+"\n" + error, Toast.LENGTH_LONG).show();
                }
            });
        else
            MemeItUsers.getInstance().getUserDetailFor(userID, onCompleteListener);

    }
    private void setDataToView(User user){
        if (!TextUtils.isEmpty(user.getImageUrl())) {
            ImageUtils.loadImageFromCloudinaryTo(profileV, user.getImageUrl());
        }
        followerV.setText(CustomMethods.formatNumber(user.getFollowerCount()));
        followingV.setText(CustomMethods.formatNumber(user.getFollowingCount()));
        memeCountV.setText(CustomMethods.formatNumber(user.getPostCount()));
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        String titles[] = {"Memes", "Followings", "Followers"};

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MemeListFragment.newInstance(new MemeListFragment.MyMemesLoader(),
                            new MemeAdapter.Grid(getContext()));
                case 1:
                    return new FollowersFragment();
                case 2:
                    return new FollowersFragment();

                default:
                    throw new IllegalArgumentException("Should be 1-3");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private boolean isMe() {
        return TextUtils.isEmpty(userID);
        //todo jv check also if the user id equals this user's
    }

}
