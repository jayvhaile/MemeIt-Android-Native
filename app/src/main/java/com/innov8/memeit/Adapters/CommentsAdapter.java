package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.utilis.OnCompleteListener;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import okhttp3.ResponseBody;
import static java.lang.Math.toIntExact;

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
        private TextView like;
        private TextView dislike;
        Group ownCommentViews;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentV=itemView.findViewById(R.id.list_comment);
            dateV=itemView.findViewById(R.id.list_item_date);
            posterPicV=itemView.findViewById(R.id.comment_poster_pp);
            posterNameV=itemView.findViewById(R.id.list_name);
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
        }
        
        public void bind(final Comment mComment){
            final MemeItMemes memeItMemes = MemeItMemes.getInstance();
            Boolean isOwnComment = MemeItUsers.getInstance().getMyUser(getMContext()).getUserID().equals(mComment.getPosterID());
            commentV.setText(mComment.getComment());
            ImageUtils.loadImageFromCloudinaryTo(posterPicV,mComment.getPoster().getProfileUrl());
            posterNameV.setText(mComment.getPoster().getName());
            dateV.setText(CustomMethods.convertDate(mComment.getDate()));
            final int[] likeCount = {new BigDecimal(mComment.getLikeCount()).intValueExact()};
            final int[] dislikeCount = {new BigDecimal(mComment.getDislikeCount()).intValueExact()};
            like.setText(likeCount[0] + "");
            dislike.setText(dislikeCount[0] + "");

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        //============================ DELETE ====================================//
                        case R.id.delete:
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
                            break;
                        //============================ EDIT ====================================//
                        case R.id.edit:
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
                            break;
                        //============================ LIKE ====================================//
                        case R.id.like_comment:
                            if(!mComment.isLikedByMe() && !mComment.isDislikedByMe()){
                                memeItMemes.likeComment(mComment.getCommentID(), generateOnComplete(like,true));
                                likeCount[0]++;
                                like.setText(convertNumber(likeCount[0]));
                            }
                            else if(!mComment.isLikedByMe() && mComment.isDislikedByMe()){
                                memeItMemes.removeDislikeComment(mComment.getCommentID(), generateOnComplete(like,true));
                                memeItMemes.likeComment(mComment.getCommentID(),generateOnComplete(like,true));
                                dislikeCount[0]--;
                                likeCount[0]++;
                                like.setText(convertNumber(likeCount[0]));
                                dislike.setText(convertNumber(dislikeCount[0]));
                            }
                            else if(mComment.isLikedByMe() && !mComment.isDislikedByMe()){
                                memeItMemes.removeLikeComment(mComment.getCommentID(), generateOnComplete(like,false));
                                likeCount[0]--;
                                like.setText(convertNumber(likeCount[0]));
                            }
                            break;
                        //============================ DISLIKE ====================================//
                        case R.id.dislike_comment:
                            if(!mComment.isLikedByMe() && !mComment.isDislikedByMe()){
                                memeItMemes.dislikeComment(mComment.getCommentID(), generateOnComplete(dislike,true));
                                dislikeCount[0]++;
                                dislike.setText(convertNumber(dislikeCount[0]));
                            }
                            else if(mComment.isLikedByMe() && !mComment.isDislikedByMe()){
                                memeItMemes.removeLikeComment(mComment.getCommentID(), generateOnComplete(dislike,true));
                                memeItMemes.dislikeComment(mComment.getCommentID(),generateOnComplete(dislike,true));
                                likeCount[0]--;
                                dislikeCount[0]++;
                                like.setText(convertNumber(likeCount[0]));
                                dislike.setText(convertNumber(dislikeCount[0]));
                            }
                            else if(!mComment.isLikedByMe() && mComment.isDislikedByMe()){
                                memeItMemes.removeDislikeComment(mComment.getCommentID(), generateOnComplete(like,false));
                                dislikeCount[0]--;
                                dislike.setText(convertNumber(dislikeCount[0]));
                            }
                            break;
                        case R.id.share_comment:
                            break;
                    }
                }
            };
            if(isOwnComment) {
                delete.setOnClickListener(onClickListener);
                edit.setOnClickListener(onClickListener);
            }
            like.setOnClickListener(onClickListener);
            dislike.setOnClickListener(onClickListener);
        }
    }
    public OnCompleteListener generateOnComplete(final TextView textView, final boolean activate){
        return new OnCompleteListener() {
            @Override
            public void onSuccess(Object o) {
                setTextViewDrawableColor(textView,R.color.colorAccent);
                textView.setTextColor(activate
                        ? getMContext().getResources().getColor(R.color.colorAccent)
                        : getMContext().getResources().getColor(R.color.like_bg));
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getMContext(),"An error has occured.",Toast.LENGTH_SHORT).show();
            }
        };
    }
    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(getMContext().getResources().getColor(color), PorterDuff.Mode.SRC_IN));
            }
        }
    }
    public boolean isPostingComment() {
        return isPostingComment;
    }

    public void setPostingComment(boolean postingComment) {
        isPostingComment = postingComment;
    }
    public String convertNumber(int number){
        if(number>=1000 && number < 1000000){
            return (number/1000) + "K";
        }
        else if(number<1000){
            return number + "";
        }
        else {
            return (number/1000000) + "M";
        }
    }
}
