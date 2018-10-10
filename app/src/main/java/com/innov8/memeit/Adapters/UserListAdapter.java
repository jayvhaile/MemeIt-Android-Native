package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.innov8.memegenerator.Adapters.ListAdapter;
import com.innov8.memegenerator.Adapters.MyViewHolder;
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton;
import com.innov8.memegenerator.loading_button_lib.interfaces.OnAnimationEndListener;
import com.innov8.memeit.Activities.ProfileActivity;
import com.innov8.memeit.CustomClasses.FontTextView;
import com.innov8.memeit.CustomViews.ProfileDraweeView;
import com.innov8.memeit.KUtilsKt;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItClient;
import com.memeit.backend.dataclasses.MUser;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.OnCompleted;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import okhttp3.ResponseBody;

public class UserListAdapter extends ListAdapter<User> {
    private Typeface tf;
    private MUser myUser;
    float size;
    public UserListAdapter(@NotNull Context mContext) {
        super(mContext, R.layout.list_item_follower);
        tf = Typeface.createFromAsset(getMContext().getAssets(), FontTextView.asset);
        myUser= MemeItClient.INSTANCE.getMyUser();
        size=mContext.getResources().getDimension(R.dimen.profile_mini_size);
    }

    @NotNull
    @Override
    public MyViewHolder<User> createViewHolder(@NotNull View view) {
        return new UserListViewHolder(view);
    }
    private boolean isMe(String id){
        return myUser.getId().equals(id);
    }

    public class UserListViewHolder extends MyViewHolder<User> {
        ProfileDraweeView followerImage;
        TextView followerName;
        TextView followerDetail;
        CircularProgressButton followButton;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            followerImage = itemView.findViewById(R.id.notif_icon);
            followerName = itemView.findViewById(R.id.follower_name);
            followButton = itemView.findViewById(R.id.follower_follow_btn);
            followerDetail = itemView.findViewById(R.id.follower_detail);
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String t = followButton.getText().toString();
                    followButton.startAnimation();
                    User user = getItemAt(getItem_position());
                    if (t.equalsIgnoreCase("unfollow")) {
                        MemeItUsers.INSTANCE.unfollowUser(user.getUid()).enqueue(new OnCompleted<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                followButton.revertAnimation(new OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        followButton.setText("Follow");
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                followButton.revertAnimation();
                                Toast.makeText(getMContext(), "Error " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        MemeItUsers.INSTANCE.followUser(user.getUid()).enqueue(new OnCompleted<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                followButton.revertAnimation(new OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        followButton.setText("Unfollow");
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                followButton.revertAnimation();
                                Toast.makeText(getMContext(), "Error " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getMContext(), ProfileActivity.class);
                    i.putExtra("user", getItemAt(getItem_position()));
                    getMContext().startActivity(i);
                }
            });
            followButton.setTypeface(tf);
        }

        public void bind(User user) {
            followerName.setText(user.getName());
            followerDetail.setText(user.getPostCount() + " posts");
            followerImage.setText(KUtilsKt.prefix(user.getName()));
            KUtilsKt.loadImage(followerImage,user.getImageUrl(),size,size);
            if(isMe(user.getUid())){
                followButton.setVisibility(View.GONE);
            }else{
                followButton.setVisibility(View.VISIBLE);
                if (user.isFollowedByMe()) {
                    followButton.setText("Unfollow");
                } else {
                    followButton.setText("Follow");
                }
            }


        }
    }

}
