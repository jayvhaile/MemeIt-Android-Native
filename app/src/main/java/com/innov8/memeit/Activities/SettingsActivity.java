package com.innov8.memeit.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.CustomClasses.SharedPrefs;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.Reaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefs = new SharedPrefs(getApplicationContext(),SharedPrefs.SETTINGS);
        context = this;

        initViews();

        initSettings();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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

    public void initViews(){
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        defaultReaction = findViewById(R.id.default_reaction);
        cachesize = findViewById(R.id.cache_size);
        imageQuality = findViewById(R.id.image_quality);
        nameProfile = findViewById(R.id.namesettings);
        usernameProfile = findViewById(R.id.usernamesettings);
        pp=findViewById(R.id.settings_pp);
    }

    public void initSettings(){
        username.setText(sharedPrefs.getUsername());
        name.setText(sharedPrefs.getName());
        cachesize.setText(getCacheSize());
        defaultReaction.setImageDrawable(getDefaultReactionDrawable());
        imageQuality.setText(getImageQuality());
        //pp.setImageURI(""); //todo: JV replace the empty string with a method to retrieve URI
        nameProfile.setText(sharedPrefs.getName());
        usernameProfile.setText(sharedPrefs.getUsername());
    }

    public String getCacheSize(){
        String s = "";
        SharedPrefs.CachePreset cachePreset = sharedPrefs.getCacheSize();
        switch (cachePreset){
            case LOW: s = "Low";
            case MEDIUM: s = "Medium";
            case HIGH: s = "High";
        }
        return s;
    }
    public String getImageQuality(){
        String s = "";
        SharedPrefs.QualityPreset cachePreset = sharedPrefs.getImageQuality();
        switch (cachePreset){
            case LOW: s = "Low";
            case MEDIUM: s = "Medium";
            case HIGH: s = "High";
        }
        return s;
    }

    public Drawable getDefaultReactionDrawable(){
        switch (sharedPrefs.getDefaultReaction()){
            case FUNNY:
                return context.getResources().getDrawable(R.mipmap.laughing);
            case VERY_FUNNY:
                return context.getResources().getDrawable(R.mipmap.rofl);
            case STUPID:
                return context.getResources().getDrawable(R.mipmap.neutral);
            case ANGERING:
                return context.getResources().getDrawable(R.mipmap.angry);
                default: return context.getResources().getDrawable(R.mipmap.laughing);
        }
    }

    public void requestName(){
        new MaterialDialog.Builder(this)
                .input("name", "prefill", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                    }
                }).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = null;
        builder.setTitle("Enter your MemeIt name");
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setText(sharedPrefs.getName());
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sharedPrefs.setName(input.getText().toString());
                name.setText(input.getText().toString());
            }
        });
        final AlertDialog finalDialog = dialog;
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                hide();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void requestUsername(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = null;
        final boolean usernameTaken = false;
        builder.setTitle("Enter a username");
        View view = LayoutInflater.from(context).inflate(R.layout.username_dialog,null);
        final EditText usernameInput = view.findViewById(R.id.dialog_username);
        final TextView isAvailableText = view.findViewById(R.id.dialog_isavailable);
        usernameInput.setText(sharedPrefs.getUsername());
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                //todo jv: add a method to check if username is taken then change the value of usernameTaken
                if (!usernameTaken && usernameInput.getText().toString().length() > 0) {
                    isAvailableText.setText("Username available");
                    isAvailableText.setTextColor(getResources().getColor(R.color.green));
                } else {
                    isAvailableText.setText("Username taken");
                    isAvailableText.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });
        builder.setView(view);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!usernameTaken){
                    sharedPrefs.setUsername(usernameInput.getText().toString());
                    name.setText(usernameInput.getText().toString());
                }
                else Toast.makeText(context,"Username taken",Toast.LENGTH_SHORT).show();
            }
        });
        final AlertDialog finalDialog = dialog;
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finalDialog.hide();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    public void requestImageQuality(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a preset");
        final AlertDialog dialog = builder.create();
        dialog.setContentView(R.layout.quality_chooser);
        dialog.show();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.low:
                        sharedPrefs.setImageQuality(SharedPrefs.QualityPreset.LOW);
                        imageQuality.setText("Low");
                        break;
                    case R.id.medium:
                        sharedPrefs.setImageQuality(SharedPrefs.QualityPreset.MEDIUM);
                        imageQuality.setText("Medium");
                        break;
                    case R.id.high:
                        sharedPrefs.setImageQuality(SharedPrefs.QualityPreset.HIGH);
                        imageQuality.setText("High");
                        break;
                }
                dialog.hide();
            }
        };
        dialog.findViewById(R.id.low).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.medium).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.high).setOnClickListener(onClickListener);
    }

    public void requestImageCache(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose a preset");
        final AlertDialog dialog = builder.create();
        dialog.setContentView(R.layout.quality_chooser);
        dialog.show();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
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
    public void requestDefaultReaction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a reaction");
        View view = LayoutInflater.from(context).inflate(R.layout.reaction_chooser,null);
        final AlertDialog dialog = builder.create();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
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
