package com.innov8.memeit.Fragments;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.tabs.TabLayout;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.CustomClasses.UserListLoader;
import com.innov8.memeit.Fragments.ProfileFragments.UserListFragment;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.MyUser;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import okhttp3.ResponseBody;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class ProfileFragment extends Fragment {
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
        return inflater.inflate(R.layout.profile_page, container, false);
    }

    TabLayout tabLayout;
    ViewPager pager;
    ViewPagerAdapter adapter;
    List<Fragment> fragments;

    TextView nameV;
    TextView followerV;
    TextView memeCountV;
    SimpleDraweeView profileV;
    TextView followBtnV;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tabs_profile);
        pager = view.findViewById(R.id.profile_viewpager);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);


        nameV = view.findViewById(R.id.profile_name);


        followerV = view.findViewById(R.id.profile_followers_count);
        memeCountV = view.findViewById(R.id.profile_meme_count);
        profileV = view.findViewById(R.id.profile_image);
        followBtnV = view.findViewById(R.id.profile_follow_btn);
        followBtnV.setVisibility(isMe() ? GONE : VISIBLE);

        followBtnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (followBtnV.getText().toString().equalsIgnoreCase("Follow")) {
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
                } else if (followBtnV.getText().equals("Unfollow")) {
                    MemeItUsers.getInstance().unFollowUser(userID, new OnCompleteListener<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            Toast.makeText(getContext(), "UnFollowed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Error error) {
                            Toast.makeText(getContext(), "failed follow: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });
        MyUser myUser= MemeItUsers.getInstance().getMyUser(getContext());
        nameV.setText(myUser.getName());
        ImageUtils.loadImageFromCloudinaryTo(profileV, myUser.getImageUrl());
        if(userData!=null)updateView();
        loadData();

    }

    User userData;

    private void loadData() {
        OnCompleteListener<User> onCompleteListener = new OnCompleteListener<User>() {
            @Override
            public void onSuccess(User user) {
                userData = user;
                updateView();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getActivity(), "failer: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        if (isMe())
            MemeItUsers.getInstance().getMyUserDetail(getContext(), onCompleteListener);
        else
            MemeItUsers.getInstance().getUserDetailFor(userID, onCompleteListener);

    }

    private void updateView() {
        nameV.setText(userData.getName());
        ImageUtils.loadImageFromCloudinaryTo(profileV, userData.getImageUrl());

        followerV.setText(CustomMethods.formatNumber(userData.getFollowerCount()));
        memeCountV.setText(CustomMethods.formatNumber(userData.getPostCount()));
        if (userData.isFollowedByMe()) {
            followBtnV.setText("Unfollow");
        } else {
            followBtnV.setText("Follow");
        }
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
                    return MemeListFragment.newInstanceForUserPosts(userID);
                case 1:
                    return UserListFragment.newInstance(UserListLoader.FOLLOWING_LOADER, userID);
                case 2:
                    return UserListFragment.newInstance(UserListLoader.FOLLOWER_LOADER, userID);

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
        String id = null;
        try {
            id = MemeItUsers.getInstance().getMyUser(getContext()).getUserID();
        } catch (NullPointerException e) {

        }
        return TextUtils.isEmpty(userID) || userID.equals(id);
    }

}
