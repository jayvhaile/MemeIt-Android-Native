package com.innov8.memegenerator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.DynamicLayout;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.innov8.memegenerator.memeEngine.MemeTextView;
import com.innov8.memegenerator.models.MemeTemplate;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.utilis.OnCompleteListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SimpleMemeGenerator extends AppCompatActivity {
    private static final String TAG="SimpleMemeGenerator";

    private EditText texts;
    private EditText tags;
    private ImageView memeImage;
    private ProgressDialog pd;
    private transient Typeface typeface;

    private Uri image_url;

    Gson gson;
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
                CropImage.activity()
                        .start(SimpleMemeGenerator.this);


            }
        });
        Class<? extends Activity> c;
        MemeTextView memeTextView=gson.fromJson("{\"heightP\":0.1384083,\"textStyleProperty\":{\"allCap\":false,\"bold\":false,\"italic\":false,\"myTypeFace\":{\"fileName\":\"fonts/ubuntu.ttf\",\"name\":\"Ubuntu\"},\"strokeColor\":-16777216,\"strokeWidth\":10.0,\"stroked\":false,\"textColor\":-256,\"textSize\":120.0},\"widthP\":0.5231481,\"xP\":0.20990959,\"yP\":0.84427506}",
                new TypeToken<List<MemeTemplate>>(){}.getType());
        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
        DynamicLayout dl;



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_url=result.getUri();
                Glide.with(this)
                        .load(result.getUri())
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_add))
                        .thumbnail(0.7f)
                        .into(memeImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
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
                printMap(map);
                String public_id= String.valueOf(map.get("public_id"));
                double width= Double.parseDouble(String.valueOf( map.get("width")));
                double height=Double.parseDouble(String.valueOf( map.get("height")));
                double ratio=width/height;
                pd.setMessage("Image Uploaded");
                pd.setIndeterminate(true);
                MemeItMemes.getInstance().postMeme(prepareRequest(public_id,ratio), new OnCompleteListener<Meme>() {
                    @Override
                    public void onSuccess(Meme memeResponse) {
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

    private Meme prepareRequest(String uri,double ratio){
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
        return Meme.createMeme(uri,ratio, Meme.MemeType.IMAGE,texts,tags);

    }

    private void printMap(Map map){
        Object keys[]=map.keySet().toArray();

        for (int i = 0; i < keys.length; i++) {
            String key= String.valueOf(keys[i]);
            String elem= String.valueOf(map.get(keys[i]));
            Log.d(TAG, String.format("%02d=> %s : %s",i, key,elem));
        }
    }


}
