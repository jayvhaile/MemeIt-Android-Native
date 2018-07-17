package com.innov8.memeit.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.innov8.memeit.R;
import com.innov8.memeit.loading_button_lib.customViews.CircularProgressButton;
import com.innov8.memeit.loading_button_lib.interfaces.OnAnimationEndListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class HomeFragment extends Fragment implements MaterialSearchView.OnQueryTextListener {

    CircularProgressButton btn;

    public HomeFragment() {
        // Required empty public constructor
    }

    MaterialSearchView searchView;
    boolean followed;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        searchView = view.findViewById(R.id.search_view);

        btn = view.findViewById(R.id.follow);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.startAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        followed=!followed;
                        btn.revertAnimation(new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                Toast.makeText(getContext(), "end", Toast.LENGTH_SHORT).show();
                                btn.setText(followed?"Unfollow":"Follow");
                            }
                        });
                    }
                },4000);
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_frag_menu, menu);
        searchView.setMenuItem(menu.findItem(R.id.menu_home_frag_search));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onDestroy() {
        btn.dispose();
        super.onDestroy();
    }
}
