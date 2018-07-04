package com.innov8.memegenerator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.MemeRequest;
import com.memeit.backend.dataclasses.MemeResponse;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SimpleMemeGenerator extends AppCompatActivity {
    private static final String TAG="SimpleMemeGenerator";

    private EditText texts;
    private EditText tags;
    private ImageView memeImage;
    private ProgressDialog pd;


    private Uri image_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_meme_generator);

        texts=findViewById(R.id.texts);
        tags=findViewById(R.id.tags);
        memeImage=findViewById(R.id.meme);

        memeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                CropImage.activity()
//                        .start(SimpleMemeGenerator.this); todo : uncomment this
            }
        });
        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                image_url=result.getUri();
//                Glide.with(this)
//                        .load(result.getUri())
//                        .apply(RequestOptions.placeholderOf(R.drawable.ic_add))
//                        .thumbnail(0.7f)
//                        .into(memeImage);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        }
        //todo jv,biruk :  fix image cropper lib error and uncomment this
    }
    private void upload(){
      if(image_url==null){
          Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
          return;
      }

        MediaManager.get().upload(image_url).callback(new UploadCallback() {
            @Override
            public void onStart(String s) {
                pd=ProgressDialog.show(SimpleMemeGenerator.this,"Meme Uploading","Started",true,false);
            }

            @Override
            public void onProgress(String s, long l, long l1) {
                pd.setIndeterminate(false);
                pd.setProgress((int)(l*100/l1));
            }

            @Override
            public void onSuccess(String s, Map map) {
                String url= String.valueOf(map.get("secure_url"));
                pd.setMessage("Image Uploaded");
                pd.setIndeterminate(true);
                MemeItMemes.getInstance().postMeme(prepareRequest(url), new OnCompleteListener<MemeResponse>() {
                    @Override
                    public void onSuccess(MemeResponse memeResponse) {
                        pd.dismiss();
                        Toast.makeText(SimpleMemeGenerator.this, "Meme Uploaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Error error) {

                    }
                });
            }

            @Override
            public void onError(String s, ErrorInfo errorInfo) {
                Toast.makeText(SimpleMemeGenerator.this, ""+errorInfo.getDescription(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onError: "+errorInfo.getDescription());
            }

            @Override
            public void onReschedule(String s, ErrorInfo errorInfo) {

            }
        }).dispatch();
    }

    private MemeRequest prepareRequest(String uri){
        String txt=texts.getText().toString();
        String tag=tags.getText().toString();
        List<String> texts=null;
        List<String> tags=null;
        if (!TextUtils.isEmpty(txt)){
            texts= Arrays.asList(txt.split(","));
        }
        if (!TextUtils.isEmpty(tag)){
            tags= Arrays.asList(tag.split(","));
        }
        return new MemeRequest(uri,texts,tags);

    }

}
