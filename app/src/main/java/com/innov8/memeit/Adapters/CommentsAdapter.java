package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memegenerator.adapters.ListAdapter;
import com.innov8.memegenerator.adapters.MyViewHolder;
import com.innov8.memeit.Activities.ProfileActivity;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.utilis.OnCompleteListener;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import okhttp3.ResponseBody;

/**
 * Created by Jv on 7/5/2018.
 */

public class CommentsAdapter extends ListAdapter<Comment> {

    boolean isPostingComment;


    public CommentsAdapter(@NotNull Context mContext) {
        super(mContext, R.layout.list_item_comment_new);
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
        private ImageView edit;
        private ImageView delete;
        Group ownCommentViews;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentV=itemView.findViewById(R.id.list_comment);
            dateV=itemView.findViewById(R.id.list_item_date);
            posterPicV=itemView.findViewById(R.id.comment_poster_pp);
            posterNameV=itemView.findViewById(R.id.list_name);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            ownCommentViews = itemView.findViewById(R.id.own_comment);
            posterPicV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getMContext(), ProfileActivity.class);
                    i.putExtra("user", getItemAt(getItem_position()).getPoster().toUser());
                    getMContext().startActivity(i);
                }
            });
        }
        
        public void bind(final Comment mComment){
            Boolean isOwnComment = MemeItUsers.getInstance().getMyUser(getMContext()).getUserID().equals(mComment.getPosterID());
            commentV.setText(mComment.getComment());
            ImageUtils.loadImageFromCloudinaryTo(posterPicV,mComment.getPoster().getProfileUrl());
            posterNameV.setText(mComment.getPoster().getName());
            dateV.setText(CustomMethods.convertDate(mComment.getDate()));
            if(isOwnComment) {

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new MaterialDialog.Builder(getMContext())
                                .title("Delete comment?")
                                .positiveText("Yes")
                                .negativeText("No")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        MemeItMemes.getInstance().deleteComment(mComment.getMemeID(), mComment.getCommentID(), new OnCompleteListener<ResponseBody>() {
                                            @Override
                                            public void onSuccess(ResponseBody responseBody) {
                                                Toast.makeText(getMContext(),"Deleted.",Toast.LENGTH_SHORT).show();
                                                remove(mComment);
                                                notifyItemRemoved(getItems().indexOf(mComment));
                                            }

                                            @Override
                                            public void onFailure(Error error) {
                                                Toast.makeText(getMContext(),"An error has occured. Please try again.",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).show()
                        ;
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String[] text = new String[1];
                        final OnCompleteListener onCommentCompletedListener = new OnCompleteListener() {
                            @Override
                            public void onSuccess(Object o) {
                                setPostingComment(false);
                                Toast.makeText(getMContext(), "Edited.", Toast.LENGTH_SHORT).show();
                                mComment.setComment(text[0]);
                                commentV.setText(text[0]);
                            }

                            @Override
                            public void onFailure(Error error) {
                                Toast.makeText(getMContext(), "Editing has failed because of a problem. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        };
                        new MaterialDialog.Builder(getMContext())
                                .title("Edit comment")
                                .input("Comment", mComment.getComment(), false, new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        text[0] = (String) input;
                                        if (isPostingComment() || TextUtils.isEmpty(input)) return;
                                        setPostingComment(true);
                                        MemeItMemes.getInstance().editComment(mComment.getCommentID(), (String) input, onCommentCompletedListener);
                                    }
                                }).show();
                    }
                });
            }
        }
    }
    public boolean isPostingComment() {
        return isPostingComment;
    }

    public void setPostingComment(boolean postingComment) {
        isPostingComment = postingComment;
    }
}
