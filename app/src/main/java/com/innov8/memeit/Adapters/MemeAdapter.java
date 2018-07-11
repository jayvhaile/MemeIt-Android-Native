package com.innov8.memeit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;
import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.Activities.CommentsActivity;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Jv on 6/16/2018.
 */

public class MemeAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final String TAG = "MemeAdapter";

    private static final int MEME_TYPE=0;
    private static final int LOADING_TYPE=1;
    private Context mContext;
    private List<Meme> memes;
    private LayoutInflater mInflater;
    private boolean isLoading;
    public MemeAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        memes = new ArrayList<>();
    }



    @NonNull
    @Override
    public ViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==LOADING_TYPE)
            return new LoadingViewHolder(mInflater.inflate(R.layout.item_list_meme_loading,parent,false));

        View view = mInflater.inflate(R.layout.list_item_meme, parent, false);
        return new MemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (getItemViewType(position)==MEME_TYPE)
            ((MemeViewHolder)holder).bindMeme(memes.get(position));

    }

    @Override
    public int getItemCount() {
        return memes.size()+(isLoading()?1:0);
    }

    @Override
    public int getItemViewType(int position) {
        return position>=memes.size()?LOADING_TYPE:MEME_TYPE;
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
    public void updateMeme(Meme meme){
        int index=memes.indexOf(meme);
        memes.set(index,meme);
        notifyItemChanged(index);
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

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
        if(isLoading)
            notifyItemInserted(memes.size());
        else
            notifyItemRemoved(memes.size());
    }
    private Meme getMemeByID(String mid){
        for (Meme meme:memes) {
            if(meme.getMemeId().equals(mid))
                return meme;
        }
        return null;
    }

    protected class MemeViewHolder extends ViewHolder {
        private final SimpleDraweeView posterPicV;
        private final SimpleDraweeView memeImageV;
        private final ImageButton commentBtnV;
        private final TextView posterNameV;
        private final TextView reactionCountV;
        private final TextView commentCountV;
        private final ImageButton meme_menu;
        private String memeId;
        private OnCompleteListener<ResponseBody> reactCompletedListener;

        public MemeViewHolder(View itemView) {
            super(itemView);
            meme_menu = itemView.findViewById(R.id.meme_options);
            posterPicV = itemView.findViewById(R.id.meme_poster_pp);
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


        public void bindMeme(Meme meme) {
            memeId = meme.getMemeId();
            posterNameV.setText(meme.getPoster().getName());
            reactionCountV.setText(String.format("%d people reacted", meme.getReactionCount()));
            commentCountV.setText(String.valueOf(meme.getCommentCount()));
            ImageUtils.loadImageFromCloudinaryTo(posterPicV, meme.getPoster().getProfileUrl());
            ImageUtils.loadImageFromCloudinaryTo(memeImageV, meme.getMemeImageUrl());
        }

        private void showMemeMenu() {
            PopupMenu menu = new PopupMenu(mContext, meme_menu);
            menu.getMenuInflater().inflate(R.menu.meme_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_delete_meme:
                            MemeItMemes.getInstance().deleteMeme(memeId, new OnCompleteListener<ResponseBody>() {
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
                            });
                            return true;

                    }
                    return false;
                }
            });
            menu.show();
        }
    }
    protected class LoadingViewHolder extends ViewHolder{
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

}
