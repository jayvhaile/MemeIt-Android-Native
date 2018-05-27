package com.innov8.memeit.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItAuth;
import com.memeit.backend.OnCompleteListener;
import com.memeit.backend.dataclasses.User;

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

    String image_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_details);
        ButterKnife.bind(this);


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
                //todo: make them choose profile picture from gallery here
            }
        });


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=nameV.getText().toString();
                //todo validate name

                User user=new User(name,image_url);
                MemeItAuth.getInstance().uploadUserData(user, new OnCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("memeitc", "onSuccess: ");
                        startActivity(new Intent(SignUpDetailsActivity.this,MainActivity.class));
                    }
                    @Override
                    public void onFailure(Error error) {
                        Log.d("memeitc", "failer: ");
                        Toast.makeText(SignUpDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
