package com.innov8.memeit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memegenerator.loading_button_lib.customViews.CircularProgressButton;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;

public class FollowerListAdapter extends RecyclerView.Adapter<FollowerListAdapter.ViewHolder> {

    ArrayList<User> users = new ArrayList<>();
    Context context;

    public FollowerListAdapter(Context c){
        context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_follower,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        User currentUser = users.get(position);
        holder.followerName.setText(currentUser.getName());
        holder.followerDetail.setText(currentUser.getPostCount() + " posts");
        holder.followerImage.setImageURI(currentUser.getImageUrl());
        View.OnClickListener onClickListener;
        if(currentUser.isFollowedByMe()){
            holder.followButton.setText("Following");
            onClickListener= new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MemeItUsers.getInstance().unFollowUser(users.get(position).getUserID(), new OnCompleteListener<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            holder.followButton.setText("Follow");
                        }

                        @Override
                        public void onFailure(Error error) {
                            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            };
        }
        else{
            holder.followButton.setText("Follow");
            onClickListener= new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MemeItUsers.getInstance().followUser(users.get(position).getUserID(), new OnCompleteListener<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody responseBody) {
                            holder.followButton.setText("Following");
                        }

                        @Override
                        public void onFailure(Error error) {
                            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                        }
                    });
            }
            };
        }
        holder.followButton.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView followerImage;
        TextView followerName;
        TextView followerDetail;
        CircularProgressButton followButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            followerImage = itemView.findViewById(R.id.follower_poster_pp);
            followerName = itemView.findViewById(R.id.follower_name);
            followButton = itemView.findViewById(R.id.follower_follow_btn);
            followerDetail = itemView.findViewById(R.id.follower_detail);
        }
    }

    public void addUsers(List<User> users){
        users.addAll(users);
    }

    public void setAll(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

}
