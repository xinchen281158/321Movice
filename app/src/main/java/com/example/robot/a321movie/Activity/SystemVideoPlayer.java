package com.example.robot.a321movie.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.robot.a321movie.R;
import com.example.robot.a321movie.domain.MediaItem;
import com.example.robot.a321movie.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayer extends Activity implements View.OnClickListener{

    /**
     * 视频进度更新
     */
    private static final int Progress=1;
    private static final int HIDE_MEDIACONTROLLER = 2;
    private VideoView video_view;
    private Uri uri;
    private Utils utils;

    /**
     * 判断是否正在播放
     */
    private boolean isPlaying;
    /**
     * 电量广播
     */
    private MyBroadcastReceiver receiver;

    /**
     * VideoFragment 传递过来的列表信息和点击位置
     */
    private   ArrayList<MediaItem> mediaItems;
    private int position;

    /**
     * 手势识别器
     */
    private GestureDetector detector;

    /**
     * 是否显示控制栏
     */
    private boolean isshowMediaController=true;


    private LinearLayout llBottom;
    private TextView tvName;
    private TextView tv_current_time;
    private TextView tv_duration;
    private ImageView ivBattery;


    private TextView tvSystemTime;
    private Button btnVoice;
    private SeekBar seekBarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llTop;
    private TextView tvCurrentTime;
    private SeekBar seekBarVideo;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;
    private RelativeLayout rl_all;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-11-27 19:40:57 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvName = (TextView)findViewById( R.id.tv_name );
        tv_duration = (TextView)findViewById( R.id.tv_duration );
        tv_current_time = (TextView)findViewById( R.id.tv_current_time );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekBarVoice = (SeekBar)findViewById( R.id.seekBar_voice );
        btnSwitchPlayer = (Button)findViewById( R.id.btn_switch_player );
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekBarVideo = (SeekBar)findViewById( R.id.seekBar_video );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );
        video_view = (VideoView) findViewById(R.id.video_view);
        rl_all = findViewById(R.id.rl_all);

        btnVoice.setOnClickListener( this );
        btnSwitchPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );



    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-11-27 19:40:57 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            // Handle clicks for btnVoice
        } else if ( v == btnSwitchPlayer ) {
            // Handle clicks for btnSwitchPlayer
        } else if ( v == btnExit ) {
            // Handle clicks for btnExit
        } else if ( v == btnVideoPre ) {
            // 上一个视频按钮
            playPreVideo();
        } else if ( v == btnVideoStartPause ) {
            //停止和播放按钮
            pauseAndstart();

        } else if ( v == btnVideoNext ) {
            // 下一个视频按钮
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if(mediaItems !=null &&mediaItems.size()>0){
            position--;
            if (position >= 0){
                MediaItem mediaItem=mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                video_view.setVideoPath(mediaItem.getData());
                setButonState();
            }else if (uri!=null){
                finish();//退出播放器
            }
        }
    }

    private void playNextVideo() {
        if(mediaItems !=null &&mediaItems.size()>0){
            //播放下一个
            position++;
            if (position < mediaItems.size()){
                MediaItem mediaItem=mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                video_view.setVideoPath(mediaItem.getData());
                setButonState();
            }else if (uri!=null){
                finish();//退出播放器
            }
        }
    }

    /**
     * 设置按钮状态，是否可以点击
     */
    private void setButonState() {
        if(mediaItems !=null &&mediaItems.size()>0){
            if (mediaItems.size()==0){
                btnVideoPre.setEnabled(false);
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoNext.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            }
            if(mediaItems.size()==2){
                if(position==0){
                    btnVideoPre.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoNext.setEnabled(true);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                }else if(position==mediaItems.size()-1){
                    btnVideoPre.setEnabled(true);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoNext.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                }
            }else {
                if(position==0){
                    btnVideoPre.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                }else {
                    btnVideoPre.setEnabled(true);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoNext.setEnabled(true);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                }
            }
        }
    }

    private void pauseAndstart() {
        // 设置停止播放
        isPlaying=video_view.isPlaying();
        if(isPlaying){
            //视频在播放---设置暂停
            video_view.pause();
            //按钮状态切换为播放按钮
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else{
            //视频暂停---设置播放
            video_view.start();
            //按钮状态切换为暂停按钮
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Progress:
                    //1、得到当前视频播放进程
                    int currentPosition=video_view.getCurrentPosition();

                    //2、获得当前进度
                    seekBarVideo.setProgress(currentPosition);

                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //更新系统时间设置
                    tvSystemTime.setText(getSystemTime());



                    //3、每秒更新一次（要记得先移除 Progress）
                    handler.removeMessages(Progress);
                    handler.sendEmptyMessageDelayed(Progress,1000);

                    break;
                case HIDE_MEDIACONTROLLER:
                    hideMediaController();
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @Override
    protected void onDestroy() {
        if(receiver!=null){
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_system_video_player);

        findViews();

        initData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private void initData() {
        utils=new Utils();
        //得到 url
        uri=getIntent().getData();
        if(uri!=null){
            video_view.setVideoURI(uri);
        }

        setListener();

        //注册电量广播
        IntentFilter intentFiler=new IntentFilter();
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver=new MyBroadcastReceiver();
        registerReceiver(receiver,intentFiler);

        //获得从 VideoFragment 中传递过来的列表数据和点击位置
        mediaItems = new ArrayList<>();
        getData();
        //设置数据
        setData();

        //手势识别器
        detector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                isPlaying=!isPlaying;
                pauseAndstart();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this,"111111",0).show();
                if(isshowMediaController){
                    hideMediaController();
                }else {
                    showMediaController();
                    sendShowControllerMessage();
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void sendShowControllerMessage() {
        //发送隐藏控制面板的消息
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
    }
    private void removeControllerMessage(){
        handler.removeMessages(HIDE_MEDIACONTROLLER);
    }

    private void showMediaController() {
        rl_all.setVisibility(View.VISIBLE);
        isshowMediaController=true;
    }

    private void hideMediaController() {
        rl_all.setVisibility(View.GONE);
        isshowMediaController=false;
    }

    private void setData() {
        if(mediaItems !=null &&mediaItems.size()>0){
            MediaItem mediaItem=mediaItems.get(position);
            tvName.setText(mediaItem.getName());
            video_view.setVideoPath(mediaItem.getData());
        }else if(uri!=null){
            tvName.setText(uri.toString());
            video_view.setVideoURI(uri);
        }else{
            Toast.makeText(this,"没有传递数据！很抱歉！！",0).show();
        }
        setButonState();
    }

    private void getData() {
        //得到地址
        uri=getIntent().getData();
        mediaItems=(ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");//获得传进来的视频列表信息
        position=getIntent().getIntExtra("position",0);//获得传进来的位置，默认设置为0
    }

    /**
     * 电量广播监听器类
     */
    class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level=intent.getIntExtra("level",0);//电量0~100
            //设置电量
            setBattery(level);
        }
    }

    /**
     * 设置电量变化图标
     * @param level
     */
    private void setBattery(int level) {
        if(level<=0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level<10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level<20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level<40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level<60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level<80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level<100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }


    private void setListener() {
        //分别设置准备完成的监听、播放出错的监听、播放完成的监听

        video_view.setOnPreparedListener(new MyOnPreparedListener());//准备完成的监听

        video_view.setOnErrorListener(new MyOnErrorListener());//播放出错的监听

        video_view.setOnCompletionListener(new MyOnCompletionListener());//播放完成的监听

        //设置 seekBar 状态变化的监听
        seekBarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                showMediaController();
                video_view.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            sendShowControllerMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendShowControllerMessage();
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            //Toast.makeText(SystemVideoPlayer.this,"播放完成！",0).show();
            playNextVideo();
        }
    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener{

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            video_view.start();
            //开始播放
            int duration=video_view.getDuration();//获得视频总时长
            seekBarVideo.setMax(duration);
            tv_duration.setText(utils.stringForTime(duration));
            handler.sendEmptyMessage(Progress);
        }
    }
}
