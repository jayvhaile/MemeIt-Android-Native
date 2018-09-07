package com.innov8.memeit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.Comment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
/**
 * Created by Jv on 7/5/2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<ViewHolder> {
    
    List<Comment> comments;

    private Context mContext;
    private LayoutInflater mInflater;
    private boolean isLoading;
    public CommentsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        comments = new ArrayList<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.list_item_comment,parent,false);        
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((CommentViewHolder)holder).bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
    
    public void addAll(List<Comment> comments) {
        if(comments.size()==0)return;
        int start = this.comments.size();
        this.comments.addAll(comments);
        notifyItemRangeInserted(start, comments.size());
    }

    public void add(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    public void remove(Comment comment) {
        if (comments.contains(comment)) {
            int index = comments.indexOf(comment);
            comments.remove(comment);
            notifyItemRemoved(index);
        }
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void setAll(List<Comment> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }
    
    protected class CommentViewHolder extends ViewHolder{
        private final TextView commentV;
        private final TextView dateV;

        private final SimpleDraweeView posterPicV;
        private final TextView posterNameV;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentV=itemView.findViewById(R.id.list_comment);
            dateV=itemView.findViewById(R.id.list_item_date);
            posterPicV=itemView.findViewById(R.id.comment_poster_pp);
            posterNameV=itemView.findViewById(R.id.list_name);
        }
        
        public void bind(Comment comment){
            commentV.setText(comment.getComment());
            ImageUtils.loadImageFromCloudinaryTo(posterPicV,comment.getPoster().getProfileUrl());
            posterNameV.setText(comment.getPoster().getName());
            dateV.setText(CustomMethods.convertDate(comment.getDate()));
        }
    }
}
