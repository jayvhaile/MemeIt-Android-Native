package com.innov8.memeit.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.innov8.memeit.Adapters.CommentsAdapter;
import com.innov8.memeit.CustomClasses.ImageUtils;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsActivity extends AppCompatActivity {
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
    OnCompleteListener<Comment> onCommentCompletedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_2);
        memeImage=findViewById(R.id.meme_image);
        commentsList=findViewById(R.id.comments_list);
        commentField=findViewById(R.id.comment_field);
        commentButton=findViewById(R.id.comment_button);
        meme = getIntent().getParcelableExtra(MEME_PARAM_KEY);
        if(meme==null)
            throw new NullPointerException("Meme Must be passed to CommentActivity");
        commentsAdapter=new CommentsAdapter(this);
        LinearLayoutManager llm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        commentsList.setLayoutManager(llm);
        commentsList.setItemAnimator(new DefaultItemAnimator());
        commentsList.setAdapter(commentsAdapter);
        onCommentCompletedListener = new OnCompleteListener<Comment>() {
            @Override
            public void onSuccess(Comment comment) {
                Toast.makeText(CommentsActivity.this, "Comment Saved", Toast.LENGTH_SHORT).show();
                commentField.setText("");
                refresh();
                setPostingComment(false);
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(CommentsActivity.this, "comment failed " + error.getMessage(), Toast.LENGTH_SHORT).show();
                setPostingComment(false);
            }
        };
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt=commentField.getText().toString();
                if(isPostingComment()||TextUtils.isEmpty(txt))return;
                Comment comment=Comment.createComment(meme.getMemeId(),txt);
                setPostingComment(true);
                MemeItMemes.getInstance().comment(comment, onCommentCompletedListener);
            }
        });
        ImageUtils.loadImageFromCloudinaryTo(memeImage,meme.getMemeImageUrl());
        load();

    }

    private void load(){
        MemeItMemes.getInstance().getCommentsForMeme(meme.getMemeId(), skip,LIMIT, new OnCompleteListener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentsAdapter.addAll(comments);
                incSkip();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(CommentsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(CommentsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
}
