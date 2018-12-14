package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.content.SharedPreferences;

import com.memeit.backend.models.Reaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class SharedPrefs {
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public enum CachePreset {
        LOW, MEDIUM, HIGH
    }
    public enum ColorPreset{
        RED, ORANGE, BLUE, GREEN, BLACK, PURPLE, GOLDEN, PINK
    }
    public enum QualityPreset {
        VERY_LOW(10),LOW(25), MEDIUM(50), HIGH(75),VERY_HIGH(100);
        final int quality;

        QualityPreset(int quality) {
            this.quality = quality;
        }

        public int getQuality() {
            return quality;
        }

        @Override
        public String toString() {
            String s= super.toString();
            String words[]=s.split("_");

            StringBuilder sb=new StringBuilder();
            for (String word:words) {
                sb.append(word.charAt(0)+word.substring(1).toLowerCase());
            }
            return sb.toString();
        }
    }

    public static final String SETTINGS = "SettingPrefs";

    public SharedPrefs(Context context,@Nullable String type) {
        this.context = context;
        preferences = context.getSharedPreferences(type==null?SETTINGS:type,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getUsername() {
        return preferences.getString("username","user");
    }

    public void setUsername(String username) {
        editor.putString("username",username);
        editor.apply();
    }

    public String getName() {
        return preferences.getString("name","user");
    }

    public void setName(String name) {
        editor.putString("name",name);
        editor.apply();
    }

    public Reaction.ReactionType getDefaultReaction() {
        switch (preferences.getString("defaultReaction","funny")){
            case "funny": return Reaction.ReactionType.FUNNY;
            case "rofl": return Reaction.ReactionType.VERY_FUNNY;
            case "stupid": return Reaction.ReactionType.STUPID;
            case "angry": return Reaction.ReactionType.ANGERING;
            default:return Reaction.ReactionType.FUNNY;
        }
    }

    public void setDefaultReaction(Reaction.ReactionType defaultReaction) {
        String reaction;
        switch (defaultReaction){
            case FUNNY:reaction="funny";
            case STUPID:reaction="stupid";
            case VERY_FUNNY:reaction="rofl";
            case ANGERING:reaction="angry";
            default:reaction = "funny";
        }

        editor.putString("defaultReaction",reaction);
        editor.apply();
    }

    public String getTagsJson() {
        return preferences.getString("followedJson","{}");
    }

    public void setTagsJson(String tagsJson) {
        editor.putString("followedJson", tagsJson);
        editor.apply();
    }



    public void addTag(String tag){
        try {
            JSONArray jsonArray = new JSONArray(getTagsJson());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tag",tag);
            jsonArray.put(jsonObject);
            setTagsJson(jsonArray.toString());
        }
        catch (JSONException e) { e.printStackTrace(); }
    }
    public ArrayList<String> getTags(){
        ArrayList<String> tags = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(getTagsJson());
            for(int i = 0;i<jsonArray.length();i++){
                tags.add((jsonArray.getJSONObject(i).getString("tag")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return tags;
        }
        return tags;
    }

    public void setCacheSize(CachePreset cacheSize){
        String size;
        switch (cacheSize){
            case LOW:size = "LOW";
            case MEDIUM:size = "MEDIUM";
            case HIGH:size = "HIGH";
            default:size = "HIGH";
        }
        editor.putString("cachesize",size);
        editor.apply();
    }
    public void setImageQuality(QualityPreset cacheSize){
        editor.putInt("imageQuality",cacheSize.ordinal());
        editor.apply();
    }
    public QualityPreset getImageQuality(){
        return QualityPreset.values()[preferences.getInt("imageQuality",2)];
    }
    public CachePreset getCacheSize(){
        CachePreset qualityPresets;
        switch (preferences.getString("cachesize","HIGH")){
            case "LOW":qualityPresets = CachePreset.LOW;
            case "medium" : qualityPresets = CachePreset.MEDIUM;
            case "HIGH" : qualityPresets = CachePreset.HIGH;
            default: qualityPresets = CachePreset.HIGH;
        }
        return qualityPresets;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCoverColor(ColorPreset colorPreset){
        int color;
        switch (colorPreset){
            case RED: color = context.getResources().getColor(R.color.red);
            case BLUE:color = context.getResources().getColor(R.color.blue);
            case PINK:color = context.getResources().getColor(R.color.pink);
            case BLACK:color = context.getResources().getColor(R.color.black);
            case GREEN:color = context.getResources().getColor(R.color.greeny);
            case ORANGE:color = context.getResources().getColor(R.color.orange);
            case PURPLE:color = context.getResources().getColor(R.color.purple);
            case GOLDEN:color = context.getResources().getColor(R.color.golden);
            default: color = context.getResources().getColor(R.color.orange);
        }
        editor.putInt("color",color);
        editor.apply();
    }
    public int getCoverColor(){
        return preferences.getInt("color",context.getResources().getColor(R.color.orange));
    }
}
