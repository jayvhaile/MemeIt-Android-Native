package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity{

    Typeface avenir;
    @BindView(R.id.email)
    EditText emailV;
    @BindView(R.id.password)
    EditText passwordV;
    @BindView(R.id.google_sign_in)
    SignInButton google_sign_in;
    @BindView(R.id.sign_in)
    View sign_in;

    OnCompleteListener<Void> signInCompletedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");
        emailV.setTypeface(avenir);
        passwordV.setTypeface(avenir);

       signInCompletedListener=new OnCompleteListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
             //todo:biruk,sign in success exit and go back to app

               startActivity(new Intent(SignInActivity.this,MainActivity.class));
           }

           @Override
           public void onFailure(Error error) {
            //todo:biruk sign in error, show the error to the user

               Toast.makeText(SignInActivity.this, error.getDefaultMessage()+"\n"+error.getMessage(), Toast.LENGTH_SHORT).show();
           }
       };
        //this is for google sign in
        google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemeItAuth.getInstance().signInWithGoogle(SignInActivity.this);
            }
        });

        //this is for email sign in
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo:biruk verify email validity(has correct email structure) , and show the necessary error if not
                String email=emailV.getText().toString();
                String password=passwordV.getText().toString();
                if(true/*email is valid */)
                    MemeItAuth.getInstance().signInWithEmail(email, password, signInCompletedListener);
                else{
                    //todo:biruk email invalid, show to user
                }
            }
        });

    }
    //this is for google sign in
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE) {
            //this is for google sign in
           MemeItAuth.getInstance().handleGoogleSignInResult(data,signInCompletedListener);
        }
    }
}
