package com.example.robot.a321movie.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.robot.a321movie.R;

/**
 * Created by robot on 2017/11/22.
 */

public class titleBar extends LinearLayout implements View.OnClickListener {
    private View tv_search;

    private View rl_game;

    private View iv_record;

    private Context mContext;
    /**
     * 在代码中实例化该类的时候调用该方法
     * @param context
     */
    public titleBar(Context context) {
        this(context,null);
    }

    /**
     * 当在布局文件使用该类的时候， Android 系统通过这个构造方法实例化该类
     * @param context
     * @param attrs
     */
    public titleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设置样式的时候，可以使用该方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public titleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
    }

    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例
        tv_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        //设置点击事件
        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search://搜索
                Toast.makeText(mContext, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game://游戏
                Toast.makeText(mContext, "游戏", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record://播放历史
                Toast.makeText(mContext, "播放历史", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
