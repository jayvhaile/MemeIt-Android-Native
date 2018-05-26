package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.OnCompleteListener;
import com.memeit.backend.dataclasses.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    Typeface avenir;
    @BindView(R.id.name)
    EditText emailV;
    @BindView(R.id.password)
    EditText passwordV;
    @BindView(R.id.confrim_password)
    EditText confrim_password;
    @BindView(R.id.google_sign_in)
    SignInButton google_sign_in;
    @BindView(R.id.sign_up)
    View sign_up;
    @BindView(R.id.to_sign_in)
    View to_sign_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");

        emailV.setTypeface(avenir);
        passwordV.setTypeface(avenir);
        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItAuth.getInstance().signInWithGoogle(SignUpActivity.this);
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailV.getText().toString();
                String password=passwordV.getText().toString();
                MemeItAuth.getInstance().signUpWithEmail(email, password, new OnCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(SignUpActivity.this,SignUpDetailsActivity.class));
                    }

                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        to_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE) {
            //this is for google sign in
            Toast.makeText(this, "come backed", Toast.LENGTH_SHORT).show();
            MemeItAuth.getInstance().handleGoogleSignUpResult(data, new OnCompleteListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Intent intent=new Intent(SignUpActivity.this,SignUpDetailsActivity.class);
                    intent.putExtra(SignUpDetailsActivity.PARAM_NAME,user.getName());
                    intent.putExtra(SignUpDetailsActivity.PARAM_IMAGE_URL,user.getImageUrl());
                    startActivity(intent);
                }

                @Override
                public void onFailure(Error error) {
                    Toast.makeText(SignUpActivity.this,"noo  "+ error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
