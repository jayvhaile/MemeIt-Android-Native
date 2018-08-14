package com.innov8.memeit.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.innov8.memeit.CustomClasses.FontTextView;
import com.innov8.memeit.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileNewActivity extends AppCompatActivity {

    @BindView(R.id.follow_button) FontTextView followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        ButterKnife.bind(this);

    }

}
