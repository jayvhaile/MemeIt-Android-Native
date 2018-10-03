package com.innov8.memeit.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.Activities.TagsActivity;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;

import okhttp3.ResponseBody;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SetupFragment extends AuthFragment implements View.OnClickListener {
    public SetupFragment() {
    }

    String name;
    Uri image_url;
    boolean isFromGoogle;
    private EditText nameV;
    private SimpleDraweeView profileV;


    public void fromGoogle(String name, String pp) {
        if (!TextUtils.isEmpty(pp)) isFromGoogle = true;
        image_url = Uri.parse(pp);
        this.name = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_profile2, container, false);
        nameV = view.findViewById(R.id.name_setup);
        profileV = view.findViewById(R.id.profile_pic);
        profileV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getAuthActivity());
            }
        });
        actionButton=view.findViewById(R.id.finish);
        actionButton.setOnClickListener(this);

        nameV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager
                        mgr=(InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                finish();
                return true;
            }
        });
        nameV.setText(name);
        profileV.setImageURI(image_url);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.finish:
                finish();
        }

    }

    private void finish() {
        if (mLoading) return;
        String name = nameV.getText().toString();
        if(TextUtils.isEmpty(name)){
            getAuthActivity().showError("Name cannot be empty!");
            return;
        }
        setLoading(true);
        if (isFromGoogle) {
            uploadData(name, image_url.toString());
        } else {
            if (image_url != null) {
                uploadImageThenData();
            } else {
                uploadData(name, null);
            }
        }
    }

    private void uploadData(String name, String image_url) {
        User user = new User(name, image_url);
        MemeItUsers.getInstance().updateMyData(user, new OnCompleteListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody body) {
                setLoading(false);
                Intent i=new Intent(getContext(), TagsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("choose",true);
                startActivity(i);

            }

            @Override
            public void onFailure(Error error) {
                setLoading(false);
                getAuthActivity().showError(error.getMessage());
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
            }

            @Override
            public void onSuccess(String s, Map map) {
                String public_id = String.valueOf(map.get("public_id"));
                String name = nameV.getText().toString();
                uploadData(name, public_id);
            }

            @Override
            public void onError(String s, ErrorInfo errorInfo) {
                setLoading(false);
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
            if (resultCode == Activity.RESULT_OK) {
                image_url = result.getUri();
                isFromGoogle = false;

                Toast.makeText(getContext(), image_url.toString(), Toast.LENGTH_SHORT).show();
                profileV.setImageURI(image_url);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
