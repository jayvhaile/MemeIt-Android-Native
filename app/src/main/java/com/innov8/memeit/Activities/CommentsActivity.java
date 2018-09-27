package com.innov8.memeit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.innov8.memeit.Adapters.CommentsAdapter;
import com.innov8.memeit.CustomViews.ProfileDraweeView;
import com.innov8.memeit.KUtilsKt;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.MemeItUsers;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.MyUser;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.utilis.OnCompleteListener;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;

import static com.innov8.memeit.KUtilsKt.*;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String MEME_PARAM_KEY= "meme";
    private static final int LIMIT = 20;
    int skip = 0;
    Meme meme;
    boolean isPostingComment;
    SimpleDraweeView memeImage;
    CommentsAdapter commentsAdapter;
    RecyclerView commentsList;
    EditText commentField;
    ImageView commentButton;
    TextView tags;
    TextView description;
    TextView commentCount;
    SparkButton reactButton;
    SparkButton favButton;
    TextView reactionCount;
    Group reactGroup;
    TextView funnyCount;
    TextView stupidCount;
    TextView angryCount;
    TextView veryFunnyCount;
    MyUser myUser;

    OnCompleteListener<Comment> onCommentCompletedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_2);
        memeImage=findViewById(R.id.meme_image_view);
        commentsList=findViewById(R.id.comments_list);
        commentField=findViewById(R.id.comment_field);
        commentButton=findViewById(R.id.comment_button);
        tags = findViewById(R.id.meme_tags);
        description = findViewById(R.id.description);
        commentCount = findViewById(R.id.meme_comment_count);
        reactionCount = findViewById(R.id.meme_reactions);
        reactButton = findViewById(R.id.react_button);
        favButton = findViewById(R.id.fav_button);
        reactGroup = findViewById(R.id.react_group);
        veryFunnyCount = findViewById(R.id.reacation_count_veryfunny);
        funnyCount = findViewById(R.id.reacation_count_funny);
        stupidCount = findViewById(R.id.reacation_count_stupid);
        angryCount = findViewById(R.id.reacation_count_angry);

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this,new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if(pendingDynamicLinkData!=null){
                            String memeId = pendingDynamicLinkData.getLink().getQueryParameter("memeid");
                            meme = getIntent().getParcelableExtra(MEME_PARAM_KEY); // <--  this one
                            init();
                        }
                        else{
                            meme = getIntent().getParcelableExtra(MEME_PARAM_KEY);
                            init();
                        }
                    }
                })
                .addOnFailureListener(this,new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        meme = getIntent().getParcelableExtra(MEME_PARAM_KEY);
                        init();
                    }
                })
        ;

    }

    private void load(Meme meme){
        MemeItMemes.getInstance().getCommentsForMeme(meme.getMemeId(), skip,LIMIT, new OnCompleteListener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentsAdapter.addAll(comments);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Snackbar.make(findViewById(R.id.coordinators),"Something went wrong",Snackbar.LENGTH_SHORT).show();
                Log.w("getCommentsFormeme",error.getMessage());
            }
        });
        if(meme.getDescription() != null){
            description.setVisibility(View.VISIBLE);
            description.setText(meme.getDescription());
        }
        if(meme.getTags().size() > 0){
            tags.setVisibility(View.VISIBLE);
            tags.setText(generateStringForTags(meme.getTags()));
        }
        reactionCount.setText(String.format("%d%s", meme.getReactionCount(), meme.getReactionCount() > 1 ? " people reacted" : "person reacted"));
        commentCount.setText(meme.getCommentCount() + "");
        reactButton.setOnClickListener(this);
        findViewById(R.id.react_angry).setOnClickListener(this);
        findViewById(R.id.react_funny).setOnClickListener(this);
        findViewById(R.id.react_stupid).setOnClickListener(this);
        findViewById(R.id.react_veryfunny).setOnClickListener(this);

    }
    private void refresh(){
        resetSkip();
        MemeItMemes.getInstance().getCommentsForMeme(meme.getMemeId(), skip,LIMIT, new OnCompleteListener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentsAdapter.setAll(comments);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Snackbar.make(findViewById(R.id.coordinators),"Something went wrong",Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    private void resetSkip(){
        skip=0;
    }
    private void incSkip(){
        skip+=LIMIT;
    }

    public boolean isPostingComment() {
        return isPostingComment;
    }

    public void setPostingComment(boolean postingComment) {
        isPostingComment = postingComment;
    }
    public String generateStringForTags(List<String> tags){
        StringBuilder finalString = new StringBuilder();
        for(int i = 0;i<tags.size();i++)
            finalString.append("#").append(tags.get(i)).append(i != tags.size() - 1 ? ", " : "");
        return finalString.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.react_angry:
                react(Reaction.ReactionType.ANGERING);
            case R.id.react_funny:
                react(Reaction.ReactionType.FUNNY);
            case R.id.react_stupid:
                react(Reaction.ReactionType.STUPID);
            case R.id.react_veryfunny:
                react(Reaction.ReactionType.VERY_FUNNY);
            case R.id.react_button:
                reactGroup.setVisibility(reactGroup.getVisibility()==View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.comment_button:
                String txt=commentField.getText().toString();
                if(isPostingComment()||TextUtils.isEmpty(txt))return;
                Comment comment=Comment.createComment(meme.getMemeId(),txt);
                setPostingComment(true);
                MemeItMemes.getInstance().comment(comment, onCommentCompletedListener);
                break;
            case R.id.signup:
                startActivity(new Intent(this,AuthActivity.class));
            case R.id.fav_button:
                MemeItMemes.getInstance().addToFavourites(meme.getMemeId(), new OnCompleteListener<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        Snackbar.make(findViewById(R.id.coordinators),"Added to favorites!",Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Error error) {
                        Snackbar.make(findViewById(R.id.coordinators),"Couldn't add to favorites",Snackbar.LENGTH_SHORT).show();
                        Log.w("fav",error.getMessage());
                    }
                });
        }
    }
    public void react(final Reaction.ReactionType reactionType){
        if(myUser!=null)
        MemeItMemes.getInstance().reactToMeme(Reaction.create(reactionType, meme.getMemeId()), new OnCompleteListener<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                reactGroup.setVisibility(View.GONE);
                reactButton.setActiveImage(getReactionDrawable(reactionType));
                reactButton.setChecked(true);
            }

            @Override
            public void onFailure(Error error) {
                reactGroup.setVisibility(View.GONE);
                Snackbar.make(findViewById(R.id.coordinators),"Something went wrong.",Snackbar.LENGTH_SHORT).show();
            }
        });
        else Snackbar.make(findViewById(R.id.coordinators),"You need to login first.",Snackbar.LENGTH_SHORT).show();

    }
    public @IdRes int getReactionDrawable(Reaction.ReactionType reactionType){
        @IdRes int reactionDrawable = 0;
        switch (reactionType){
            case FUNNY:reactionDrawable = R.drawable.laughing;
            case STUPID: reactionDrawable = R.drawable.neutral;
            case ANGERING: reactionDrawable = R.drawable.angry;
            case VERY_FUNNY: reactionDrawable = R.drawable.rofl;
        }
        return reactionDrawable;
    }
    public void init(){
        commentsAdapter=new CommentsAdapter(this);
        LinearLayoutManager llm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        commentsList.setLayoutManager(llm);
        commentsList.setItemAnimator(new DefaultItemAnimator());
        commentsList.setAdapter(commentsAdapter);
        onCommentCompletedListener = new OnCompleteListener<Comment>() {
            @Override
            public void onSuccess(Comment comment) {
                commentField.setText("");
                refresh();
                setPostingComment(false);
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(CommentsActivity.this, "Comment failed " + error.getMessage(), Toast.LENGTH_SHORT).show();
                setPostingComment(false);
            }
        };
        commentButton.setOnClickListener(this);
        KUtilsKt.loadMeme(memeImage,meme,0,0);
        ProfileDraweeView pdv=findViewById(R.id.comment_pp);
        myUser = MemeItUsers.getInstance().getMyUser(this);
        pdv.setText(KUtilsKt.prefix(myUser.getName()));
        float size=dimen(R.dimen.profile_mini_size);
        loadImage(pdv,myUser.getImageUrl(),size,size);
        if(myUser==null){
            findViewById(R.id.comment_group).setVisibility(View.GONE);
            findViewById(R.id.signup).setVisibility(View.VISIBLE);
            findViewById(R.id.signup).setOnClickListener(this);
        }
        load(meme);
    }
}
