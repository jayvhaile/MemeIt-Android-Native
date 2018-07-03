package com.innov8.memeit.Adapters;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.MemeItGlideModule;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.utilis.OnCompleteListener;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Jv on 6/16/2018.
 */

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.MemeViewHolder> {
    private static final String TAG="MemeAdapter";

    private Context mContext;
    private List<Meme> memes;
    private LayoutInflater mInflater;
    public MemeAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater=LayoutInflater.from(mContext);
        memes=new ArrayList<>();
    }

    @NonNull
    @Override
    public MemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= mInflater.inflate(R.layout.list_item_meme,parent,false);
        return new MemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemeViewHolder holder, int position) {
        holder.bindMeme(memes.get(position));
    }

    @Override
    public int getItemCount() {
        return memes.size();
    }
    public void addAll(List<Meme> memes){
        int start=this.memes.size();
        this.memes.addAll(memes);
        notifyItemRangeInserted(start,memes.size());
    }
    public void add(Meme meme){
        memes.add(meme);
        notifyItemInserted(memes.size()-1);
    }
    public void remove(Meme meme){
        if(memes.contains(meme)){
            int index=memes.indexOf(meme);
            memes.remove(meme);
            notifyItemRemoved(index);
        }
    }
    public void clear(){
        memes.clear();
        notifyDataSetChanged();
    }
    public void setAll(List<Meme> memes){
        this.memes.clear();
        this.memes.addAll(memes);
        notifyDataSetChanged();
    }

    protected class MemeViewHolder extends RecyclerView.ViewHolder {
        private final SimpleDraweeView posterPicV;
        private final SimpleDraweeView memeImageV;
        private final ImageButton commentBtnV;
        private final TextView posterNameV;
        private final TextView reactionCountV;
        private final TextView commentCountV;
        private final ImageButton meme_menu;
        private String memeId;
        public MemeViewHolder(View itemView) {
            super(itemView);
            meme_menu = itemView.findViewById(R.id.meme_options);
            posterPicV = itemView.findViewById(R.id.meme_poster_pp);
            memeImageV = itemView.findViewById(R.id.meme_image);
            commentBtnV = itemView.findViewById(R.id.meme_comment);
            posterNameV = itemView.findViewById(R.id.meme_poster_name);
            reactionCountV = itemView.findViewById(R.id.meme_reactions);
            commentCountV = itemView.findViewById(R.id.meme_comment_count);
            commentBtnV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo start comment list activity
                }
            });
            meme_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMemeMenu();
                }
            });
        }

        public void bindMeme(Meme meme) {
            memeId=meme.getMemeId();
            posterNameV.setText(meme.getPoster().getName());
            reactionCountV.setText(String.format("%d people reacted",meme.getReactionCount()));
            commentCountV.setText(String.valueOf(meme.getCommentCount()));
            loadProfileImage(meme.getPoster().getProfileUrl());
            loadMemeImage(meme.getMemeImageUrl());
        }
        public void loadMemeImage(String url){
            MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                    .stepSize(10)
                    .generate(Uri.parse(url).getLastPathSegment(), memeImageV, new ResponsiveUrl.Callback() {
                        @Override
                        public void onUrlReady(Url url) {
                            memeImageV.setImageURI(url.generate());
                        }
                    });

        }
        public void loadProfileImage(String url){
            if(TextUtils.isEmpty(url)){
                posterPicV.setImageURI((Uri) null);
                return;
            }
            MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT)
                    .stepSize(10)
                    .generate(Uri.parse(url).getLastPathSegment(), posterPicV, new ResponsiveUrl.Callback() {
                        @Override
                        public void onUrlReady(Url url) {
                            posterPicV.setImageURI(url.generate());
                        }
                    });
        }
        private void showMemeMenu(){
            PopupMenu menu=new PopupMenu(mContext,meme_menu);
            menu.getMenuInflater().inflate(R.menu.meme_menu,menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.menu_delete_meme:
                            MemeItMemes.getInstance().deleteMeme(memeId, new OnCompleteListener<ResponseBody>() {
                                @Override
                                public void onSuccess(ResponseBody responseBody) {
                                    remove(Meme.forID(memeId));
                                    Toast.makeText(mContext, "Meme Deleted", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Error error) {
                                    Toast.makeText(mContext, "Meme not Deleted "+error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            return true;

                    }
                    return false;
                }
            });
            menu.show();
        }
    }
}
