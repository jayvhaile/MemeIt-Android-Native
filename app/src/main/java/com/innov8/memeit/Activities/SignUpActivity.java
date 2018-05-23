package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements MemeItAuth.SignInListener {

    Typeface avenir;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.sign_in_button)
    SignInButton signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");

        name.setTypeface(avenir);
        password.setTypeface(avenir);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItAuth.getInstance(SignUpActivity.this).signInWithGoogle(SignUpActivity.this);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE) {
           MemeItAuth.getInstance(this).handleGoogleSignInResult(data);
        }
    }

    @Override
    public void onSignInSuccessFull() {
        //todo:biruk -get user name and photo and  upload it to the server
    }

    @Override
    public void onSignInFailed(int code) {
        //todo:biruk -show the neccessary error
    }
}
