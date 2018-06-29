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
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetupProfileActivity extends AppCompatActivity {
    public static String TAG="fuck";

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
    boolean isFromGoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        ButterKnife.bind(this);

        activity = this;
        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");
        nameV.setTypeface(avenir);

        if(getIntent().getData()!=null){
            //this is called if the user signed in with google or facebook and get the profile info from that
            String name=getIntent().getStringExtra(PARAM_NAME);
            image_url= Uri.parse(getIntent().getStringExtra(PARAM_IMAGE_URL));
            nameV.setText(name);

            Glide.with(this)
                    .load(image_url)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile))
                    .thumbnail(0.7f)
                    .into(profileV);

        }




        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_url!=null&&!isFromGoogle){
                    Log.d(TAG, "finish "+image_url.toString());
                    MediaManager.get().upload(image_url).callback(new UploadCallback() {
                     @Override
                     public void onStart(String s) {
                         Log.d(TAG, "onStart: "+s);
                         Toast.makeText(activity, "yayyy", Toast.LENGTH_SHORT).show();
                     }

                     @Override
                     public void onProgress(String s, long l, long l1) {
                         Log.d(TAG, "onProgress: "+l+" / "+l1);
                     }

                     @Override
                     public void onSuccess(String s, Map map) {
                       String url= String.valueOf(map.get("secure_url"));
                       String name=nameV.getText().toString();
                       uploadData(name,url);
                     }

                     @Override
                     public void onError(String s, ErrorInfo errorInfo) {
                         Log.d(TAG, "onError: "+errorInfo.getDescription());
                     }

                     @Override
                     public void onReschedule(String s, ErrorInfo errorInfo) {

                     }
                 }).dispatch();
                }else{
                    String url= image_url==null?null:image_url.toString();
                    String name=nameV.getText().toString();
                    uploadData(name,url);
                }
            }
        });
    }

    private void uploadData(String name,String url){
        User user=new User(name,url);
        MemeItUsers.getInstance().updateMyData(user, new OnCompleteListener<User>() {
            @Override
            public void onSuccess(User body) {
                startActivity(new Intent(SetupProfileActivity.this,MainActivity.class));
            }
            @Override
            public void onFailure(Error error) {
                Toast.makeText(SetupProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
