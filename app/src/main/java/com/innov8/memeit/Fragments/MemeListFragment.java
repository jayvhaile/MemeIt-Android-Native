package com.innov8.memeit.Fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

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
    private static final int LIMIT=50;

    private RecyclerView memeList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MemeAdapter memeAdapter;

    private MemeLoader emptyLoader =new EmptyLoader();
    private MemeLoader memeLoader;



    private int skip;


    public static MemeListFragment newInstance(MemeLoader loader,MemeAdapter adapter){
        MemeListFragment fragment=new MemeListFragment();
        fragment.setMemeLoader(loader,false);
        fragment.setMemeAdapter(adapter);
        return fragment;
    }
    public MemeListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setMemeLoader(MemeLoader loader,boolean reload){
        this.memeLoader=loader;
        if(reload)refresh();
    }

    public void setMemeAdapter(MemeAdapter memeAdapter) {
        this.memeAdapter = memeAdapter;
    }

    public MemeLoader getMemeLoader() {
        return memeLoader==null? emptyLoader :memeLoader;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(memeAdapter==null) throw new NullPointerException("MemeAdapter Should be provided");
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
        memeList.setLayoutManager(memeAdapter.createlayoutManager());
        DefaultItemAnimator animator=new DefaultItemAnimator();
        memeList.setItemAnimator(animator);
        memeList.setAdapter(memeAdapter);

        memeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               /* super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount=llm.getChildCount();
                int totalItemCount=llm.getItemCount();
                int fp=llm.findFirstVisibleItemPosition();

                if(!memeAdapter.isLoading()){//todo jv add isLastPage checker
                    if(visibleItemCount+fp>=totalItemCount&&fp>=0){
                        load();
                    }
                }
                //todo load more at the end*/
            }
        });
    }


    private void load(){
        memeAdapter.setLoading(true);
        getMemeLoader().load(skip, LIMIT, new OnCompleteListener<List<Meme>>() {
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
    public static class MyMemesLoader implements MemeLoader{
        @Override
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener) {
            MemeItMemes.getInstance().getMyMemes(skip,limit,listener);
        }
    }
    public static class EmptyLoader implements MemeLoader{
        @Override
        public void load(int skip,int limit,OnCompleteListener<List<Meme>> listener) {
            listener.onSuccess(new ArrayList<Meme>());
        }
    }
}
