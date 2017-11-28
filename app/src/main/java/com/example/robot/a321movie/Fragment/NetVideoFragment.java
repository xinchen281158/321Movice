package com.example.robot.a321movie.Fragment;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by robot on 2017/11/22.
 * 网络视屏
 */

public class NetVideoFragment extends BaseFragment {
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
        mTextView.setText("网络视屏Fragment");
    }
}
