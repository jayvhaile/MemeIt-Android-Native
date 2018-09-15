package com.innov8.memeit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innov8.memeit.R;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {
    ArrayList<String> tags = new ArrayList<>();
    Context context;

    private static final int SELECTED = 1;
    private static final int UNSELECTED = 0;

    ArrayList<Integer> selected = new ArrayList<>();
    public ArrayList<String> selectedTags = new ArrayList<>();

    public TagsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_tags,parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(position,holder.tagText,holder.cardView,holder.hash);
            }
        });

        if(getItemViewType(position) == SELECTED){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.tagText.setTextColor(-1);
            holder.hash.setTextColor(context.getResources().getColor(R.color.white_light));
        }
        else{
            holder.cardView.setCardBackgroundColor(-1);
            holder.tagText.setTextColor(context.getResources().getColor(R.color.colorAccent));
            holder.hash.setTextColor(context.getResources().getColor(R.color.black_light));
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isSelected(position) ? SELECTED : UNSELECTED;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tagText;
        CardView cardView;
        TextView hash;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tagText = itemView.findViewById(R.id.tagText);
            cardView = itemView.findViewById(R.id.card_tag);
            hash = itemView.findViewById(R.id.hash);

        }
    }

    public void setData(ArrayList<String> strings){
        tags.addAll(strings);
    }
    private boolean isSelected(int pos) {
        boolean f = false;
        for (Integer i : selected) {
            if (pos == i) {
                f = true;
                break;
            }
        }
        return f;
    }
    private void toggle(int position, TextView tagText, CardView cardView, TextView hash){
        if(getItemViewType(position) == UNSELECTED){
            selected.add(position);
            selectedTags.add(tags.get(position));
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            tagText.setTextColor(-1);
            hash.setTextColor(context.getResources().getColor(R.color.white_light));
        }
        else{
            selected.remove(position);
            selectedTags.remove(tags.get(position));
            cardView.setCardBackgroundColor(-1);
            tagText.setTextColor(context.getResources().getColor(R.color.colorAccent));
            hash.setTextColor(context.getResources().getColor(R.color.black_light));
        }
    }

    public ArrayList<String> getSelectedTags() {
        return selectedTags;
    }

    private ColorPreset generateColor(){
        return new ColorPreset(
                Color.parseColor(
                        context.getResources().getStringArray(R.array.tagColors)
                                [new Random().nextInt(19)]
                )
        );
    }

    private class ColorPreset{
        int backgroundColorRes;
        int textColor = R.color.white;
        public ColorPreset(int backgroundColorRes, Integer textColor) {
            this.backgroundColorRes = backgroundColorRes;
            if(textColor!=null) this.textColor = textColor;
        }
        public ColorPreset(int backgroundColorRes) {
            this.backgroundColorRes = backgroundColorRes;
        }

        public int getBackgroundColorRes() {
            return backgroundColorRes;
        }

        public void setBackgroundColorRes(int backgroundColorRes) {
            this.backgroundColorRes = backgroundColorRes;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }
    }
}
