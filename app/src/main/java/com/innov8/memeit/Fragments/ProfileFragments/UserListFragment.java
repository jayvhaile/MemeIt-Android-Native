package com.innov8.memeit.Fragments.ProfileFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.innov8.memeit.Adapters.UserListAdapter;
import com.innov8.memeit.CustomClasses.MemeLoader;
import com.innov8.memeit.CustomClasses.UserListLoader;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserListFragment extends Fragment {
    private static final int LIMIT=50;
    private RecyclerView followerList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private UserListAdapter followerAdapter;
    public UserListFragment() {
        // Required empty public constructor
    }

    private int skip;
    private byte userLoaderType;
    private UserListLoader userListLoader;
    private OnCompleteListener<List<User>> listener;
    private OnCompleteListener<List<User>> refreshListener;

    public static UserListFragment newInstance(byte userLoaderType,String uid) {
        UserListFragment fragment= new UserListFragment();
        Bundle arg = new Bundle();
        arg.putString("uid",uid);
        arg.putByte("loader_type", userLoaderType);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null)
            throw new NullPointerException("Argument should not be null");

        userLoaderType = getArguments().getByte("loader_type", MemeLoader.EMPTY_LOADER);
        String uid=getArguments().getString("uid");
        userListLoader=UserListLoader.Companion.create(userLoaderType,uid);
        listener =new OnCompleteListener<List<User>>() {
            @Override
            public void onSuccess(List<User> userResponses) {
                followerAdapter.addAll(userResponses);
                incSkip();
            }

            @Override
            public void onFailure(OnCompleteListener.Error error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        refreshListener =new OnCompleteListener<List<User>>() {
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
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout=view.findViewById(R.id.swipe_to_refresh);
        followerList=view.findViewById(R.id.followers_recycler_view);
        followerAdapter = new UserListAdapter(getContext());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        followerList.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        DefaultItemAnimator animator=new DefaultItemAnimator();
        followerList.setItemAnimator(animator);
        followerList.setAdapter(followerAdapter);
        load();
    }
    private boolean refresh;
    private void load(){
        refresh=false;
        userListLoader.setListener(listener);
        userListLoader.load(skip,LIMIT);
    }
    private void refresh(){
        resetSkip();
        refresh=true;
        userListLoader.setListener(refreshListener);
        userListLoader.load(skip,LIMIT);
    }
    @Override
    public void onStart() {
        super.onStart();
        userListLoader.setListener(refresh?refreshListener:listener);
    }

    @Override
    public void onStop() {
        userListLoader.setListener(null);
        super.onStop();
    }

    private void resetSkip(){
        skip=0;
    }
    private void incSkip(){
        skip+=LIMIT;
    }

}
