package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;


/**
 * Created by Jv on 6/16/2018.
 */

public abstract class MemeAdapter extends RecyclerView.Adapter<MyViewHolder<Meme>> {
    private static final String TAG = "MemeAdapter";

    private static final int MEME_TYPE=0;
    private static final int LOADING_TYPE=1;
    protected Context mContext;
    protected List<Meme> memes;
    protected LayoutInflater mInflater;
    int screen_width;
    public MemeAdapter(Context mContext) {
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
    public static class Listed extends MemeAdapter{
        private boolean isLoading;

        public Listed(Context mContext) {
            super(mContext);
        }

        @NonNull
        @Override
        public MyViewHolder<Meme>  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType==LOADING_TYPE)
                return new MemeAdapter.LoadingViewHolder(mInflater.inflate(R.layout.item_list_meme_loading,parent,false));


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
    public static class Grid extends MemeAdapter{

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
        private SparkButton reactButton;
        private Group reactGroup;
        private String memeId;
        private OnCompleteListener reactCompletedListener;

        public MemeListViewHolder(final View itemView) {
            super(itemView);
            meme_menu = itemView.findViewById(R.id.meme_options);
            posterPicV = itemView.findViewById(R.id.follower_poster_pp);
            memeImageV = itemView.findViewById(R.id.meme_image);
            commentBtnV = itemView.findViewById(R.id.meme_comment);
            posterNameV = itemView.findViewById(R.id.meme_poster_name);
            reactionCountV = itemView.findViewById(R.id.meme_reactions);
            commentCountV = itemView.findViewById(R.id.meme_comment_count);
            reactButton = itemView.findViewById(R.id.react_button);
            reactGroup = itemView.findViewById(R.id.react_group);
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

            final View.OnClickListener reactListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Reaction.ReactionType reactionType = null;
                    int reactRes = 0;
                    switch (view.getId()){
                        case R.id.react_funny :
                            reactionType = Reaction.ReactionType.FUNNY;
                            reactRes = R.mipmap.laughing;
                            break;
                        case R.id.react_veryfunny:
                            reactionType = Reaction.ReactionType.VERY_FUNNY;
                            reactRes = R.mipmap.rofl;
                            break;
                        case R.id.react_stupid:
                            reactionType = Reaction.ReactionType.STUPID;
                            reactRes = R.mipmap.neutral;
                            break;
                        case R.id.react_angry:
                            reactionType = Reaction.ReactionType.ANGERING;
                            reactRes = R.mipmap.angry;
                            break;
                    }
                    final int finalReactRes = reactRes;
                    react(reactionType, finalReactRes);
                    toggleReactVisibility();
                }
            };
            itemView.findViewById(R.id.react_funny).setOnClickListener(reactListener);
            itemView.findViewById(R.id.react_veryfunny).setOnClickListener(reactListener);
            itemView.findViewById(R.id.react_stupid).setOnClickListener(reactListener);
            itemView.findViewById(R.id.react_angry).setOnClickListener(reactListener);
            /*reactButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(mContext, "long", Toast.LENGTH_SHORT).show();
                    toggleReactVisibility();
                    return true;
                }
            });*/

            reactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleReactVisibility();
                }
            });
            meme_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMemeMenu();
                }
            });
        }

        private void react(Reaction.ReactionType reactionType, final int finalReactRes) {
            MemeItMemes.getInstance().reactToMeme(Reaction.create(reactionType,memeId), new OnCompleteListener<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody responseBody) {
                    reactButton.setActiveImage(finalReactRes);
                    reactButton.playAnimation();
                }

                @Override
                public void onFailure(Error error) {
                    Log.w("react",error.getMessage());
                    Toast.makeText(mContext, "reaction failed\n"+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void toggleReactVisibility() {
            final int v=reactGroup.getVisibility()== View.VISIBLE?View.GONE:View.VISIBLE;
            reactGroup.setVisibility(v);
        }

        public void bind(Meme meme) {
            reactGroup.setVisibility(View.GONE);
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
            memeImageV.getLayoutParams().width=width;
            memeImageV.getLayoutParams().height=height;
            memeImageV.requestLayout();
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
