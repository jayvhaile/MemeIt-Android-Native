package com.innov8.memeit.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.innov8.memeit.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpDetailsActivity extends AppCompatActivity {
    public static final String PARAM_NAME="name";
    public static final String PARAM_IMAGE_URL="url";
    Typeface avenir;
    @BindView(R.id.name)
    EditText nameV;
    @BindView(R.id.profile)
    ImageView profileV;

    @BindView(R.id.finish)
    View finish;
    Activity activity;

    Uri image_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);
        ButterKnife.bind(this);

        activity = this;
        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");
        nameV.setTypeface(avenir);

        if(getIntent()!=null){
            //this is called if the user signed in with google or facebook and get the profile info from that
            String name=getIntent().getStringExtra(PARAM_NAME);
            String url=getIntent().getStringExtra(PARAM_IMAGE_URL);

            Toast.makeText(this, "url: "+url, Toast.LENGTH_SHORT).show();
            nameV.setText(name);

            Glide.with(this)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile))
                    .thumbnail(0.7f)
                    .into(profileV);

        }

        profileV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)

                        .start(activity);
            }
        });


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "kjhkjhkjh", Toast.LENGTH_SHORT).show();
                if (image_url!=null){
                    Toast.makeText(activity, "yayyy", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "finish "+image_url.toString());
                    MediaManager.get().upload(image_url).callback(new UploadCallback() {
                     @Override
                     public void onStart(String s) {
                         Log.d(TAG, "onStart: ");
                     }

                     @Override
                     public void onProgress(String s, long l, long l1) {
                         Log.d(TAG, "onProgress: "+l+" / "+l1);
                     }

                     @Override
                     public void onSuccess(String s, Map map) {
                         Toast.makeText(activity, "sucess "+s, Toast.LENGTH_SHORT).show();
                         Set set=map.keySet();

                         for (Object oo:set) {
                             Log.d(TAG, "onSuccess key: "+String.valueOf(oo));
                         }

                     }

                     @Override
                     public void onError(String s, ErrorInfo errorInfo) {
                         Log.d(TAG, "onError: "+errorInfo.getDescription());
                     }

                     @Override
                     public void onReschedule(String s, ErrorInfo errorInfo) {

                     }
                 }).unsigned("aa")
                            .dispatch();
                }
                //upload the user image here here
               /* String name=nameV.getText().toString();

                User user=new User(name,image_url.toString());
                MemeItUsers.getInstance().updateMyData(user, new OnCompleteListener<User>() {
                    @Override
                    public void onSuccess(User body) {
                        startActivity(new Intent(SignUpDetailsActivity.this,MainActivity.class));
                    }
                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(SignUpDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });
    }
    public static String TAG="fuck";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_url=result.getUri();
                Glide.with(this)
                        .load(result.getUri())
                        .apply(RequestOptions.circleCropTransform())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile))
                        .thumbnail(0.7f)
                        .into(profileV);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
