package com.example.robot.a321movie.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.robot.a321movie.R;

/**
 * Splash ：初始化进入页面
 */
public class SplashActivity extends Activity implements View.OnClickListener {

    private Handler handler=new Handler();
    private LinearLayout activity_splash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity_splash = findViewById(R.id.activity_splash);
        activity_splash.setOnClickListener(this);

        //发送延时消息，进入下一个 MainActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        },2000);
    }

    private  boolean isStart;
    private void initView() {
        if(!isStart) {
            isStart=true;
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
        //销毁当前页面
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStart=false;
        //清楚所有消息队列中的消息防止内存泄露
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        initView();
    }
}
