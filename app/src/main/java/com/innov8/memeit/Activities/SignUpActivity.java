package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.innov8.memeit.CustomClasses.CustomDialog;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;
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
    @BindView(R.id.sign_up)
    View sign_up;
    @BindView(R.id.to_sign_in)
    View to_sign_in;
    CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        dialog = new CustomDialog(this,"Signing up...");

        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");

        emailV.setTypeface(avenir);
        confrim_password.setTypeface(avenir);
        passwordV.setTypeface(avenir);



        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailV.getText().toString();
                String password=passwordV.getText().toString();
                boolean emailIsValid = CustomMethods.isEmailValid(email);
                boolean passwordIsValid = password.length()>7;
                dialog.show();

                if(confrim_password.getText().toString().equals(password)){
                if(emailIsValid&&passwordIsValid)
                MemeItAuth.getInstance().signUpWithEmail(email, password, new OnCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.hide();
                        startActivity(new Intent(SignUpActivity.this,SetupProfileActivity.class));
                    }

                    @Override
                    public void onFailure(Error error) {
                        dialog.hide();
                        Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                else{
                    String response = "";
                    if(!emailIsValid&&passwordIsValid) response = "Please enter a valid email.";
                    else if(emailIsValid&&!passwordIsValid) response = "Your password must at least be 8 characters long";
                    else response = "Please enter a valid email and enter a password that is at least 8 characters long.";
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                }}
                else Toast.makeText(getApplicationContext(),"Please make sure your passwords match.",Toast.LENGTH_LONG).show();
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
                    Intent intent=new Intent(SignUpActivity.this,SetupProfileActivity.class);
                    intent.putExtra(SetupProfileActivity.PARAM_NAME,user.getName());
                    intent.putExtra(SetupProfileActivity.PARAM_IMAGE_URL,user.getImageUrl());
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
