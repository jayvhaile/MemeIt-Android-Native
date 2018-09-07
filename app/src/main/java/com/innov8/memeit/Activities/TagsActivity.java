package com.innov8.memeit.Activities;

import android.os.Bundle;

import com.innov8.memeit.Adapters.TagsAdapter;
import com.innov8.memeit.R;

import java.util.ArrayList;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TagsActivity extends AppCompatActivity {
    TagsAdapter tagsAdapter;
    RecyclerView recyclerView;

    public static final ArrayList<String> DUMMY_DATA = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        recyclerView = findViewById(R.id.tags_recycler);
        tagsAdapter = new TagsAdapter(this);
        tagsAdapter.setData(generateDummyData()); //todo jv and biruk: come up with tags
                               //todo jv: add trending tags here
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(tagsAdapter);
    }
    public ArrayList<String> generateDummyData(){
        final ArrayList<String> DUMMY_DATA = new ArrayList<>();
        Collections.addAll(DUMMY_DATA,
                "programming",
                "relationships",
                "pets",
                "life",
                "school",
                "women",
                "men",
                "music",
                "movies"
        );
        return DUMMY_DATA;
    }
}
