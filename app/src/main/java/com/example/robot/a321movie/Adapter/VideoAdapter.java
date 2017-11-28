package com.example.robot.a321movie.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.robot.a321movie.R;
import com.example.robot.a321movie.domain.MediaItem;
import com.example.robot.a321movie.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by robot on 2017/11/26.
 */

public class VideoAdapter extends BaseAdapter {

    private ImageView iv_icon;
    private TextView tv_name;
    private TextView tv_time;
    private TextView tv_size;

    private Context mContext;
    private ArrayList<MediaItem> mediaItems;

    private Utils utils;

    public VideoAdapter(Context mContext, ArrayList<MediaItem> mediaItems) {
        this.mContext = mContext;
        this.mediaItems = mediaItems;
        utils=new Utils();
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

            convertView = convertView.inflate(mContext, R.layout.lv_video, null);
            iv_icon = convertView.findViewById(R.id.iv_icon);
            tv_name = convertView.findViewById(R.id.tv_name);
            tv_time = convertView.findViewById(R.id.tv_time);
            tv_size = convertView.findViewById(R.id.tv_size);

            //根据 position 得到列表中对应位置的数据
            MediaItem mediaItem = mediaItems.get(position);
            tv_name.setText(mediaItem.getName());
            tv_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
            tv_size.setText(Formatter.formatFileSize(mContext, mediaItem.getSize()));
            return convertView;
        }
//    static class ViewHolder{
//        ImageView iv_icon;
//        TextView tv_name;
//        TextView tv_time;
//        TextView tv_size;
//    }

}
