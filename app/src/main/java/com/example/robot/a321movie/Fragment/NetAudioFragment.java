package com.example.robot.a321movie.Fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.robot.a321movie.Base.BaseFragment;

/**
 * Created by robot on 2017/11/22.
 * 网络音乐
 */

public class NetAudioFragment extends BaseFragment {
    private TextView mTextView;
    @Override
    public View initView() {
        mTextView=new TextView(mContext);
        mTextView.setTextColor(Color.RED);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(20);
        return mTextView;
    }

    @Override
    public void initDate() {
        super.initDate();
        mTextView.setText("网络音乐Fragment");
    }
}
