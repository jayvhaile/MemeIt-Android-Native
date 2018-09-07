package com.memeit.backend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.memeit.backend.dataclasses.AuthInfo;
import com.memeit.backend.dataclasses.AuthToken;
import com.memeit.backend.dataclasses.MyUser;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.MyCallBack2;
import com.memeit.backend.utilis.OnCompleteListener;
import com.memeit.backend.utilis.PrefUtils;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Response;

import static com.memeit.backend.utilis.OnCompleteListener.Error.NETWORK_ERROR;
import static com.memeit.backend.utilis.OnCompleteListener.Error.OTHER_ERROR;
import static com.memeit.backend.utilis.Utils.checkAndFireError;
import static com.memeit.backend.utilis.Utils.checkAndFireSuccess;

public class MemeItAuth {

    private static MemeItAuth memeItAuth;


    public enum SignInMethod {
        USERNAME, GOOGLE, FACEBOOK;
    }

    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 6598;
    private static final String PREFERENCE_TOKEN = "__token__";
    private static final String PREFERENCE_UID = "__uid__";
    private static final String PREFERENCE_SIGNIN_METHOD = "__signin_method__";
    static final String PREFERENCE_USER_DATA_SAVED = "__user_data_saved__";
//    private Context mContext;

    private MemeItAuth() {

    }
    public static void init(){
        if (memeItAuth != null)
            throw new RuntimeException("MemeItAuth already initialized");
        memeItAuth = new MemeItAuth();
    }

    public static MemeItAuth getInstance() {
        if (memeItAuth == null)
            throw new RuntimeException("MemeItAuth must be initialized first");
        return memeItAuth;
    }

    public boolean isSignedIn(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean tokenExists = preferences.getString(PREFERENCE_TOKEN, null) != null;
        if (!tokenExists) return false;
        SignInMethod method = getSignedInMethod(context );
        if (method == null) return false;
        if (method == SignInMethod.GOOGLE)
            return GoogleSignIn.getLastSignedInAccount(context) != null;
        if (method == SignInMethod.FACEBOOK)
            return false;//todo:jv -check if facebook sign-in status
        else
            return true;
    }
    public void isUserDataSaved(Context context, final OnCompleteListener<Boolean> listener){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if ( preferences.getBoolean(PREFERENCE_USER_DATA_SAVED, false)){
            listener.onSuccess(true);
        }else{
            MemeItClient.getInstance().getInterface()
                    .isMyUserDataSaved().enqueue(new MyCallBack2<Boolean, Boolean>(listener) {
                @Override
                public void onResponse(Call <Boolean>call, Response<Boolean> response) {
                    if(response.isSuccessful()){
                        PrefUtils.get().edit()
                                .putBoolean(PREFERENCE_USER_DATA_SAVED, response.body())
                                .apply();
                        checkAndFireSuccess(listener,response.body());
                    }else{
                        checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                    }
                }
            });
        }
    }

