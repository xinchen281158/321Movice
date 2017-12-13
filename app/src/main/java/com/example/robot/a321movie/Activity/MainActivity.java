package com.example.robot.a321movie.Activity;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.robot.a321movie.Fragment.AudioFragment;
import com.example.robot.a321movie.Base.BaseFragment;
import com.example.robot.a321movie.Fragment.NetAudioFragment;
import com.example.robot.a321movie.Fragment.NetVideoFragment;
import com.example.robot.a321movie.Fragment.VideoFragment;
import com.example.robot.a321movie.R;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg_main;

    /**
     * 选中的位置
     */
    private int position;

    /**
     * 页面集合
     */
    private ArrayList<BaseFragment> baseFragments;

    /**
     * 上次切换的fragment
     */
    private BaseFragment mContentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main=findViewById(R.id.rg_main);

        //设置RadioGroup 的点击监听
        rg_main.setOnCheckedChangeListener(this);



        baseFragments=new ArrayList<>();
        baseFragments.add(new VideoFragment());//添加本地视频界面 0
        baseFragments.add(new AudioFragment());//添加本地音乐界面 1
        baseFragments.add(new NetVideoFragment());//添加网络视频界面 2
        baseFragments.add(new NetAudioFragment());//添加网络音乐界面 3

        //设置选中首页
        rg_main.check(R.id.rb_video);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_video:
                position=0;
                break;
            case R.id.rb_audio:
                position=1;
                break;
            case R.id.rb_netvideo:
                position=2;
                break;
            case R.id.rb_netaudio:
                position=3;
                break;
            default:
                position=0;
        }
       BaseFragment to=getFragment();
        switchFragment(mContentFragment,to);
    }

    /**
     *防止重复创建 Fragment
     * @param from 刚才显示的 Fragment，马上就要被隐藏
     * @param to    马上要切换到的Fragment，
     */
    private void switchFragment(BaseFragment from,BaseFragment to) {
        if(from!=to){
            mContentFragment=to;
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            //才切换
            //判断有没有被添加
            if(!to.isAdded()){
                //没有被添加
                //from 隐藏
                if(from!=null){
                    ft.hide(from);
                }
                //添加 to
                if(to!=null){
                    ft.add(R.id.fl_main, to).commit();
                }
            }else {
                //to 已经被添加
                //from 隐藏
                if(from!=null){
                    ft.hide(from);
                }
                //显示 to
                if(to!=null){
                    ft.show(to).commit();
                }
            }
        }
    }
//    private void switchFragment(BaseFragment bf) {
//        //1、得到 FragmentManger
//        FragmentManager fm=getSupportFragmentManager();
//        //2、开启事务
//        FragmentTransaction ft=fm.beginTransaction();
//        //3、替换
//        ft.replace(R.id.fl_main,bf);//使用这个方法会重复创建 Fragment，消耗不必要的资源
//        //4、提交事务
//        ft.commit();
//    }

    private BaseFragment getFragment() {
        if(baseFragments!=null){
            BaseFragment baseFragment=baseFragments.get(position);
        return  baseFragment;
        }
        return null;
    }
}
