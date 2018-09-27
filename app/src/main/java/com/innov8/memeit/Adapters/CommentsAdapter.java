package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.innov8.memegenerator.adapters.ListAdapter;
import com.innov8.memegenerator.adapters.MyViewHolder;
import com.innov8.memeit.Activities.ProfileActivity;
import com.innov8.memeit.CustomViews.ProfileDraweeView;
import com.innov8.memeit.KUtilsKt;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.utilis.OnCompleteListener;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import okhttp3.ResponseBody;

/**
 * Created by Jv on 7/5/2018.
 */

public class CommentsAdapter extends ListAdapter<Comment> {

    boolean isPostingComment;

    float size;
    public static final int LIKE = 0;
    public static final int DISLIKE = 1;
    public static final int ADD = 0;
    public static final int REMOVE = 1;

    public CommentsAdapter(@NotNull Context mContext) {
        super(mContext, R.layout.list_item_comment_new);
        size = mContext.getResources().getDimension(R.dimen.profile_mini_size);
    }

    @NotNull
    @Override
    public MyViewHolder<Comment> createViewHolder(@NotNull View view) {
        return new CommentViewHolder(view);
    }

    protected class CommentViewHolder extends MyViewHolder<Comment> implements View.OnClickListener {
        private final TextView commentV;
        private final TextView dateV;

        private final ProfileDraweeView posterPicV;
        private final TextView posterNameV;
        private ImageView edit;
        private ImageView delete;
        private TextView like;
        private TextView dislike;
        Group ownCommentViews;


        public CommentViewHolder(View itemView) {
            super(itemView);
            commentV = itemView.findViewById(R.id.list_comment);
            dateV = itemView.findViewById(R.id.list_item_date);
            posterPicV = itemView.findViewById(R.id.comment_poster_pp);
            posterNameV = itemView.findViewById(R.id.list_name);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            like = itemView.findViewById(R.id.like_comment);
            dislike = itemView.findViewById(R.id.dislike_comment);
            ownCommentViews = itemView.findViewById(R.id.own_comment);
            posterPicV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getMContext(), ProfileActivity.class);
                    i.putExtra("user", getItemAt(getItem_position()).getPoster().toUser());
                    getMContext().startActivity(i);
                }
            });
            delete.setOnClickListener(this);
            edit.setOnClickListener(this);
            like.setOnClickListener(this);
            dislike.setOnClickListener(this);
        }


        public void bind(final Comment mComment) {
            KUtilsKt.loadImage(posterPicV,mComment.getPoster().getProfileUrl(),size,size);
            commentV.setText(mComment.getComment());
            posterPicV.setText(KUtilsKt.prefix(mComment.getPoster().getName()));
            posterNameV.setText(mComment.getPoster().getName());
            dateV.setText(KUtilsKt.formatDate(mComment.getDate()));
            like.setText(String.valueOf(mComment.getLikeCount()));
            dislike.setText(String.valueOf(mComment.getDislikeCount()));
            boolean isOwnComment = MemeItUsers.getInstance().getMyUser(getMContext()).getUserID().equals(mComment.getPosterID());
            if (isOwnComment)
                ownCommentViews.setVisibility(View.GONE);
            else
                ownCommentViews.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View v) {
            final Comment comment = getItemAt(getItem_position());
            switch (v.getId()) {
                case R.id.like_comment:
                    if (comment.isLikedByMe()) {
                        MemeItMemes.getInstance().removeLikeComment(comment.getCommentID(), generateOnComplete(LIKE,REMOVE,getItem_position()));
                    } else {
                        MemeItMemes.getInstance().likeComment(comment.getCommentID(), generateOnComplete(LIKE,ADD,getItem_position()));
                    }
                    break;
                case R.id.dislike_comment:
                    if (comment.isDislikedByMe()) {
                        MemeItMemes.getInstance().removeDislikeComment(comment.getCommentID(), generateOnComplete(DISLIKE,REMOVE,getItem_position()));
                    } else {
                        MemeItMemes.getInstance().dislikeComment(comment.getCommentID(), generateOnComplete(DISLIKE,ADD,getItem_position()));
                    }
                    break;
                case R.id.delete:
                    new MaterialDialog.Builder(getMContext())
                            .title("Delete comment?")
                            .positiveText("Yes")
                            .negativeText("No")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    MemeItMemes.getInstance().deleteComment(comment.getMemeID(), comment.getCommentID(), new OnCompleteListener<ResponseBody>() {
                                        @Override
                                        public void onSuccess(ResponseBody responseBody) {
                                            Toast.makeText(getMContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                                            remove(comment);
                                        }

                                        @Override
                                        public void onFailure(Error error) {
                                            Toast.makeText(getMContext(), "An error has occured. Please try again.", Toast.LENGTH_SHORT).show();
                                            Log.w("Deleting  comment", error.getMessage());
                                        }
                                    });
                                }
                            }).show();
                    break;
                case R.id.edit:

                    new MaterialDialog.Builder(getMContext())
                            .title("Edit comment")
                            .input("Comment", comment.getComment(), false, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, final CharSequence input) {
                                    if (TextUtils.isEmpty(input))
                                        return;
                                    MemeItMemes.getInstance().editComment(comment.getCommentID(), input.toString(), new OnCompleteListener<ResponseBody>() {
                                        @Override
                                        public void onSuccess(ResponseBody o) {
                                            Toast.makeText(getMContext(), "Edited.", Toast.LENGTH_SHORT).show();
                                            comment.setComment(input.toString());
                                            commentV.setText(input.toString());
                                        }

                                        @Override
                                        public void onFailure(Error error) {
                                            Toast.makeText(getMContext(), "Editing has failed because of a problem. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).show();
                    break;

            }
        }
    }
    public OnCompleteListener generateOnComplete(final int type, final int addOrRemove, final int position){
        return new OnCompleteListener() {
            @Override
            public void onSuccess(Object o) {
                Comment comment = getItemAt(position);
                if(type == LIKE){
                    comment.setLikedByMe(addOrRemove == ADD);
                    comment.setLikeCount(addOrRemove == ADD
                            ? comment.getLikeCount() + 1
                            : comment.getLikeCount() - 1);
                    comment.setDislikeCount(addOrRemove == ADD
                            ? comment.getDislikeCount() - 1
                            : comment.getDislikeCount());
                }
                else if(type == DISLIKE){
                    comment.setDislikedByMe(addOrRemove == ADD);
                    comment.setDislikeCount(addOrRemove == ADD
                            ? comment.getDislikeCount() + 1
                            : comment.getDislikeCount() - 1);
                    comment.setLikeCount(addOrRemove == ADD
                            ? comment.getLikeCount() - 1
                            : comment.getLikeCount());
                }
                else throw new InvalidParameterException("Parameter \"Type\" must be either LIKE or DISLIKE");
                notifyItemChanged(position);
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getMContext(),"An error has occured",Toast.LENGTH_SHORT).show();
            }
        };
    }
}
