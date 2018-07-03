package com.innov8.memeit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.innov8.memeit.Adapters.MemeAdapter;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class MemeListFragment extends Fragment {
    private static final String TAG="MemeListFragment";
    private static final int LIMIT=20;

    private RecyclerView memeList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MemeAdapter memeAdapter;

    private MemeLoader emptyLoader =new EmptyLoader();
    private MemeLoader memeLoader;



    private int skip;


    public static MemeListFragment withLoader(MemeLoader loader){
        MemeListFragment fragment=new MemeListFragment();
        fragment.setMemeLoader(loader,false);
        return fragment;
    }
    public MemeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memeAdapter=new MemeAdapter(getContext());
    }

    public void setMemeLoader(MemeLoader loader,boolean reload){
        this.memeLoader=loader;
        if(reload)refresh();
    }

    public MemeLoader getMemeLoader() {
        return memeLoader==null? emptyLoader :memeLoader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_meme_list, container, false);
        swipeRefreshLayout=view.findViewById(R.id.swipe_to_refresh);
        memeList=view.findViewById(R.id.meme_recycler_view);
        setupUI();
        load();
        return view;
    }



    private void setupUI(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               refresh();
            }
        });
        memeList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        DefaultItemAnimator animator=new DefaultItemAnimator();
        memeList.setItemAnimator(animator);
        memeList.setAdapter(memeAdapter);

        memeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //todo load more at the end
            }
        });
    }
    private void load(){
        getMemeLoader().load(skip, LIMIT, new OnCompleteListener<List<Meme>>() {
            @Override
            public void onSuccess(List<Meme> memeResponses) {
                memeAdapter.addAll(memeResponses);
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
        getMemeLoader().load(skip, LIMIT, new OnCompleteListener<List<Meme>>() {
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
        });

    }
    private void resetSkip(){
        skip=0;
    }
    private void incSkip(){
        skip+=LIMIT;
    }

    public interface MemeLoader{
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener);
    }
    public static class HomeLoader implements MemeLoader{
        @Override
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener) {
            //todo getHomeMemesforguest if user is not signed in
            MemeItMemes.getInstance().getHomeMemes(skip,limit,listener);
        }
    }

    public static class TrendingLoader implements MemeLoader{
        @Override
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener) {
            MemeItMemes.getInstance().getTrendingMemes(skip,limit,listener);
        }
    }
    public static class FavoritesLoader implements MemeLoader{
        @Override
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener) {
            MemeItMemes.getInstance().getFavouriteMemes(skip,limit,listener);
        }
    }
    public static class EmptyLoader implements MemeLoader{
        @Override
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener) {
            listener.onSuccess(new ArrayList<Meme>());
        }
    }
}
