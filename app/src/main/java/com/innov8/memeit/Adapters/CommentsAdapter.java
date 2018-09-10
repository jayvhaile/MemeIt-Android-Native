package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memegenerator.adapters.ListAdapter;
import com.innov8.memegenerator.adapters.MyViewHolder;
import com.innov8.memeit.Activities.ProfileActivity;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.Comment;

import org.jetbrains.annotations.NotNull;
/**
 * Created by Jv on 7/5/2018.
 */

public class CommentsAdapter extends ListAdapter<Comment> {


    public CommentsAdapter(@NotNull Context mContext) {
        super(mContext, R.layout.list_item_comment);
    }

    @NotNull
    @Override
    public MyViewHolder<Comment> createViewHolder(@NotNull View view) {
        return new CommentViewHolder(view);
    }

    protected class CommentViewHolder extends MyViewHolder<Comment>{
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getMContext(), ProfileActivity.class);
                    i.putExtra("user", getItemAt(getItem_position()).getPoster().toUser());
                    getMContext().startActivity(i);
                }
            });
        }
        
        public void bind(Comment comment){
            commentV.setText(comment.getComment());
            ImageUtils.loadImageFromCloudinaryTo(posterPicV,comment.getPoster().getProfileUrl());
            posterNameV.setText(comment.getPoster().getName());
            dateV.setText(CustomMethods.convertDate(comment.getDate()));
        }
    }
}
