package com.innov8.memeit.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.Fragments.ProfileFragments.FollowersFragment;
import com.innov8.memeit.Fragments.ProfileFragments.FollowingFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;


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

        view = inflater.inflate(R.layout.activity_profile_new, container, false);

        tabLayout = view.findViewById(R.id.tabs_profile);
        pager = view.findViewById(R.id.profile_viewpager);
        adapter = new ViewPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);


        followerV = view.findViewById(R.id.profile_followers_count);
        followingV = view.findViewById(R.id.profile_following_count);
        memeCountV = view.findViewById(R.id.profile_meme_count);
        profileV = view.findViewById(R.id.profile_image);
        followBtnV = view.findViewById(R.id.profile_follow_btn);
//        followBtnV.setVisibility(isMe() ? GONE : VISIBLE);

//        followBtnV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//              /*  MemeItUsers.getInstance().followUser(userID, new OnCompleteListener<ResponseBody>() {
//                    @Override
//                    public void onSuccess(ResponseBody responseBody) {
//                        Toast.makeText(getContext(), "Followed", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(Error error) {
//                        Toast.makeText(getContext(), "failed follow: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });*/
//            }
//        });
        RoundingParams rp = new RoundingParams();
        rp.setBorder(Color.WHITE, 10);
        rp.setRoundAsCircle(true);
//        profileV.setHierarchy(GenericDraweeHierarchyBuilder.newInstance(getResources())
//                .setRoundingParams(rp)
//                .build());
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
                /*followerV.setText(CustomMethods.formatNumber(user.getFollowerCount()));
                followingV.setText(CustomMethods.formatNumber(user.getFollowingCount()));
                memeCountV.setText(CustomMethods.formatNumber(user.getPostCount()));*/
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getActivity(), "failer: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        if (isMe())
            MemeItUsers.getInstance().getMyUserDetail(onCompleteListener);
        else
            MemeItUsers.getInstance().getUserDetailFor(userID, onCompleteListener);

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        String titles[] = {"Memes", "Following", "Followers"};

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
                    return new FollowingFragment();
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
        //todo biruk remove this funny todo
    }

}
