package com.innov8.memeit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.CustomClasses.Notification;
import com.innov8.memeit.R;
import com.memeit.backend.dataclasses.Reaction;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    Context context;
    ArrayList<Notification> notifications = new ArrayList<>();

    int day = 0;

    public static final int NORMAL_NOTIFICATION = 0;
    public static final int DIVIDER = -1;

    public NotificationsAdapter(Context c) {
        context = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_notification,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*Notification currentNotif = notifications.get(position);
        switch (currentNotif.getType()){
            case COMMENT:
                holder.notificationTitle.setText(String.format("%s has commented on your meme.", currentNotif.getCommenter()));
                holder.notificationImage.setImageURI(); //todo jv: find a way to get pp uri of person from their id
                holder.notificationSecondImage.setImageURI(); //todo: same here
            case REACT:
                holder.notificationTitle.setText(String.format("%s has reacted to your meme.", currentNotif.getReacter()));
                holder.notificationImage.setImageURI(); //todo jv: find a way to get pp uri of person from their id
                holder.notificationSecondImage.setImageResource(getReaction(currentNotif.getReactionType()));
                break;
            case FOLLOW:
                holder.notificationTitle.setText(String.format("%s has started following you.", currentNotif.getFollower()));
                holder.notificationImage.setImageURI(); //todo jv: find a way to get pp uri of person from their id
                break;
            case GENERAL:
                holder.notificationTitle.setText(currentNotif.getGeneralTitle());
                holder.notificationImage.setImageResource(R.mipmap.app_icon_temp);
                break;
            case BADGE:
                holder.notificationTitle.setText("You have just been rewarded the " + currentNotif.getBadgeType() + " badge.");
                holder.notificationImage.setImageResource(); // todo add badge icon here
                break;
        }
        holder.notificationTime.setText(currentNotif.getType() != Notification.Type.GENERAL ? CustomMethods.convertDate(currentNotif.getTime()) : currentNotif.getGeneralDetail());
        holder.notificationDot.setColorFilter(getDotColor(currentNotif.getType()));*/
    }

    @Override
    public int getItemViewType(int position) {
        int type = NORMAL_NOTIFICATION;

        return type;
    }

    public int getReaction(Reaction.ReactionType reaction){
       /* switch (reaction){
            case FUNNY:
                return R.mipmap.laughing;
            case VERY_FUNNY:
                return R.mipmap.rofl;
            case STUPID:
                return R.mipmap.neutral;
            case ANGERING:
                return R.mipmap.angry;
            default: return R.mipmap.laughing;
        }*/
       return 0;
    }



    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView notificationImage;
        SimpleDraweeView notificationSecondImage;
        TextView notificationTitle;
        TextView notificationTime;
        ImageView notificationDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationDot = itemView.findViewById(R.id.notification_dot);
            notificationImage = itemView.findViewById(R.id.notification_image);
            notificationSecondImage = itemView.findViewById(R.id.notification_second_image);
            notificationTime = itemView.findViewById(R.id.notification_time);
            notificationTitle = itemView.findViewById(R.id.notification_title);
        }
    }
    public void setData(ArrayList<Notification> notifications){
        this.notifications = notifications;
    }
    public int getDotColor(Notification.Type type){
        int color;
        switch (type){
            case BADGE:color = context.getResources().getColor(R.color.green);
            case GENERAL:color = context.getResources().getColor(R.color.blue);
            case FOLLOW:color = context.getResources().getColor(R.color.red);
            case COMMENT:color = context.getResources().getColor(R.color.purple);
            case REACT:color = context.getResources().getColor(R.color.orange);
            default: color = context.getResources().getColor(R.color.red);
        }
        return color;
    }
}
