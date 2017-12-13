package com.example.robot.a321movie.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
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


import com.example.robot.a321movie.R;
import com.example.robot.a321movie.View.VideoView;
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
    private static final int SHOW_SPEED=3;
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

    /**
     * 屏幕的宽和高
     */
    private int screenWidth=0;
    private int screenHeight=0;

    /**
     * 全屏
     */
    private static final int FULL_SCREEN=1;
    /**
     * 默认
     */
    private static final int DEFAULT_SCREEN=2;
    /**
     * 是否全屏
     */
    private boolean isFullScreen=false;

    /**
     * 视屏真正的宽和高
     */
    private int videoWidth;
    private int videoHeight;

    /**
     * 声音
     */
    private AudioManager am;
    /**
     * 当前音量
     */
    private int currentVoice;
    /**
     * 最大音量
     */
    private int maxVoice;
    /**
     * 是否静音,默认为false
     */
    private boolean isMute;

    /**
     * 判断 Uri 是否是来自于网络
     */
    private boolean isNetUri;
    /**
     * 判断使用的是系统的卡顿监听还是自己自定义的卡顿监听
     */
    private boolean isUseSystem=true;
    /**
     * 上一个缓冲
     */
    private int precurrentPosition;

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
    private LinearLayout ll_buffer;
    private LinearLayout ll_loading;
    private TextView tv_loading;
    private TextView tv_buffer;

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
        ll_buffer = findViewById(R.id.ll_buffer);
        ll_loading = findViewById(R.id.ll_loading);
        tv_loading=findViewById(R.id.tv_loading);
        tv_buffer=findViewById(R.id.tv_buffer);

        btnVoice.setOnClickListener( this );
        btnSwitchPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );

        //最大音量和 SeekBar 关联
        seekBarVoice.setMax(maxVoice);
        //设置当前进度--当前音量
        seekBarVoice.setProgress(currentVoice);
        //设置 SeekBarVoice 监听器
        seekBarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);

    }
    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(progress>0){
                        isMute=false;
                    }else {
                        isMute=true;
                    }
                    updateVolume(progress,isMute);
                }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeControllerMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideControllerMessage();
        }
    }

    /**
     * 更新声音变化 SeekBar
     * @param progress
     */
    private void updateVolume(int progress,boolean isMute) {
        //判断是否处于静音状态
        if(isMute){
            //设置为静音状态
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekBarVoice.setProgress(0);
        }else{
            //设置为非静音状态
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            seekBarVoice.setProgress(progress);
            currentVoice=progress;
        }
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
            // 点击静音按钮
            isMute=!isMute;
            updateVolume(currentVoice,isMute);
        } else if ( v == btnSwitchPlayer ) {
            // 点击感叹号按钮，切换播放器
            showSwitchPlayerDialog();
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
            // 设置视频全屏和默认
            setFullScreenAndDefault();
        }
    }

    /**
     * 点击切换不同播放器
     */
    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提示您");
        builder.setMessage("当您播放的视频没有声音的时候，请切换到万能播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if(mediaItems !=null &&mediaItems.size()>0){
            position--;
            if (position >= 0){
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem=mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri=utils.isNetUri(mediaItem.getData());
                video_view.setVideoPath(mediaItem.getData());
                setButonState();
            }else if (uri!=null){
                setButonState();//退出播放器
            }
        }
    }

    private void playNextVideo() {
        if(mediaItems !=null &&mediaItems.size()>0){
            //播放下一个
            position++;
            if (position < mediaItems.size()){
                ll_loading.setVisibility(View.VISIBLE);
                MediaItem mediaItem=mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                video_view.setVideoPath(mediaItem.getData());
                setButonState();
            }else if (uri!=null){
                setButonState();//退出播放器
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
        }else if(uri!=null){
            //两个按钮都设置为灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
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
                case SHOW_SPEED:
                    String netSpeed=utils.showNetSpeed();//得到网速

                    //显示网络速度
                    tv_buffer.setText("闪光加载中..."+netSpeed);
                    tv_loading.setText("系统检测缓存中..."+netSpeed);

                    //每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED,2000);
                    break;

                case Progress:
                    //1、得到当前视频播放进程
                    int currentPosition=video_view.getCurrentPosition();

                    //2、获得当前进度
                    seekBarVideo.setProgress(currentPosition);

                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));

                    //更新系统时间设置
                    tvSystemTime.setText(getSystemTime());

                    //支持视频缓冲，一秒钟更新一次,只有网络资源才能有缓存效果
                    if(isNetUri){
                        int buffer=video_view.getBufferPercentage();//得到视频的缓冲
                        int totalBuffer=buffer*seekBarVideo.getMax();//getMax（）是的到视频的最大值
                        int secondaryProgress=totalBuffer/100;
                        seekBarVideo.setSecondaryProgress(secondaryProgress);
                    }else {
                        //本地视频没有缓冲效果
                        seekBarVideo.setSecondaryProgress(0);
                    }

                    //自定监听卡顿
                    if (!isUseSystem && video_view.isPlaying()) {

                        if(video_view.isPlaying()) {
                            int buffer = currentPosition - precurrentPosition;
                            if (buffer < 500) {
                                //视频卡了
                                ll_buffer.setVisibility(View.VISIBLE);
                            } else {
                                //视屏未卡
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

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

        getData();
        //设置数据
        setData();
    }

    private float startY;
    /**
     * 屏幕的高
     */
    private float touchRang;
    /**
     * 按下时候的音量
     */
    private int mVol;

    /**
     * 识别手势上下滑动改变音量
     * @param event
     * @return
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //把事件传递给手势识别器
//        detector.onTouchEvent(event);
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                //记录按下时候的值
////                Toast.makeText(this,"11111",0).show();
//                startY=event.getY();
//                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//                touchRang=Math.min(screenHeight,screenWidth);//screenHeight
//                removeControllerMessage();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                //记录移动的时候的值
//                float endY=event.getY();
//                float distance=startY-endY;
//                //改变的音量
//                if(maxVoice==0){
//                    maxVoice=9;
//                }
//                float delta=(distance/touchRang)*maxVoice;
//                //最终的声音
//                int voice= (int) Math.min(Math.max(mVol+delta,0),maxVoice);
//                if(delta!=0){
//                    isMute=false;
//                    updateVolume(voice,isMute);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                sendHideControllerMessage();
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    private void initData() {
        utils=new Utils();

        setListener();

        //注册电量广播
        IntentFilter intentFiler=new IntentFilter();
        intentFiler.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver=new MyBroadcastReceiver();
        registerReceiver(receiver,intentFiler);

        //获得从 VideoFragment 中传递过来的列表数据和点击位置
        mediaItems = new ArrayList<>();


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
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this,"111111",0).show();
                if(isshowMediaController){
                    hideMediaController();
                }else {
                    showMediaController();
                    sendHideControllerMessage();
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕的宽和高
//        //过时的方式
//        screenWidth=getWindowManager().getDefaultDisplay().getWidth();
//        screenHeight=getWindowManager().getDefaultDisplay().getHeight();
        //得到屏幕的宽和高最新方式
        DisplayMetrics displayMetrics=new DisplayMetrics();//描述关于显示诸如尺寸、密度的一般信息的工具类
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth=displayMetrics.widthPixels;
        screenHeight=displayMetrics.heightPixels;


        /**
         * 实例化 AudioManger
         */
        am=(AudioManager)getSystemService(AUDIO_SERVICE);
        //得到当前音量
        currentVoice=am.getStreamVolume(AudioManager.STREAM_MUSIC);
        //得到系统最大音量
        maxVoice=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获得该类型音量最大值

    }

    /**
     * 监听物理键，实现声音的大小调节
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updateVolume(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            sendHideControllerMessage();
            return true;//不显示系统的进度条
        }else  if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updateVolume(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            sendHideControllerMessage();
            return true;//不显示系统的进度条
        }else return super.onKeyDown(keyCode, event);
    }


    private void setFullScreenAndDefault(){
        if(isFullScreen){
            //是全屏，设置为默认
            setVideoType(DEFAULT_SCREEN);
        }else {
            //是默认，设置为全屏
            setVideoType(FULL_SCREEN);
        }
    }

    /**
     * 设置屏幕显示是否为全屏
     * @param defaultScreen
     */
    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN://全屏
                //1、设置视频画面的大小---屏幕有多大就设置多大
                video_view.setVideoSize(screenWidth,screenHeight);
                //2、设置按钮的状态--默认
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen=true;//是全屏
                break;

            case DEFAULT_SCREEN://默认
                //1、设置视频画面的大小
                //视频真实的宽高
                int mVideoWidth=videoWidth;
                int mVideoHeight=videoHeight;
                //屏幕的宽和高
                int width=screenWidth;
                int height=screenHeight;

                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                video_view.setVideoSize(width,height);

                //设置按钮状态全屏
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen=false;//视屏不是全屏
                break;

        }
    }

    private void sendHideControllerMessage() {
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
            isNetUri=utils.isNetUri(uri.toString());
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

        //使用系统自带监听卡顿监听器监听卡顿
        if(isUseSystem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                video_view.setOnInfoListener(new VideoOnInfoListener());
            }
        }
    }
    class VideoOnInfoListener implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了拖动卡
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://卡顿结束
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
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
            sendHideControllerMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideControllerMessage();
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
            startVitamioPlayer();
            return true;
        }
    }

    /**
     * a、把数据传入 VitamioVideoPlayer 播放器
     *b、关闭系统播放器
     */
    private void startVitamioPlayer() {
        if(video_view!=null){
            video_view.stopPlayback();
        }
        Intent intent = new Intent(SystemVideoPlayer.this, VitamioViderPlayer.class);
        if(mediaItems!=null&&mediaItems.size()>0){
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        }else if(uri!=null){
            intent.setData(uri);
        }
        startActivity(intent);
        //关闭当前界面
        finish();
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

            //设置 ViedoView 的宽高
            videoWidth=mp.getVideoWidth();//得到视频实际大小的宽
            videoHeight=mp.getVideoHeight();//得到视频实际大小的高
//            video_view.setVideoSize(videoWidth,videoHeight);//测试实际视频大小尺寸
            //设置屏幕的样式
            setVideoType(DEFAULT_SCREEN);
            //把加载页面取消掉
            ll_loading.setVisibility(View.GONE);
        }

    }

}
