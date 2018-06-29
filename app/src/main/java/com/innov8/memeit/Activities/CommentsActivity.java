package com.innov8.memeit.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.innov8.memeit.R;
import com.memeit.backend.MemeItMemes;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsActivity extends AppCompatActivity {

    int page = 1;
    int limit = 20;
    int skip = 0;
    int start = 1;
    int end = 20;
    int total = 300; //todo: fix this and enter total number of comments for a post

    MemeItMemes memeItMemes;

    String memeID;
    @BindView(R.id.comments_list)
    ListView commentsList;
    @BindView(R.id.comments_back)
    ImageView backNav;
    @BindView(R.id.comments_forward)
    ImageView forwardNav;
    @BindView(R.id.comment_field)
    EditText commentField;
    @BindView(R.id.comment_button)
    ImageView commentButton;
    @BindView(R.id.comments_out_of_text)
    TextView outOfText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        memeID = getIntent().getStringExtra("id");

        memeItMemes = MemeItMemes.getInstance();

        memeItMemes.getCommentsForMeme(memeID, skip, limit, new OnCompleteListener<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                commentsList.setAdapter(new CommentsAdapter(getApplicationContext(),R.layout.list_item_comment,comments));
                setOutOfText();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(getApplicationContext(),"Something went wrong.",Toast.LENGTH_LONG).show();
                Log.w("Comments Error",error.getMessage() + "\nid = " + memeID);
            }
        });

        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(page!=1){
                    page--;
                    skip-=20;
                    start-=20;
                    end-=20;

                    memeItMemes.getCommentsForMeme(memeID, skip, limit, new OnCompleteListener<List<Comment>>() {
                        @Override
                        public void onSuccess(List<Comment> comments) {
                            commentsList.setAdapter(new CommentsAdapter(getApplicationContext(),R.layout.list_item_comment,comments));
                            setOutOfText();
                        }

                        @Override
                        public void onFailure(Error error) {
                            Toast.makeText(getApplicationContext(),"Something went wrong.",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if(page==2) backNav.setColorFilter(Color.parseColor("#bbbbbb"));
            }
        });
        forwardNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                skip+=20;
                start+=20;
                end+=20;
                memeItMemes.getCommentsForMeme(memeID, skip, limit, new OnCompleteListener<List<Comment>>() {
                    @Override
                    public void onSuccess(List<Comment> comments) {
                        commentsList.setAdapter(new CommentsAdapter(getApplicationContext(),R.layout.list_item_comment,comments));
                        setOutOfText();
                    }

                    @Override
                    public void onFailure(Error error) {
                        Toast.makeText(getApplicationContext(),"Something went wrong.",Toast.LENGTH_LONG).show();
                    }
                });
                if(page==1) backNav.setColorFilter(Color.parseColor("#000000"));
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = commentField.getText().toString();
                if(!commentText.equals("")){
                    memeItMemes.comment(memeID, new Comment(commentText), new OnCompleteListener<Comment>() {
                        @Override
                        public void onSuccess(Comment comment) {
                            memeItMemes.getCommentsForMeme(memeID, skip, limit, new OnCompleteListener<List<Comment>>() {
                                @Override
                                public void onSuccess(List<Comment> comments) {
                                    commentsList.setAdapter(new CommentsAdapter(getApplicationContext(),R.layout.list_item_comment,comments));
                                    setOutOfText();
                                }

                                @Override
                                public void onFailure(Error error) {
                                    Toast.makeText(getApplicationContext(),"Something went wrong.",Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Error error) {
                            Toast.makeText(getApplicationContext(),"Sorry, a problem has occured",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else Toast.makeText(getApplicationContext(),"Please enter a comment.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public class CommentsAdapter extends ArrayAdapter<Comment>{
        Context c;
        ArrayList<Comment> comments = new ArrayList<>();

        public CommentsAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
            super(context, resource, objects);
            c = context;
            comments = (ArrayList<Comment>) objects;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view= LayoutInflater.from(c).inflate(R.layout.list_item_comment,parent,false);
            TextView name = view.findViewById(R.id.list_name);
            TextView comment = view.findViewById(R.id.list_comment);
            TextView date = view.findViewById(R.id.list_item_date);
            ImageView userPhoto = view.findViewById(R.id.list_pp);

            Comment currentObject = comments.get(position);

            name.setText(currentObject.getPid());
            comment.setText(currentObject.getComment());
            date.setText(currentObject.getDate());
//            Glide.with(c).load().into(userPhoto); todo: Biruk: Not sure how to do this yet. Find out.

            return super.getView(position, convertView, parent);
        }
    }

    @SuppressLint("DefaultLocale")
    public void setOutOfText(){
        outOfText.setText(String.format("showing %d - %d out of %d memes.", start, end, total));
    }
}
