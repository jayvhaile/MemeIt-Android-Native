package com.innov8.memeit.CustomClasses;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.innov8.memeit.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class AvatarNameBehavior extends CoordinatorLayout.Behavior<TextView> {
    private final static String TAG = "behavior";
    private float mFinalHeight;
    private Context mContext;

    private float dscy;
    private float dfcy;
    private float margin;
    private float startingTextSize;
    private float finalTextSize;
    private float scaledDensity;

    public AvatarNameBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarNameBehavior);
            dscy = a.getDimension(R.styleable.AvatarNameBehavior_dscy, 0);
            dfcy = a.getDimension(R.styleable.AvatarNameBehavior_dfcy, 0);
            margin = a.getDimension(R.styleable.AvatarNameBehavior_margin, 0);
            finalTextSize=a.getDimension(R.styleable.AvatarNameBehavior_ftextsize,0);
            a.recycle();
        }
        init();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof ImageView;
    }
    private void init(){
        mFinalHeight = mContext.getResources().getDimension(R.dimen.toolbar_height);
        scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
    }
    int textColor=0;
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        if (dependency instanceof ImageView) {
            if(startingTextSize==0){
                startingTextSize=child.getTextSize();
            }
            float size=dependency.getWidth();
            float dccx=dependency.getX()+size/2;
            float dccy=dependency.getY()+size/2;

            float ctextSize=(startingTextSize-finalTextSize)*(dccy-dfcy)/(dscy-dfcy)+finalTextSize;

            ctextSize=ctextSize/ scaledDensity;
            child.setTextSize(ctextSize);

            float cw=child.getWidth();
            float ch=child.getHeight();

            double angle=90f*(dccy-dscy)/(dfcy-dscy);

            int c=angle>88f?Color.WHITE:Color.BLACK;

            if(textColor!=c){
                textColor=c;
                child.setTextColor(textColor);
            }

            angle= angle*Math.PI/180;
            float cr= (float) ((Math.sin(angle)*cw/2)+(Math.cos(angle)*ch/2));
            float m= (float) (margin-((margin*0.5f)*Math.cos(angle)));
            float radius=(size/2)+m+cr;


            float tcx= (float) (Math.sin(angle)*radius);
            float tcy= (float) (Math.cos(angle)*radius);

            setCenter(child,tcx+dccx,tcy+dccy);
            return true;
        }
        return false;
    }

    private void setCenter(TextView child,float cx,float cy){
        child.setX(cx-child.getWidth()/2);
        child.setY(cy-child.getHeight()/2);
    }

}
