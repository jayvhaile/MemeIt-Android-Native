package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memegenerator.adapters.MyViewHolder;
import com.innov8.memeit.Activities.CommentsActivity;
import com.innov8.memeit.Activities.ProfileActivity;
import com.innov8.memeit.CustomClasses.CustomMethods;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Jv on 6/16/2018.
 */

public abstract class MemeAdapter2 extends RecyclerView.Adapter<MyViewHolder<Meme>> {
    private static final String TAG = "MemeAdapter";

    private static final int MEME_TYPE=0;
    private static final int LOADING_TYPE=1;
    protected Context mContext;
    protected List<Meme> memes;
    protected LayoutInflater mInflater;
    int screen_width;
    public MemeAdapter2(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        memes = new ArrayList<>();
        screen_width = mContext.getResources().getDisplayMetrics().widthPixels;
    }
    public void addAll(List<Meme> memes) {
        if(memes.size()==0)return;
        int start = this.memes.size();
        this.memes.addAll(memes);
        notifyItemRangeInserted(start, memes.size());
    }
    public void add(Meme meme) {
        memes.add(meme);
        notifyItemInserted(memes.size() - 1);
    }
    public void remove(Meme meme) {
        if (memes.contains(meme)) {
            int index = memes.indexOf(meme);
            memes.remove(meme);
            notifyItemRemoved(index);
        }
    }
    public void clear() {
        memes.clear();
        notifyDataSetChanged();
    }
    public void setAll(List<Meme> memes) {
        this.memes.clear();
        this.memes.addAll(memes);
        notifyDataSetChanged();
    }
    public void updateMeme(Meme meme){
        int index=memes.indexOf(meme);
        memes.set(index,meme);
        notifyItemChanged(index);
    }
    protected Meme getMemeByID(String mid){
        for (Meme meme:memes) {
            if(meme.getMemeId().equals(mid))
                return meme;
        }

        return null;
    }
    public abstract boolean isLoading();
    public abstract void setLoading(boolean loading);
    public abstract RecyclerView.LayoutManager createlayoutManager();
    public static class Listed extends MemeAdapter2 {
        private boolean isLoading;

        public Listed(Context mContext) {
            super(mContext);
        }

        @NonNull
        @Override
        public MyViewHolder<Meme>  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType==LOADING_TYPE)
                return new LoadingViewHolder(mInflater.inflate(R.layout.item_list_meme_loading,parent,false));

