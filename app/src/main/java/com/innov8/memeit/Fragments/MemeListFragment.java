package com.innov8.memeit.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.innov8.memegenerator.utils.UtilKt;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.CustomClasses.EmptyLoader;
import com.innov8.memeit.CustomClasses.MemeLoader;
import com.innov8.memeit.CustomClasses.SearchLoader;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.HomeElement;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MemeListFragment extends Fragment {
    private static final String TAG = "MemeListFragment";
    private static final int LIMIT = 20;

    private RecyclerView memeList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MemeAdapter memeAdapter;

    private MemeLoader emptyLoader = new EmptyLoader();
    private MemeLoader memeLoader;


    private int skip;

    private byte memeAdapterType;
    private byte memeLoaderType;
    private OnCompleteListener<List<Meme>> listener;
    private OnCompleteListener<List<Meme>> refreshListener;

    public static MemeListFragment newInstance(byte memeAdapterType, byte memeLoaderType) {
        MemeListFragment fragment = new MemeListFragment();
        Bundle arg = new Bundle();
        arg.putByte("adapter_type", memeAdapterType);
        arg.putByte("loader_type", memeLoaderType);
        fragment.setArguments(arg);
        return fragment;
    }

    public static MemeListFragment newInstanceForUserPosts(String userID) {
        MemeListFragment fragment = new MemeListFragment();
        Bundle arg = new Bundle();
        arg.putByte("adapter_type", MemeAdapter.GRID_ADAPTER);
        arg.putByte("loader_type", MemeLoader.USER_POST_MEME_LOADER);
        arg.putString("uid", userID);
        fragment.setArguments(arg);
        return fragment;
    }

    public MemeListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null)
            throw new NullPointerException("Argument should not be null");
        memeAdapterType = getArguments().getByte("adapter_type", MemeAdapter.LIST_ADAPTER);
        memeLoaderType = getArguments().getByte("loader_type", MemeLoader.EMPTY_LOADER);
        listener = new OnCompleteListener<List<Meme>>() {
            @Override
            public void onSuccess(List<Meme> memeResponses) {
                memeAdapter.setLoading(false);
                memeAdapter.addAll(memeResponses);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                memeAdapter.setLoading(false);
            }
        };
        refreshListener = new OnCompleteListener<List<Meme>>() {
            @Override
            public void onSuccess(List<Meme> memeResponses) {
                swipeRefreshLayout.setRefreshing(false);
                memeAdapter.setAll(memeResponses);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    private void initLoader() {
        if (searchMode && searchLoader == null) {
            searchLoader = new SearchLoader();
        }
        if (memeLoader == null)
            if (memeLoaderType == MemeLoader.USER_POST_MEME_LOADER) {
                String uid = getArguments().getString("uid");
                memeLoader = MemeLoader.Companion.create(memeLoaderType, getContext(), uid);
            } else {
                memeLoader = MemeLoader.Companion.create(memeLoaderType, getContext(), null);
            }
    }


    private void initAdapter() {
        if (memeAdapter == null)
            memeAdapter = MemeAdapter.Companion.create(memeAdapterType, getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meme_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initAdapter();
        initLoader();
        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
        memeList = view.findViewById(R.id.meme_recycler_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(false);
            }
        });
        memeList.setLayoutManager(memeAdapter.createLayoutManager());
        DefaultItemAnimator animator = new DefaultItemAnimator();
        memeList.setItemAnimator(animator);
        memeList.setAdapter(memeAdapter);

        memeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(disableScrollListener)return;
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager llm = memeList.getLayoutManager();
                int visibleItemCount = llm.getChildCount();
                int totalItemCount = llm.getItemCount();
                int fp = ((LinearLayoutManager) llm).findFirstVisibleItemPosition();
                if (!memeAdapter.getLoading()) {//todo jv add isLastPage checker
                    if (visibleItemCount + fp >= totalItemCount && fp >= 0) {
                        UtilKt.log("SCROLL LOAD");
                        load();
                    }
                }
                //todo load more at the end
            }
        });
        load();
    }


    private boolean refresh;


    private void resetSkip() {
        skip = 0;
    }

    private void incSkip() {
        skip += LIMIT;
    }


    SearchLoader searchLoader;
    List<HomeElement> tempList;
    int tempSkip;

    boolean searchMode;

    String searchText;
    String[] searchTags;
    boolean disableScrollListener;
    public void setSearchMode(boolean searchMode) {
        disableScrollListener =true;
        if (!this.searchMode&&searchMode) {
            searchLoader=new SearchLoader();
            tempList = new ArrayList<>(memeAdapter.getItems());
            memeAdapter.clear();
            UtilKt.log("searchmode true");
            tempSkip = skip;
            skip = 0;
        } else if(this.searchMode&&!searchMode){
            searchLoader = null;
            searchText = null;
            searchTags = null;
            UtilKt.log("searchmode false");
            memeAdapter.setAll(new ArrayList<>(tempList));
            skip = tempSkip;
            tempSkip=0;
            tempList=null;
        }
        enableScrollListenerLater();
        this.searchMode = searchMode;
    }

    private void enableScrollListenerLater() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                disableScrollListener =false;
            }
        },100);
    }

    private void load() {
        disableScrollListener=true;
        memeAdapter.setLoading(true);
        refresh = false;
        if (searchMode) {
            searchLoader.setListener(listener);
            searchLoader.search(searchText, searchTags, skip, LIMIT);
        }else{
            memeLoader.setListener(listener);
            memeLoader.load(skip, LIMIT);
        }
        enableScrollListenerLater();


    }

    private void refresh(boolean setLoading) {
        disableScrollListener=true;
        resetSkip();
        memeAdapter.setLoading(setLoading);
        refresh = true;
        if(searchMode){
            searchLoader.reset();
            searchLoader.setListener(refreshListener);
            searchLoader.search(searchText, searchTags, skip, LIMIT);
        }else{
            memeLoader.reset();
            memeLoader.setListener(refreshListener);
            memeLoader.load(skip, LIMIT);
        }
        enableScrollListenerLater();
    }

    public void search(String s, String tags[]) {
        UtilKt.log("SEARCH");
        if (searchMode) {
            searchText = s;
            searchTags = tags;
            refresh(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(searchMode)
            searchLoader.setListener(refresh ? refreshListener : listener);
        else
            memeLoader.setListener(refresh ? refreshListener : listener);
    }

    @Override
    public void onStop() {
        if (memeLoader!= null) memeLoader.setListener(null);
        if (searchMode&&searchLoader!= null) searchLoader.setListener(null);
        super.onStop();
    }
}
