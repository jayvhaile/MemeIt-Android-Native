package com.innov8.memeit.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.innov8.memeit.Activities.AuthActivity;
import com.innov8.memeit.Activities.MainActivity;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.utilis.OnCompleteListener;

public class LoginFragment extends AuthFragment implements View.OnClickListener {
    public LoginFragment() {
    }

    OnCompleteListener<Void> signInCompletedListener;
    EditText usernameV;
    EditText passwordV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login2, container, false);


        CustomMethods.makeEditTextsAvenir(getActivity(), view, R.id.login_password, R.id.login_username);

        usernameV = view.findViewById(R.id.login_username);
        passwordV = view.findViewById(R.id.login_password);
        actionButton = view.findViewById(R.id.login_btn);
        actionButton.setOnClickListener(this);
        view.findViewById(R.id.login_google).setOnClickListener(this);
        view.findViewById(R.id.login_facebook).setOnClickListener(this);
        view.findViewById(R.id.rel).setOnClickListener(this);
        /* This sets the bottom padding that makes the views not go underneath the navigation bar */
        view.findViewById(R.id.rel).setPadding(
                0,
                (int) (16 * getResources().getDisplayMetrics().density + 0.5f),
                0,
                (int) (10 * getResources().getDisplayMetrics().density + 0.5f) + AuthActivity.getSoftButtonsBarHeight(getActivity()));


        signInCompletedListener = new OnCompleteListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setLoading(false);
                startActivity(new Intent(getContext(), MainActivity.class));

            }

            @Override
            public void onFailure(final Error error) {
                setLoading(false);
                getAuthActivity().showError(error.getDefaultMessage() + "\n" + error.getMessage());
            }
        };

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE)
            MemeItAuth.getInstance().handleGoogleSignInResult(getContext(),data, signInCompletedListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                loginWithUserName();
                break;
            case R.id.login_google:
                MemeItAuth.getInstance().signInWithGoogle(getActivity());
                break;
            case R.id.login_facebook:
                //todo facebook signin
                break;
            case R.id.rel:
                getAuthActivity().setCurrentFragment(AuthActivity.FRAGMENT_SIGNUP);
                break;

        }
    }

    private void loginWithUserName() {
        final String username = usernameV.getText().toString();
        final String password = passwordV.getText().toString();
        if (!CustomMethods.isUsernameValid(username)) {
            getAuthActivity().showError("Username should at least be 5 in length!");
        } else if (password.length() <= 1) {//todo change back to 8
            getAuthActivity().showError("Password should at least be 8 in length!");
        } else {
            setLoading(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MemeItAuth.getInstance().signInWithUsername(getContext(),username, password, signInCompletedListener);

                }
            }, 2000);

        }
    }


}
