package com.innov8.memeit.Activities;

import android.os.Bundle;

import com.innov8.memeit.CustomClasses.FontTextView;
import com.innov8.memeit.R;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileNewActivity extends AppCompatActivity {

    FontTextView followButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        followButton=findViewById(R.id.follow_button);
    }

}
