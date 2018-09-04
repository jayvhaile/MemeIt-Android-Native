package com.innov8.memeit.Activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.CustomClasses.SharedPrefs;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;

public class SettingsActivity2 extends AppCompatActivity implements View.OnClickListener {

    TextView username;
    TextView name;
    TextView cachesize;
    TextView imageQuality;
    ImageView defaultReaction;
    SimpleDraweeView pp;
    TextView nameProfile;
    TextView usernameProfile;

    Context context;

    SharedPrefs sharedPrefs;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefs = new SharedPrefs(getApplicationContext(), SharedPrefs.SETTINGS);
        context = this;

        initViews();

        initSettings();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.name_linear:
                requestName();
                break;
            case R.id.username_linear:
                requestUsername();
                break;
            case R.id.tags_to_follow_linear:
                break;
            case R.id.image_quality_linear:
                requestImageQuality();
                break;
            case R.id.cache_size_linear:
                requestImageCache();
                break;
            case R.id.default_reaction_linear:
                requestDefaultReaction();
                break;
            case R.id.change_pp:
                //todo finish this later
                break;
        }
    }

    public void initViews() {
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        defaultReaction = findViewById(R.id.default_reaction);
        cachesize = findViewById(R.id.cache_size);
        imageQuality = findViewById(R.id.image_quality);
        nameProfile = findViewById(R.id.namesettings);
        usernameProfile = findViewById(R.id.usernamesettings);
        pp = findViewById(R.id.settings_pp);
    }

    public void initSettings() {
        username.setText(sharedPrefs.getUsername());
        name.setText(sharedPrefs.getName());
        cachesize.setText(getCacheSize());
        defaultReaction.setImageDrawable(getDefaultReactionDrawable());
        imageQuality.setText(sharedPrefs.getImageQuality().toString());
        //pp.setImageURI(""); //todo: JV replace the empty string with a method to retrieve URI
        nameProfile.setText(sharedPrefs.getName());
        usernameProfile.setText(sharedPrefs.getUsername());
    }

    public String getCacheSize() {
        String s = "";
        SharedPrefs.CachePreset cachePreset = sharedPrefs.getCacheSize();
        switch (cachePreset) {
            case LOW:
                s = "Low";
            case MEDIUM:
                s = "Medium";
            case HIGH:
                s = "High";
        }
        return s;
    }



    public Drawable getDefaultReactionDrawable() {
        switch (sharedPrefs.getDefaultReaction()) {
            case FUNNY:
                return context.getResources().getDrawable(R.drawable.laughing);
            case VERY_FUNNY:
                return context.getResources().getDrawable(R.drawable.rofl);
            case STUPID:
                return context.getResources().getDrawable(R.drawable.neutral);
            case ANGERING:
                return context.getResources().getDrawable(R.drawable.angry);
            default:
                return context.getResources().getDrawable(R.drawable.laughing);
        }
    }

    public void requestName() {
        new MaterialDialog.Builder(this)
                .title("Enter your Name")
                .input("Enter Name here", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Toast.makeText(context, "hiya", Toast.LENGTH_SHORT).show();
                        MemeItUsers.getInstance().updateName(context,input.toString(), new OnCompleteListener<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                Toast.makeText(context, "Name updated!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Error error) {
                                Toast.makeText(context, "Name Update Failed!\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .negativeText("Cancel")
                .show();
    }

    public void requestUsername() {
        new MaterialDialog.Builder(this)
                .title("Enter your username here")
                .input("Enter Name here", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        //todo validate username
                        MemeItUsers.getInstance().updateUsername(context,input.toString(), new OnCompleteListener<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                Toast.makeText(context, "Username updated!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Error error) {
                                Toast.makeText(context, "Username Update Failed!\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .show();
    }

    public void requestImageQuality() {
        new MaterialDialog.Builder(this)
                .items("Very Low","Low","Medium","High","Very High")
                .itemsCallbackSingleChoice(sharedPrefs.getImageQuality().ordinal(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        sharedPrefs.setImageQuality(SharedPrefs.QualityPreset.values()[which]);
                        return true;
                    }
                }).show();
    }

    public void requestImageCache() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a preset");
        final AlertDialog dialog = builder.create();
        dialog.setContentView(R.layout.quality_chooser);
        dialog.show();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.low:
                        sharedPrefs.setCacheSize(SharedPrefs.CachePreset.LOW);
                        cachesize.setText("Low");
                        break;
                    case R.id.medium:
                        sharedPrefs.setCacheSize(SharedPrefs.CachePreset.MEDIUM);
                        cachesize.setText("Medium");
                        break;
                    case R.id.high:
                        sharedPrefs.setCacheSize(SharedPrefs.CachePreset.HIGH);
                        cachesize.setText("High");
                        break;
                }
                dialog.hide();
            }
        };
        dialog.findViewById(R.id.low).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.medium).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.high).setOnClickListener(onClickListener);
    }

    public void requestDefaultReaction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a reaction");
        View view = LayoutInflater.from(context).inflate(R.layout.reaction_chooser, null);
        final AlertDialog dialog = builder.create();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.laughing:
                        sharedPrefs.setDefaultReaction(Reaction.ReactionType.FUNNY);
                    case R.id.rofl:
                        sharedPrefs.setDefaultReaction(Reaction.ReactionType.VERY_FUNNY);
                    case R.id.neutral:
                        sharedPrefs.setDefaultReaction(Reaction.ReactionType.STUPID);
                    case R.id.angry:
                        sharedPrefs.setDefaultReaction(Reaction.ReactionType.ANGERING);
                }
                defaultReaction.setImageDrawable(getDefaultReactionDrawable());
                dialog.hide();
            }
        };
        view.findViewById(R.id.laughing).setOnClickListener(onClickListener);
        view.findViewById(R.id.rofl).setOnClickListener(onClickListener);
        view.findViewById(R.id.neutral).setOnClickListener(onClickListener);
        view.findViewById(R.id.angry).setOnClickListener(onClickListener);
        builder.setView(view);
        dialog.show();
    }
}
