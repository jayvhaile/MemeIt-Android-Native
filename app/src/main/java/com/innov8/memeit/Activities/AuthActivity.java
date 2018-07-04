package com.innov8.memeit.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;

import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {
    public static final String STARTING_FRAGMENT_PARAM = "frag";
    public static final int FRAGMENT_LOGIN = 0;
    public static final int FRAGMENT_SIGNUP = 1;
    public static final int FRAGMENT_SETUP = 2;

    private SignUpFragment signUpFragment;
    private LoginFragment loginFragment;
    private SetupFragment setupFragment;

    private int currentFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomMethods.makeWindowSeamless(this);
        setContentView(R.layout.activity_redesigned_login);
        ButterKnife.bind(this);
        CustomMethods.makeBackgroundScrollAnimate(this, R.id.background_login_1, R.id.background_login_2);
        initFragments();
        if (getIntent() != null) {
            int i = getIntent().getIntExtra(STARTING_FRAGMENT_PARAM, FRAGMENT_LOGIN);
            setCurrentFragment(i);
        } else {
            setCurrentFragment(FRAGMENT_LOGIN);
        }
    }

    private Fragment getFragmentForInt(int i) {
        switch (i) {
            case FRAGMENT_LOGIN:
                return loginFragment;
            case FRAGMENT_SIGNUP:
                return signUpFragment;
            case FRAGMENT_SETUP:
                return setupFragment;
            default:
                throw new IllegalArgumentException("Index should have been either 0,1 or 2!");
        }
    }

    private void initFragments() {
        signUpFragment = new SignUpFragment();
        loginFragment = new LoginFragment();
        setupFragment = new SetupFragment();
    }

    public void setCurrentFragment(int i) {
        currentFrag = i;
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.holder, getFragmentForInt(i))
                .commit();
    }

    /*This method returns the height of the navigation buttons*/
    private static int getSoftButtonsBarHeight(Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            a.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            return realHeight > usableHeight ? realHeight - usableHeight : 0;
        }
        return 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE) {
            getFragmentForInt(currentFrag).onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class LoginFragment extends Fragment {
        public LoginFragment() {
        }

        OnCompleteListener<Void> signInCompletedListener;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_login, container, false);


            CustomMethods.makeEditTextsAvenir(getActivity(), view, R.id.signup_password, R.id.signup_username);

            final EditText emailV = view.findViewById(R.id.signup_username);
            final EditText passwordV = view.findViewById(R.id.signup_password);
                    /* This sets the bottom padding that makes the views not go underneath the navigation bar */
            view.findViewById(R.id.rel).setPadding(
                    0,
                    (int) (16 * getResources().getDisplayMetrics().density + 0.5f),
                    0,
                    (int) (10 * getResources().getDisplayMetrics().density + 0.5f) + getSoftButtonsBarHeight(getActivity()));

            view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AuthActivity a = ((AuthActivity) LoginFragment.this.getActivity());
                    a.setCurrentFragment(FRAGMENT_SIGNUP);
                }
            });
            signInCompletedListener = new OnCompleteListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                }

                @Override
                public void onFailure(Error error) {
                    Toast.makeText(getContext(), error.getDefaultMessage() + "\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = emailV.getText().toString();
                    String password = passwordV.getText().toString();
                    if (/*CustomMethods.isEmailValid(email) && */password.length() >= 8/*todo: change this back to 8*/) {
                        MemeItAuth.getInstance().signInWithEmail(email, password, signInCompletedListener);
                    } else {
                        Toast.makeText(getContext(), "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            view.findViewById(R.id.signup_google).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MemeItAuth.getInstance().signInWithGoogle(getActivity());
                }
            });
            view.findViewById(R.id.signup_facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo facebook signin
                }
            });
            return view;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE)
                MemeItAuth.getInstance().handleGoogleSignInResult(data, signInCompletedListener);
        }
    }

    public static class SignUpFragment extends Fragment {
        public SignUpFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_signup, container, false);
            final EditText emailV = view.findViewById(R.id.signup_username);
            final EditText passwordV = view.findViewById(R.id.signup_password);

            CustomMethods.makeEditTextsAvenir(getActivity(), view, R.id.signup_password, R.id.signup_username, R.id.signup_confirm);

                    /* This sets the bottom padding that makes the views not go underneath the navigation bar */
            view.findViewById(R.id.rel).setPadding(
                    0,
                    (int) (16 * getResources().getDisplayMetrics().density + 0.5f),
                    0,
                    (int) (10 * getResources().getDisplayMetrics().density + 0.5f) + getSoftButtonsBarHeight(getActivity()));
            view.findViewById(R.id.rel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AuthActivity a = ((AuthActivity) SignUpFragment.this.getActivity());
                    a.setCurrentFragment(FRAGMENT_LOGIN);
                }
            });
            view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = emailV.getText().toString();
                    String password = passwordV.getText().toString();
                    if (CustomMethods.isEmailValid(email) && password.length() >= 1/*todo: change this back to 8*/) {
                        showProgress(true);
                        MemeItAuth.getInstance().signUpWithEmail(email, password, new OnCompleteListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showProgress(false);
                                AuthActivity a = ((AuthActivity) SignUpFragment.this.getActivity());
                                a.setCurrentFragment(FRAGMENT_SETUP);
                            }

                            @Override
                            public void onFailure(Error error) {
                                showProgress(false);
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        //todo show the neccesary errors with a snackbar
                        Toast.makeText(getContext(), "email or password error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            view.findViewById(R.id.signup_google).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MemeItAuth.getInstance().signInWithGoogle(getActivity());
                }
            });
            view.findViewById(R.id.signup_facebook).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo facebook signup
                }
            });
            return view;
        }

        private void showProgress(boolean show) {
            getView().findViewById(R.id.spin_kit_login).setVisibility(show ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == MemeItAuth.GOOGLE_SIGNIN_REQUEST_CODE) {
                MemeItAuth.getInstance().handleGoogleSignUpResult(data, new OnCompleteListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        AuthActivity a = ((AuthActivity) SignUpFragment.this.getActivity());
                        a.setupFragment.fromGoogle(user.getName(), user.getImageUrl());
                        a.setCurrentFragment(FRAGMENT_SETUP);
                    }

                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public static class SetupFragment extends Fragment implements View.OnClickListener {
        public SetupFragment() {
        }

        String name;
        String image_url;
        boolean isFromGoogle;
        private EditText nameV;
        private SimpleDraweeView profileV;


        public void fromGoogle(String name, String pp) {
            if (!TextUtils.isEmpty(pp)) isFromGoogle = true;
            image_url = pp;
            this.name = name;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_setup_profile, container, false);
            nameV = view.findViewById(R.id.name_setup);
            EditText username = view.findViewById(R.id.username_setup);
            EditText tags = view.findViewById(R.id.tags);
            CustomMethods.makeEditTextsAvenir(getActivity(), view, R.id.name_setup, R.id.tags_setup, R.id.username_setup);
            profileV = view.findViewById(R.id.profile);
            profileV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(getContext(), SetupFragment.this);
                }
            });
            view.findViewById(R.id.finish).setOnClickListener(this);
            nameV.setText(name);
            ImageUtils.loadImageTo(profileV, image_url);
            return view;
        }

        @Override
        public void onClick(View view) {
            String name = nameV.getText().toString();
            if (isFromGoogle) {
                uploadData(name, image_url);
            } else {
                if (image_url != null) {
                    uploadImageThenData();
                } else {
                    uploadData(name, null);
                }
            }
        }

        //todo-jv: upload tags and username
        private void uploadData(String name, String image_url) {
            User user = new User(name, image_url);
            MemeItUsers.getInstance().updateMyData(user, new OnCompleteListener<User>() {
                @Override
                public void onSuccess(User body) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                }

                @Override
                public void onFailure(Error error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void uploadImageThenData() {
            MediaManager.get().upload(image_url).callback(new UploadCallback() {
                @Override
                public void onStart(String s) {
                    Toast.makeText(getContext(), "Image Uploading Started", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(String s, long l, long l1) {
                    Toast.makeText(getContext(), "Progress: " + l + "/" + l1, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String s, Map map) {
                    String url = String.valueOf(map.get("secure_url"));
                    String name = nameV.getText().toString();
                    uploadData(name, url);
                }

                @Override
                public void onError(String s, ErrorInfo errorInfo) {
                    Toast.makeText(getContext(), "Image Upload Error: " + errorInfo.getDescription(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String s, ErrorInfo errorInfo) {

                }
            }).dispatch();

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    image_url = result.getUri().toString();
                    isFromGoogle = false;
                    ImageUtils.loadImageTo(profileV, image_url);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
