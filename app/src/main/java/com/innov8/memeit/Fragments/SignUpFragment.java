package com.innov8.memeit.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.innov8.memeit.Activities.AuthActivity;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

public class SignUpFragment extends AuthFragment implements View.OnClickListener {
    public SignUpFragment() {
    }

    EditText userNameV;
    EditText emailV;
    EditText passwordV;


    OnCompleteListener<Void> onCompleteListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup2, container, false);

        userNameV = view.findViewById(R.id.signup_username);
        emailV = view.findViewById(R.id.signup_email);
        passwordV = view.findViewById(R.id.signup_password);
        actionButton = view.findViewById(R.id.signup_btn);

        CustomMethods.makeEditTextsAvenir(getActivity(), view, R.id.signup_password, R.id.signup_username, R.id.signup_confirm);

        /* This sets the bottom padding that makes the views not go underneath the navigation bar */
        view.findViewById(R.id.rel).setPadding(
                0,
                (int) (16 * getResources().getDisplayMetrics().density + 0.5f),
                0,
                (int) (10 * getResources().getDisplayMetrics().density + 0.5f) + AuthActivity.getSoftButtonsBarHeight(getActivity()));
        view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        actionButton.setOnClickListener(this);
        view.findViewById(R.id.signup_google).setOnClickListener(this);
        view.findViewById(R.id.signup_facebook).setOnClickListener(this);
        view.findViewById(R.id.rel).setOnClickListener(this);
        onCompleteListener = new OnCompleteListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setLoading(false);
                AuthActivity a = ((AuthActivity) SignUpFragment.this.getActivity());
                a.setCurrentFragment(AuthActivity.FRAGMENT_SETUP);
            }

            @Override
            public void onFailure(Error error) {
                //todo if it is username conflict suggest a new one
                setLoading(false);
                getAuthActivity().showError(error.getMessage());
            }
        };
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE) {
            MemeItAuth.getInstance().handleGoogleSignUpResult(data, new OnCompleteListener<User>() {
                @Override
                public void onSuccess(User user) {
                   getAuthActivity().setupFragment.fromGoogle(user.getName(), user.getImageUrl());
                   getAuthActivity().setCurrentFragment(AuthActivity.FRAGMENT_SETUP);
                }
                @Override
                public void onFailure(Error error) {
                   getAuthActivity().showError(error.getMessage());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signup_btn:
                signupWithUsername();
                break;
            case R.id.signup_google:
                MemeItAuth.getInstance().signInWithGoogle(getActivity());
                break;
            case R.id.signup_facebook:
                //todo facebook signup
                break;
            case R.id.rel:
                getAuthActivity().setCurrentFragment(AuthActivity.FRAGMENT_LOGIN);
                break;
        }
    }

    private void signupWithUsername() {
        String username = userNameV.getText().toString();
        String email = emailV.getText().toString();
        String password = passwordV.getText().toString();
        if (!CustomMethods.isUsernameValid(username)){
            getAuthActivity().showError("Username should at least be 5 in length!");
        }else if(!CustomMethods.isEmailValid(email)){//todo change back to 8
            getAuthActivity().showError("Invalid Email!");
        }else if(password.length() <= 1){//todo change back to 8
            getAuthActivity().showError("Password should at least be 8 in length!");
        }else{
            setLoading(true);
            MemeItAuth.getInstance().signUpWithUsername(username,email, password, onCompleteListener);
        }
    }
}
