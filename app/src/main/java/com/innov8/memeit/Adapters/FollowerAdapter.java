package com.innov8.memeit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memegenerator.adapters.MyViewHolder;
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Jv on 7/15/2018.
 */

public class FollowerAdapter extends RecyclerView.Adapter<MyViewHolder<User>> {
    protected Context mContext;
    protected List<User> users;
    protected LayoutInflater mInflater;
    int screen_width;

    public FollowerAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        users = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder<User> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_follower, parent, false);
        CircularProgressButton followButton = view.findViewById(R.id.follower_follow_btn);

        return new FollowerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder<User> holder, int position) {
        holder.bind(users.get(position));
        CircularProgressButton followButton = holder.itemView.findViewById(R.id.follower_follow_btn);
        if(users.get(position).isFollowedByMe()) {
            followButton.setText("Following");
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void addAll(List<User> users) {
        if(users.size()==0)return;
        int start = this.users.size();
        this.users.addAll(users);
        notifyItemRangeInserted(start, users.size());
    }

    public void add(User user) {
        users.add(user);
        notifyItemInserted(users.size() - 1);
    }


    public void remove(User user) {
        if (users.contains(user)) {
            int index = users.indexOf(user);
            users.remove(user);
            notifyItemRemoved(index);
        }
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void setAll(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public class FollowerViewHolder extends MyViewHolder<User>{
        private final SimpleDraweeView followerppV;
        private final TextView followerName;
        private final TextView followerDetail;
        private final CircularProgressButton followBtn;
        private String userId;
        private boolean isFollowedByMe;

        public FollowerViewHolder(View itemView) {
            super(itemView);
            followerppV = itemView.findViewById(R.id.follower_poster_pp);
            followerName = itemView.findViewById(R.id.follower_name);
            followerDetail = itemView.findViewById(R.id.follower_detail);
            followBtn = itemView.findViewById(R.id.follower_follow_btn);
            followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followBtn.startAnimation();
                   /* if (isFollowedByMe)
                        MemeItUsers.getInstance().unFollowUser(userId, FollowerViewHolder.this);
                    else
                        MemeItUsers.getInstance().followUser(userId, FollowerViewHolder.this);*/

                }
            });

        }

        @Override
        public void bind(User user) {
            this.userId = user.getUserID();
            this.isFollowedByMe = user.isFollowedByMe();
            followerName.setText(user.getName());
            followerDetail.setText(CustomMethods.formatNumber(user.getPostCount(), "posts"));
            ImageUtils.loadImageFromCloudinaryTo(followerppV, user.getImageUrl());
            followBtn.setText(isFollowedByMe ? "Unfollow" : "Follow");
        }

        /*@Override
        public void onSuccess(ResponseBody responseBody) {
            isFollowedByMe=!isFollowedByMe;
            followBtn.revertAnimation(new OnAnimationEndListener() {
                @Override
                public void onAnimationEnd() {
                    Toast.makeText(mContext, "end", Toast.LENGTH_SHORT).show();
                    followBtn.setText(isFollowedByMe?"Unfollow":"Follow");
                }
            });
        }

        @Override
        public void onFailure(Error error) {
            followBtn.revertAnimation();
        }*/
    }
}
