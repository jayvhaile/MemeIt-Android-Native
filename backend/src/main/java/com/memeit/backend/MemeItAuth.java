package com.memeit.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.MyUser;
import com.memeit.backend.dataclasses.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.memeit.backend.OnCompleteListener.*;
import static com.memeit.backend.OnCompleteListener.Error.NETWORK_ERROR;
import static com.memeit.backend.OnCompleteListener.Error.OTHER_ERROR;
import static com.memeit.backend.Utils.checkAndFireError;
import static com.memeit.backend.Utils.checkAndFireSuccess;
import static java.lang.Error.*;

public class MemeItAuth {

    private static MemeItAuth memeItAuth;


    public enum SignInMethod {
        EMAIL, GOOGLE, FACEBOOK;
    }

    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 6598;
    private static final String PREFERENCE_TOKEN = "__token__";
    private static final String PREFERENCE_SIGNIN_METHOD = "__signin_method__";
    private static final String PREFERENCE_USER_DATA_SAVED = "__user_data_saved__";
    private Context mContext;
    private SharedPreferences preferences;

    private MemeItAuth(Context context) {
        mContext = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }
    public static void init(Context context){
        if (memeItAuth != null)
            throw new RuntimeException("MemeItAuth already initialized");
        memeItAuth = new MemeItAuth(context);
    }

    public static MemeItAuth getInstance() {
        if (memeItAuth == null)
            throw new RuntimeException("MemeItAuth must be initialized first");
        return memeItAuth;
    }

    public boolean isSignedIn() {
        boolean tokenExists = preferences.getString(PREFERENCE_TOKEN, null) != null;
        if (!tokenExists) return false;
        SignInMethod method = getSignedInMethod();
        if (method == null) return false;
        if (method == SignInMethod.GOOGLE)
            return GoogleSignIn.getLastSignedInAccount(mContext) != null;
        if (method == SignInMethod.FACEBOOK)
            return false;//todo:jv -check if facebook sign-in status
        else
            return true;
    }

    public boolean isUserDataSaved() {
        if (!isSignedIn()) return false;
        return preferences.getBoolean(PREFERENCE_USER_DATA_SAVED, false);
    }

    public SignInMethod getSignedInMethod() {
        String m = preferences.getString(PREFERENCE_SIGNIN_METHOD, null);
        if (m == null) return null;
        try {
            return SignInMethod.valueOf(m);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    public void getUser(final OnCompleteListener<User> listener) {
        if (!isSignedIn())
            checkAndFireError(listener, OTHER_ERROR.setMessage("User not signed in"));
        else {
            MemeItClient.getInstance().getInterface().getMyUser()
                    .enqueue(new MyCallBack<User, User>(listener) {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        checkAndFireSuccess(listener, response.body());
                    } else {
                        //todo handle possible server error responses
                        checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                    }
                }
            });

        }
    }

    public static final String TAG = "MemeItAuth";

    public void signUpWithEmail(String email, String password, final OnCompleteListener<Void> listener) {
        MyUser user = new MyUser(email, password);
        MemeItClient.getInstance().getInterface()
                .signUpWithEmail(user)
                .enqueue(new MyCallBack<AuthToken, Void>(listener) {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        if (response.isSuccessful()) {
                            AuthToken token = response.body();
                            setSignedIn(SignInMethod.EMAIL, token.getToken());
                            checkAndFireSuccess(listener, null);
                        } else {
                            checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                        }
                    }
                });
    }


    public void signInWithEmail(String email, String password, final OnCompleteListener<Void> listener) {
        MyUser user = new MyUser(email, password);
        MemeItClient.getInstance().getInterface()
                .loginWithEmail(user)
                .enqueue(new MyCallBack<AuthToken, Void>(listener) {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        if (response.isSuccessful()) {
                            AuthToken token = response.body();
                            setSignedIn(SignInMethod.EMAIL, token.getToken());
                            checkAndFireSuccess(listener, null);
                        } else {
                            checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                        }
                    }
                });
    }


    public void signInWithGoogle(Activity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        final GoogleSignInClient client = GoogleSignIn.getClient(activity, gso);
        Intent gc = client.getSignInIntent();
        activity.startActivityForResult(gc, GOOGLE_SIGNIN_REQUEST_CODE);

    }

    public void handleGoogleSignInResult(Intent data, final OnCompleteListener<Void> listener) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            final GoogleSignInAccount account = task.getResult(ApiException.class);
            MyUser user = new MyUser(account.getEmail(), account.getId());
            MemeItClient.getInstance().getInterface()
                    .loginWithGoogle(user)
                    .enqueue(new MyCallBack<AuthToken, Void>(listener) {
                        @Override
                        public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                            if (response.isSuccessful()) {
                                AuthToken token = response.body();
                                setSignedIn(SignInMethod.GOOGLE, token.getToken());
                                checkAndFireSuccess(listener, null);
                            } else {
                                //todo sign out from google
                                checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthToken> call, Throwable throwable) {
                            //todo sign out from google
                            checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
                        }
                    });

        } catch (ApiException e) {
            checkAndFireError(listener, OTHER_ERROR.setMessage(e.getMessage()));
        }
    }

    public void handleGoogleSignUpResult(Intent data, final OnCompleteListener<User> listener) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            final GoogleSignInAccount account = task.getResult(ApiException.class);
            MyUser user = new MyUser(account.getEmail(), account.getId(),null);
            MemeItClient.getInstance().getInterface()
                    .signUpWithGoogle(user)
                    .enqueue(new MyCallBack<AuthToken, User>(listener) {
                        @Override
                        public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                            if (response.isSuccessful()) {
                                AuthToken token = response.body();
                                setSignedIn(SignInMethod.GOOGLE, token.getToken());
                                Uri u=account.getPhotoUrl();
                                String url=u==null?null:u.toString();
                                User user = new User(account.getDisplayName(),url);
                                checkAndFireSuccess(listener, user);
                            } else {
                                //todo sign out from google
                                try {
                                    Toast.makeText(mContext, "--------- "+response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthToken> call, Throwable throwable) {
                            //todo sign out from google
                            checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
                        }
                    });

        } catch (ApiException e) {
            Toast.makeText(mContext, "649897 "+e.getMessage(), Toast.LENGTH_SHORT).show();
            checkAndFireError(listener, OTHER_ERROR.setMessage("api exxx  "+e.getMessage()));
        }
    }


    public void uploadUserData(User user, OnCompleteListener<Void> listener) {
        //todo:jv add upload user data listner
        //todo:jv upload userImage
        MemeItClient.getInstance().getInterface()
                .uploadUserData(user)
                .enqueue(new MyCallBack<User, Void>(listener) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User user = response.body();
                            Log.d(TAG, "onResponse: " + user);
                            preferences.edit()
                                    .putBoolean(PREFERENCE_USER_DATA_SAVED, true)
                                    .apply();
                        } else {
                            Toast.makeText(mContext, "error uploading " + response.message(), Toast.LENGTH_SHORT).show();
                        }
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
    public String getToken(){
        return preferences.getString(PREFERENCE_TOKEN,null);
    }

    public void SignOut() {
        preferences.edit()
                .remove(PREFERENCE_TOKEN)
                .remove(PREFERENCE_SIGNIN_METHOD)
                .remove(PREFERENCE_USER_DATA_SAVED)
                .apply();
    }


}
