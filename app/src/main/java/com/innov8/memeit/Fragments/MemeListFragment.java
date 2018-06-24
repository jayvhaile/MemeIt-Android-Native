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
import com.memeit.backend.dataclasses.MemeResponse;
import com.memeit.backend.utilis.OnCompleteListener;
import java.util.List;

public class MemeListFragment extends Fragment {
    private static final String TAG="MemeListFragment";
    private static final int LIMIT=20;
    private  MemeItMemes memeAPI=MemeItMemes.getInstance();

    private RecyclerView memeList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MemeAdapter memeAdapter;


    private int skip;
    public MemeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memeAdapter=new MemeAdapter(getContext());
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
                resetSkip();
               memeAPI.getTrendingMemes(skip, LIMIT, new OnCompleteListener<List<MemeResponse>>() {
                    @Override
                    public void onSuccess(List<MemeResponse> memeResponses) {
                        swipeRefreshLayout.setRefreshing(false);
                        memeAdapter.setAll(memeResponses);
                        incSkip();
                    }

                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: "+error.getMessage());

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
        MemeItMemes.getInstance().getTrendingMemes(skip, LIMIT, new OnCompleteListener<List<MemeResponse>>() {
            @Override
            public void onSuccess(List<MemeResponse> memeResponses) {
                memeAdapter.addAll(memeResponses);
                incSkip();
            }
            @Override
            public void onFailure(Error error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
