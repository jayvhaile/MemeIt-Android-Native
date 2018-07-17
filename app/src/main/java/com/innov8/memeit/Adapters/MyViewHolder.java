package com.innov8.memeit.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Jv on 7/12/2018.
 */

public abstract class MyViewHolder<T> extends RecyclerView.ViewHolder {


    public MyViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(T t);
}
