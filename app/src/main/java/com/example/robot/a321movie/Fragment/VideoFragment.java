package com.example.robot.a321movie.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.robot.a321movie.Activity.SystemVideoPlayer;
import com.example.robot.a321movie.Adapter.VideoAdapter;
import com.example.robot.a321movie.R;
import com.example.robot.a321movie.domain.MediaItem;

import java.util.ArrayList;

/**
 * Created by robot on 2017/11/22.
 * 本地视屏
 */

public class VideoFragment extends BaseFragment {
    private ListView lv_video;
    private TextView tv_novideo;
    private ProgressBar pb_loading;

    public ArrayList<MediaItem> MediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pb_loading.setVisibility(View.GONE);//隐藏加载进度
            if(MediaItems!=null&&MediaItems.size()>0){
                //有数据
                //设置适配器
                VideoAdapter videoAdapter=new VideoAdapter(mContext,MediaItems);
                lv_video.setAdapter(videoAdapter);
                //给 ListView 的 Item设置监听
                MyOnItemClickListener listener=new MyOnItemClickListener();
                lv_video.setOnItemClickListener(listener);
                //隐藏文本
                tv_novideo.setVisibility(View.GONE);
            }else {
                //没有数据
                //显示文本
                tv_novideo.setVisibility(View.VISIBLE);
            }
        }
    };

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem=MediaItems.get(position);
//            //1、调起系统所有播放器---隐式意图
//            Intent intent=new Intent();
//            intent.setDataAndType(Uri.parse(mediaItem.getData()), "video/*");//虚拟机找不到视频播放器
//            mContext.startActivity(intent);
            //2、自定义播放器
//            Intent intent = new Intent(mContext,SystemVideoPlayer.class);
//            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video/*");
//            mContext.startActivity(intent);
            //3、传递播放列表数据到 SystemVideo 中去（传递列表数据---对象----序列化）
            Intent intent = new Intent(mContext,SystemVideoPlayer.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",MediaItems);
            intent.putExtras(bundle);

            //还得传递要播放的列表中的具体位置
            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    @Override
    public View initView() {
        View view=View.inflate(mContext, R.layout.video_fragment,null);
        lv_video = view.findViewById(R.id.lv_video);
        tv_novideo = view.findViewById(R.id.tv_novideo);
        pb_loading = view.findViewById(R.id.pb_loading);
        return view;
    }

    @Override
    public void initDate() {
        super.initDate();
        Log.e("TAG","VideoFragment初始化....");
        //初始化视频数据
        getDateFromLocal();
    }

    private void getDateFromLocal() {
        /**
         * 要读取本地视频文件数据，是一个长时间的耗时操作
         * 从本地的 sdcard 得到数据有两种方式:
         1、遍历 sdcard ，后缀名
         2、从内容提供者里面获取视屏
         3、如果是6.0的系统 需要动态获取读取 sdcard 的权限
         */
        new Thread(new Runnable() {
            @Override
            public void run() {

                isGrantExternalRW((Activity) mContext);

                MediaItems = new ArrayList<MediaItem>();
                ContentResolver resolver=mContext.getContentResolver();
                Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Video.Media.DISPLAY_NAME,//视频在 SD 卡的名称
                        MediaStore.Video.Media.DURATION,//视频的总时长
                        MediaStore.Video.Media.SIZE,//视频的大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者
                };
                Cursor cursor=resolver.query(uri,objs,null,null,null);
                if(cursor!=null){
                    while(cursor.moveToNext()){
                        MediaItem mediaItem=new MediaItem();

                        String name=cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration=cursor.getLong(1);//视频的时常
                        mediaItem.setDuration(duration);

                        long size=cursor.getLong(2);//视频的大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//视频的绝对地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//歌曲的作者
                        mediaItem.setArtist(artist);

                        MediaItems.add(mediaItem);
                    }
                    cursor.close();
                }

                handler.sendEmptyMessage(10);
            }
        }).start();
    }

    /**
     * 解决Android6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    private static boolean isGrantExternalRW(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);
            return false;
        }

        return true;
    }
}
