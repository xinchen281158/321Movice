package com.example.robot.a321movie.Base;

import android.content.Context;
import android.view.View;

/**
 * Created by robot on 2017/11/22.
 * 基类
 * 公共类
 * 作用：所有 pager 的基类
 */

public abstract class BasePager {

    /**
     * 上下文
     */
    public Context context;
    /**
     * 接受各个页面的实例
     */
    public View rootView;

    public BasePager(Context context) {
        this.context=context;
        rootView=initView();
    }

    /**
     * 强制孩子实现，来实现特定的效果
     * @return
     */
    public abstract View initView();

    /**
     * 当子页面需要初始化数据，联网请求数据，或者绑定数据的时候需要重写该方法
     */
    public void initDate(){}

}
