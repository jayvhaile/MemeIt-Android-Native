package com.memeit.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.memeit.backend.dataclasses.MyUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemeItAuth {

    private static MemeItAuth memeItAuth;

    public void setSignInListener(SignInListener signInListener) {
        this.signInListener = signInListener;
    }

    public enum SignInMethod {
        EMAIL, GOOGLE, FACEBOOK;
    }

    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 6598;
    private static final String PREFERENCE_TOKEN = "__token__";
    private static final String PREFERENCE_SIGNIN_METHOD = "__signin_method__";
    private static final String PREFERENCE_USER_DATA_SAVED = "__user_data_saved__";
    private Context mContext;
    private SharedPreferences preferences;
    private SignInListener signInListener;
    private MemeItAuth(Context context) {
        mContext = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static MemeItAuth getInstance(Context context) {
        if (memeItAuth == null)
            memeItAuth = new MemeItAuth(context);
        return memeItAuth;
    }

    public boolean isSignedIn() {
        boolean tokenExists=preferences.getString(PREFERENCE_TOKEN, null) != null;
        if (!tokenExists)return false;
        SignInMethod method=getSignedInMethod();
        if (method==null)return false;
        if (method==SignInMethod.GOOGLE)
            return GoogleSignIn.getLastSignedInAccount(mContext)!=null;
        if (method==SignInMethod.FACEBOOK)
            return false;//todo:jv -check if facebook sign-in status
        else
            return true;
    }

    public SignInMethod getSignedInMethod() {
        if (!isSignedIn()) return null;
        String m = preferences.getString(PREFERENCE_SIGNIN_METHOD, null);
        if (m == null) return null;
        try {
            return SignInMethod.valueOf(m);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isUserDataSaved() {
        if (!isSignedIn()) return false;
        return preferences.getBoolean(PREFERENCE_USER_DATA_SAVED, false);
    }

    public MyUser getUser() {
        if (!isSignedIn()) return null;
        else {
            //todo get user detail
            return null;
        }
    }

    public void signInWithEmail(String email, String password) {

    }

    public void signInWithGoogle(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("")
                .requestProfile()
                .build();
        final GoogleSignInClient client = GoogleSignIn.getClient(activity, gso);
        Intent gc = client.getSignInIntent();
        activity.startActivityForResult(gc, GOOGLE_SIGNIN_REQUEST_CODE);

    }

    public void handleGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            setSignedIn(SignInMethod.GOOGLE,account.getIdToken());
            fireSignInSuccessfull();
        } catch (ApiException e) {
            fireSignInFailed(0);
        }
    }


    public void uploadUserData(MyUser user) {
        //todo:jv add upload user data listner
        //todo:jv upload userImage
        MemeItClient.getInstance().getInterface().uploadUserData(user).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                if (response.isSuccessful()) {
                   preferences.edit()
                            .putBoolean(PREFERENCE_USER_DATA_SAVED, true)
                            .apply();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {

            }
        });
    }

    public void signInWithFacebook() {
        //todo: jv -implement sign in with facebook
    }

    private void setSignedIn(SignInMethod signInMethod, String token) {
        preferences.edit()
                .putString(PREFERENCE_TOKEN, token)
                .putString(PREFERENCE_SIGNIN_METHOD, signInMethod.toString())
                .apply();
    }

    public void SignOut() {
        preferences.edit()
                .remove(PREFERENCE_TOKEN)
                .remove(PREFERENCE_SIGNIN_METHOD)
                .remove(PREFERENCE_USER_DATA_SAVED)
                .apply();
    }


    private void fireSignInSuccessfull(){
        if (signInListener !=null) signInListener.onSignInSuccessFull();
    }
    private void fireSignInFailed(int code){
        if (signInListener !=null) signInListener.onSignInFailed(code);
    }

    public static interface SignInListener {
        public void onSignInSuccessFull();

        public void onSignInFailed(int code);
    }
    public static interface uploadUserDataListener {
        public void onSignInSuccessFull();
        public void onSignInFailed(int code);
    }
}
