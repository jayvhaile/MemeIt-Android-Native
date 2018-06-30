package com.innov8.memeit.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.innov8.memeit.CustomClasses.MemeItGlideModule;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.Meme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jv on 6/16/2018.
 */

public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.MemeViewHolder> {
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
        private final ImageView posterPicV;
        private final ImageView memeImageV;
        private final ImageButton commentBtnV;
        private final TextView posterNameV;
        private final TextView reactionCountV;
        private final TextView commentCountV;
        private String memeId;
        public MemeViewHolder(View itemView) {
            super(itemView);
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
        }

        public void bindMeme(Meme meme) {
            memeId=meme.getMemeId();
            posterNameV.setText(meme.getPoster().getName());
            reactionCountV.setText(String.format("%d people reacted",meme.getReactionCount()));
            commentCountV.setText(String.valueOf(meme.getCommentCount()));
            Glide.with(mContext)
                    .load(meme.getMemeImageUrl())
                    .thumbnail(0.25f)
                    .into(memeImageV);
            Glide.with(mContext)
                    .load(meme.getPoster().getProfileUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(posterPicV);
        }
    }
}
