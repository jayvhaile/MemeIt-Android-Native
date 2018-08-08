package com.innov8.memegenerator.CustomViews;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.view.menu.MenuBuilder;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.innov8.memegenerator.R;

/**
 * Created by Jv on 7/7/2018.
 */


public class NavTab extends LinearLayout {
    int menu_resource;
    int skip_tint;
    int item_padding;
    boolean showTitle;
    int size;

    int selected_tint;
    int deselected_tint;

    LayoutParams  layoutParams;
    OnItemSelectListener onItemSelectListener;

    public NavTab(Context context) {
        super(context);
    }

    public NavTab(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);

        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.NavTab,0,0);
        try {
            menu_resource =a.getResourceId(R.styleable.NavTab_menu,0);
            skip_tint =a.getInteger(R.styleable.NavTab_skip_tint,-1);
            selected_tint=a.getColor(R.styleable.NavTab_selected_tint,Color.RED);
            deselected_tint=a.getColor(R.styleable.NavTab_deselected_tint,Color.GRAY);
            item_padding= (int) a.getDimension(R.styleable.NavTab_item_padding,0f);
            
        }finally {
            a.recycle();
        }
        layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,0 ,1f);

        if(menu_resource==0)return;

        init();
        select(0);
    }

    public NavTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavTab(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(){
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                int no = Integer.parseInt(String.valueOf(view.getTag()));
                select(no);
                fireOnItemSelected(view);
            }
        };

        for (int i = 0; i < getChildCount(); i++) {
            ImageView imgv= (ImageView) getChildAt(i);
            imgv.setLayoutParams(layoutParams);
            imgv.setOnClickListener(listener);
            imgv.setTag(i);
            imgv.setPadding(0,item_padding ,0,item_padding);
        }
        invalidate();

    }
    private void select(int x){
        clearTints();
        setTinted(x);
    }

    private void setTinted(int x) {
        if (isNotTintable(x)) return;
        ImageView iv = (ImageView)getChildAt(x);
        iv.setColorFilter(selected_tint, PorterDuff.Mode.SRC_IN);
    }
    private void clearTints(){
        for (int i = 0; i < getChildCount(); i++) {
            if (isNotTintable(i)) continue;
            ImageView iv = (ImageView) getChildAt(i);
            iv.setColorFilter(deselected_tint, PorterDuff.Mode.SRC_IN);
        }
    }

    private boolean isNotTintable(int index){
        return skip_tint == -1 || index == skip_tint;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }
    private void fireOnItemSelected(View view){
        if (onItemSelectListener!=null){
            onItemSelectListener.selected(view);
        }
    }

    public interface OnItemSelectListener{
        void selected(View view);
    }
}