    public SignInMethod getSignedInMethod(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String m = preferences.getString(PREFERENCE_SIGNIN_METHOD, null);
        if (m == null) return null;
        try {
            return SignInMethod.valueOf(m);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static final String TAG = "MemeItAuth";

    public void signUpWithUsername(final Context context, String username, String email, String password, final OnCompleteListener<Void> listener) {
        AuthInfo user = new AuthInfo(username,email, password);
        MemeItClient.getInstance().getInterface()
                .signUpWithEmail(user)
                .enqueue(new MyCallBack2<AuthToken, Void>(listener) {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        if (response.isSuccessful()) {
                            AuthToken token = response.body();
                            setSignedIn(context,SignInMethod.USERNAME, token);
                            checkAndFireSuccess(listener, null);
                        } else {
                            checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                        }
                    }
                });
    }


    public void signInWithUsername(final Context context, String username, String password, final OnCompleteListener<Void> listener) {
        AuthInfo user = new AuthInfo(username, password);
        MemeItClient.getInstance().getInterface()
                .loginWithEmail(user)
                .enqueue(new MyCallBack2<AuthToken, Void>(listener) {
                    @Override
                    public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                        if (response.isSuccessful()) {
                            AuthToken token = response.body();
                            setSignedIn(context,SignInMethod.USERNAME, token);
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
    public void signInWithGoogle(Fragment fragment) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        final GoogleSignInClient client = GoogleSignIn.getClient(fragment.getContext(),gso);
        Intent gc = client.getSignInIntent();
        fragment.startActivityForResult(gc, GOOGLE_SIGNIN_REQUEST_CODE);
    }

    public void handleGoogleSignInResult(final Context context, Intent data, final OnCompleteListener<Void> listener) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            final GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthInfo user = new AuthInfo(account.getEmail(), account.getId(),(Void) null);
            MemeItClient.getInstance().getInterface()
                    .loginWithGoogle(user)
                    .enqueue(new MyCallBack2<AuthToken, Void>(listener) {
                        @Override
                        public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                            if (response.isSuccessful()) {
                                AuthToken token = response.body();
                                setSignedIn(context,SignInMethod.GOOGLE, token);
                                checkAndFireSuccess(listener, null);
                            } else {
                                //todo sign out from google
                                try {
                                    checkAndFireError(listener,OTHER_ERROR.setMessage(response.message()+""+response.errorBody().string()));
                                } catch (IOException e) {
                                    checkAndFireError(listener,OTHER_ERROR.setMessage("nooo"+e.getMessage()));
                                }
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

    public void handleGoogleSignUpResult(final Context context, Intent data, final OnCompleteListener<User> listener) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            final GoogleSignInAccount account = task.getResult(ApiException.class);
            AuthInfo user = new AuthInfo(account.getEmail(), account.getId(),(Void) null);
            MemeItClient.getInstance().getInterface()
                    .signUpWithGoogle(user)
                    .enqueue(new MyCallBack2<AuthToken, User>(listener) {
                        @Override
                        public void onResponse(Call<AuthToken> call, Response<AuthToken> response) {
                            if (response.isSuccessful()) {
                                AuthToken token = response.body();
                                setSignedIn(context,SignInMethod.GOOGLE, token);
                                Uri u=account.getPhotoUrl();
                                String url=u==null?null:u.toString();
                                User user = new User(account.getDisplayName(),url);
                                checkAndFireSuccess(listener, user);
                            } else {
                                //todo sign out from google
                                try {
                                    checkAndFireError(listener,OTHER_ERROR.setMessage(response.message()+""+response.errorBody().string()));
                                } catch (IOException e) {
                                    checkAndFireError(listener,OTHER_ERROR.setMessage("nooo"+e.getMessage()));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthToken> call, Throwable throwable) {
                            //todo sign out from google
                            checkAndFireError(listener, NETWORK_ERROR.setMessage(throwable.getMessage()));
                        }
                    });

        } catch (ApiException e) {
            checkAndFireError(listener, OTHER_ERROR.setMessage("api exxx  "+e.getMessage()));
        }
    }

    public void signInWithFacebook() {
        //todo: jv -implement sign in with facebook
    }

    private void setSignedIn(Context context,SignInMethod signInMethod, AuthToken token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putString(PREFERENCE_TOKEN, token.getToken())
                .putString(PREFERENCE_SIGNIN_METHOD, signInMethod.toString())
                .apply();
        new MyUser(token.getUid()).save(preferences);
    }
    public String getToken(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREFERENCE_TOKEN,null);
    }


    public void signOut(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .remove(PREFERENCE_TOKEN)
                .remove(PREFERENCE_UID)
                .remove(PREFERENCE_SIGNIN_METHOD)
                .remove(PREFERENCE_USER_DATA_SAVED)
                .apply();
        //todo clear cache
    }
}
