package com.innov8.memeit.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.innov8.memeit.Fragments.ProfileFragment;
import com.innov8.memeit.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String uid = getIntent().getStringExtra("uid");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_frag_holder, ProfileFragment.newInstance(uid))
                .commit();
    }
}
