package com.innov8.memegenerator;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Haile on 7/31/2017.
 */

public class ParentActivity extends AppCompatActivity {
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void setupToolbarAsDisplayHome(String title) {
        this.mToolbar = findViewById(R.id.toolbar);
        this.mToolbar.setTitle(title);
        this.setSupportActionBar(this.mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    protected void setupToolbar(String title) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        mToolbar.setTitleMarginStart(0);
        setSupportActionBar(mToolbar);
    }
    protected void setupToolbar(int res) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getResources().getString(res));
        setSupportActionBar(mToolbar);
    }Drawable d;


}
