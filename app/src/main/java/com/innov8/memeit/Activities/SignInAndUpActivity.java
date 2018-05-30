package com.innov8.memeit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.innov8.memeit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInAndUpActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.sign_up)
    TextView signup;
    @BindView(R.id.sign_in)
    TextView signin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_and_up);
        ButterKnife.bind(this);

        signup.setOnClickListener(this);
        signin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in:
                startActivity(new Intent(this,SignInActivity.class));
                finish();
                return;
            case R.id.sign_up:
                startActivity(new Intent(this,SignUpActivity.class));
                finish();
                return;
        }
    }
}
