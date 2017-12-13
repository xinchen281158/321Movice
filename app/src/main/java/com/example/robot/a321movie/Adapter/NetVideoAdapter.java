package com.example.robot.a321movie.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.robot.a321movie.R;
import com.example.robot.a321movie.domain.MediaItem;
import com.example.robot.a321movie.utils.Utils;

import org.w3c.dom.Text;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by robot on 2017/11/26.
 */

public class NetVideoAdapter extends BaseAdapter {

    private ImageView iv_netVideo;

    private TextView tv_netName;

    private TextView tv_netDesc;

    private Context mContext;
    private ArrayList<MediaItem> mediaItems;


    public NetVideoAdapter(Context mContext, ArrayList<MediaItem> mediaItems) {
        this.mContext = mContext;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = convertView.inflate(mContext, R.layout.item_netvideo, null);

        tv_netDesc=convertView.findViewById(R.id.tv_netDesc);
        tv_netName=convertView.findViewById(R.id.tv_netName);
        iv_netVideo=convertView.findViewById(R.id.iv_netVideo);
        //根据 position 得到列表中对应位置的数据
        MediaItem mediaItem = mediaItems.get(position);
        //使用 xUtils3 绑定图片显示
       // x.image().bind(iv_netVideo,mediaItem.getImageUrl());
        //使用 Glide 绑定图片并显示
        Glide.with(mContext).load(mediaItem.getImageUrl()).into(iv_netVideo);
        tv_netDesc.setText(mediaItem.getDesc());
        tv_netName.setText(mediaItem.getName());
        return convertView;
    }
//    static class ViewHolder{
//        ImageView iv_icon;
//        TextView tv_name;
//        TextView tv_time;
//        TextView tv_size;
//    }

}
