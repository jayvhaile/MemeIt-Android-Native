package com.innov8.memeit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innov8.memeit.CustomClasses.Suggestion;
import com.innov8.memeit.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagSuggestionAdapter extends RecyclerView.Adapter<TagSuggestionAdapter.ViewHolder> {
    Context context;
    ArrayList<Suggestion> suggestions = new ArrayList<>();

    public TagSuggestionAdapter(Context context) {
        this.context = context;
    }

    public TagSuggestionAdapter(Context context, ArrayList<Suggestion> suggestions) {
        this.context = context;
        this.suggestions = suggestions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_suggestion,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder R, int position) {
        R.followers.setText(String.format("%d", suggestions.get(position).getFollowers()));
        R.hashtag.setText(suggestions.get(position).getTag());
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView hashtag;
        TextView followers;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hashtag = itemView.findViewById(R.id.list_hashtag);
            followers = itemView.findViewById(R.id.list_followers);
        }
    }
    public void addData(List<Suggestion> suggestions){
        this.suggestions.addAll(suggestions);
    }
}
