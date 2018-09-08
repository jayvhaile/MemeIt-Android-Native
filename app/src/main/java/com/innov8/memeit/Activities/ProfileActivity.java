package com.innov8.memeit.Activities;

import android.os.Bundle;

import com.innov8.memeit.Fragments.ProfileFragment;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.User;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String uid = getIntent().getStringExtra("uid");
        User user = getIntent().getParcelableExtra("user");

        ProfileFragment pf;
        if(user!=null){
            pf=ProfileFragment.newInstance(user);
        }else{
            pf=ProfileFragment.newInstance(uid);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_frag_holder,pf)
                .commit();
    }
}
