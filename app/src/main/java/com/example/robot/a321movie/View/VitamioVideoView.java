package com.example.robot.a321movie.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.*;

/**
 * Created by robot on 2017/11/29.
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView{

    public VitamioVideoView(Context context) {
        this(context,null);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);//存储测量的宽度和高度
    }


    public void setVideoSize(int videoWidth,int videoHeight){
        //layoutParams 用来告诉父视图应该如何安排自己
        ViewGroup.LayoutParams params=getLayoutParams();
        params.width=videoWidth;
        params.height=videoHeight;
        setLayoutParams(params);
    }
}
