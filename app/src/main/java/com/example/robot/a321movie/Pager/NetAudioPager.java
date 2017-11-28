package com.example.robot.a321movie.Pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.robot.a321movie.Base.BasePager;

/**
 * Created by robot on 2017/11/22.
 */

public class NetAudioPager extends BasePager {

    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView=new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(18);
        return textView;
    }

    @Override
    public void initDate() {
        super.initDate();
        textView.setText("网络音乐界面");
    }
}
