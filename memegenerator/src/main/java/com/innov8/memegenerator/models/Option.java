package com.innov8.memegenerator.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class Option<T,V extends View> {
    protected Context mContext;
    private String name;
    private V view;
    private OnOptionChangedListener<T> listener;

    public Option(Context context, String name) {
        this.mContext = context;
        this.name = name;
        initView();
    }
    private void initView(){
        view= (V) LayoutInflater.from(mContext).inflate(getViewLayoutId(),null);
        OnCreateView(view);
    }
    protected abstract void OnCreateView(V view);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getView() {
        return view;
    }

    public void setOnOptionChangedListener(OnOptionChangedListener<T> listener) {
        this.listener = listener;
    }

    public abstract void updateOption(T t);
    public abstract int getViewLayoutId();


    protected void fireOnOptionChanged(T t){
        if (listener != null) {
            listener.onOptionChanged(t);
        }
    }

    public interface  OnOptionChangedListener<T>{
        public void onOptionChanged(T t);
    }

}