            View view = mInflater.inflate(R.layout.list_item_meme, parent, false);
            return new MemeListViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (getItemViewType(position)==MEME_TYPE)
                holder.bind(memes.get(position));

        }

        @Override
        public int getItemCount() {
            return memes.size()+(isLoading()?1:0);
        }

        @Override
        public int getItemViewType(int position) {
            return position>=memes.size()?LOADING_TYPE:MEME_TYPE;
        }
        @Override
        public boolean isLoading() {
            return isLoading;
        }
        @Override
        public void setLoading(boolean loading) {
            isLoading = loading;
            if(isLoading)
                notifyItemInserted(memes.size());
            else
                notifyItemRemoved(memes.size());
        }

        @Override
        public RecyclerView.LayoutManager createlayoutManager() {
            LinearLayoutManager lm=new LinearLayoutManager(mContext,RecyclerView.VERTICAL,false);
            return lm;
        }

    }
    public static class Grid extends MemeAdapter2 {

        public Grid(Context mContext) {
            super(mContext);
        }

        @Override
        public boolean isLoading() {
            return false;
        }

        @Override
        public void setLoading(boolean loading) {

        }
        @Override
        public RecyclerView.LayoutManager createlayoutManager() {
            GridLayoutManager glm=new GridLayoutManager(mContext,3,RecyclerView.VERTICAL,false);
            return glm;
        }
        @NonNull
        @Override
        public MyViewHolder<Meme> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.list_item_meme_grid, parent, false);
            return new MemeGridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder<Meme> holder, int position) {
            holder.bind(memes.get(position));
        }

        @Override
        public int getItemCount() {
            return memes.size();
        }
    }
    public class MemeListViewHolder extends MyViewHolder<Meme> {
        private final SimpleDraweeView posterPicV;
        private final SimpleDraweeView memeImageV;
        private LinearLayout holder;
        private final ImageButton commentBtnV;
        private final TextView posterNameV;
        private final TextView reactionCountV;
        private final TextView commentCountV;
        private final ImageButton meme_menu;
        private String memeId;
        private OnCompleteListener reactCompletedListener;

        public MemeListViewHolder(View itemView) {
            super(itemView);
            meme_menu = itemView.findViewById(R.id.meme_options);
            posterPicV = itemView.findViewById(R.id.follower_poster_pp);
            memeImageV = itemView.findViewById(R.id.meme_image);
            commentBtnV = itemView.findViewById(R.id.meme_comment);
            posterNameV = itemView.findViewById(R.id.meme_poster_name);
            reactionCountV = itemView.findViewById(R.id.meme_reactions);
            commentCountV = itemView.findViewById(R.id.meme_comment_count);
            commentBtnV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Meme meme=getMemeByID(memeId);
                    if(meme==null)
                        throw new IllegalStateException("Meme Should not be null");
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra(CommentsActivity.MEME_PARAM_KEY, meme);
                    mContext.startActivity(intent);
                }
            });
            posterPicV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(mContext, ProfileActivity.class);
                    i.putExtra("uid",getMemeByID(memeId).getPoster().getID());
                    mContext.startActivity(i);
                }
            });
            meme_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMemeMenu();
                }
            });
            setupReactions(itemView);
            reactCompletedListener=new OnCompleteListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(mContext, "Reacted", Toast.LENGTH_SHORT).show();
                    refreshMeme();
                }

                @Override
                public void onFailure(Error error) {
                    Toast.makeText(mContext, "reaction failed"+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
        }
        public void bind(Meme meme) {
            memeId = meme.getMemeId();
            posterNameV.setText(meme.getPoster().getName());
            reactionCountV.setText(String.format("%d people reacted", meme.getReactionCount()));
            commentCountV.setText(String.valueOf(meme.getCommentCount()));
            ImageUtils.loadImageFromCloudinaryTo(posterPicV, meme.getPoster().getProfileUrl());
            adjust(meme.getMemeImageRatio());
            ImageUtils.loadImageFromCloudinaryTo(memeImageV, meme.getMemeImageUrl());
        }


        private void adjust( double ratio){
            int width=screen_width;
            int height= (int) (width/ratio);
            int max_height= (int) CustomMethods.convertDPtoPX(mContext,500.0f);
            int min_height= (int) CustomMethods.convertDPtoPX(mContext,200.0f);
            height=height<min_height?min_height:height>max_height?max_height:height;
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(width,height);
            memeImageV.setLayoutParams(params);
        }

        private void refreshMeme(){
            Meme  meme=getMemeByID(memeId);
            MemeItMemes.getInstance().getRefreshedMeme(meme, new OnCompleteListener<Meme>() {
                @Override
                public void onSuccess(Meme meme) {
                    updateMeme(meme);
                }

                @Override
                public void onFailure(Error error) {

                }
            });
        }
        public void setupReactions(View itemView) {
            final int[] ITEM_DRAWABLES = {
                    R.mipmap.laughing,
                    R.mipmap.rofl,
                    R.mipmap.neutral,
                    R.mipmap.angry};

            final String[] STR = { "Funny","Very funny", "Stupid", "Triggering"};

            final ArcMenu menu = itemView.findViewById(R.id.arcMenu);
            menu.showTooltip(true);
            menu.setToolTipBackColor(Color.WHITE);
            menu.setToolTipCorner(6f);
            menu.setToolTipPadding(2f);
            menu.setToolTipTextColor(Color.BLUE);
            menu.setAnim(300, 300, ArcMenu.ANIM_MIDDLE_TO_RIGHT, ArcMenu.ANIM_MIDDLE_TO_RIGHT,
                    ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE, ArcMenu.ANIM_INTERPOLATOR_ACCELERATE_DECLERATE);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    react(v);
                }
            };
            final int itemCount = ITEM_DRAWABLES.length;
            for (int i = 0; i < itemCount; i++) {
                FloatingActionButton item = new FloatingActionButton(mContext);
                item.setSize(FloatingActionButton.SIZE_MINI);
                item.setIcon(ITEM_DRAWABLES[i]);
                item.setBackgroundColor(Color.WHITE);
                menu.setChildSize(item.getIntrinsicHeight()); // set absolute child size for menu
                menu.addItem(item, STR[i],listener);
                item.setTag(i);
            }
        }

        private void react(View v) {
            int no= Integer.parseInt(String.valueOf(v.getTag()));
            Reaction reaction=Reaction.create(Reaction.ReactionType.values()[no], memeId);
            MemeItMemes.getInstance().reactToMeme(reaction,reactCompletedListener);
        }
        private void showMemeMenu() {
            PopupMenu menu = new PopupMenu(mContext, meme_menu);
            menu.getMenuInflater().inflate(R.menu.meme_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_delete_meme:
                            /*MemeItMemes.getInstance().deleteMeme(memeId, new OnCompleteListener<ResponseBody>() {
                                @Override
                                public void onSuccess(ResponseBody responseBody) {
                                    remove(Meme.forID(memeId));
                                    //todo show snackbar instead of toast
                                    Toast.makeText(mContext, "Meme Deleted", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Error error) {
                                    //todo show snackbar instead of toast
                                    Toast.makeText(mContext, "Cannot Delete Meme " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });*/
                            return true;

                    }
                    return false;
                }
            });
            menu.show();
        }
    }
    public class LoadingViewHolder extends MyViewHolder<Meme>{
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Meme meme) {

        }
    }
    public class MemeGridViewHolder extends MyViewHolder<Meme>{
        private final SimpleDraweeView memeImageV;
        public MemeGridViewHolder(View itemView) {
            super(itemView);
            memeImageV = itemView.findViewById(R.id.meme_image);
            int width=screen_width/3;
            FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(width,width);
            memeImageV.setLayoutParams(lp);
        }

        @Override
        public void bind(Meme meme) {
            ImageUtils.loadImageFromCloudinaryTo(memeImageV, meme.getMemeImageUrl());
        }
    }
}
