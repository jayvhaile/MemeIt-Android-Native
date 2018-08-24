package com.innov8.memeit.Fragments.ProfileFragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.innov8.memeit.Adapters.FollowerListAdapter;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends Fragment {
    private static final int LIMIT=50;
    private RecyclerView followerList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FollowerListAdapter followerAdapter;
    public FollowingFragment() {
        // Required empty public constructor
    }

    private int skip;

    //todo pass a userId to load for the specific user
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_following, container, false);

        swipeRefreshLayout=view.findViewById(R.id.swipe_to_refresh);
        followerList=view.findViewById(R.id.followers_recycler_view);
        setupUI();
        return view;
    }

    private void setupUI(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        followerList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        DefaultItemAnimator animator=new DefaultItemAnimator();
        followerList.setItemAnimator(animator);
        load();
        followerList.setAdapter(followerAdapter);
    }
    private void load(){
        MemeItUsers.getInstance().getMyFollowingList(skip, LIMIT, new OnCompleteListener<List<User>>() {
            @Override
            public void onSuccess(List<User> userResponses) {
                followerAdapter.addUsers(userResponses);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void refresh(){
        resetSkip();
        MemeItUsers.getInstance().getMyFollowingList(skip, LIMIT, new OnCompleteListener<List<User>>() {
            @Override
            public void onSuccess(List<User> userResponses) {
                swipeRefreshLayout.setRefreshing(false);
                followerAdapter.setAll(userResponses);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void resetSkip(){
        skip=0;
    }
    private void incSkip(){
        skip+=LIMIT;
    }
}
