package com.example.robot.a321movie.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.robot.a321movie.Activity.SystemVideoPlayer;
import com.example.robot.a321movie.Adapter.NetVideoAdapter;
import com.example.robot.a321movie.Base.BaseFragment;
import com.example.robot.a321movie.Base.Constant;
import com.example.robot.a321movie.R;
import com.example.robot.a321movie.View.XListView;
import com.example.robot.a321movie.domain.MediaItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by robot on 2017/11/22.
 * 网络视屏
 */

public class NetVideoFragment extends BaseFragment {
    ArrayList<MediaItem> mediaItems;
    /**
     * 使用 xUtils3 注解初始化控件
     */
    @ViewInject(R.id.lv_netvideo)
    private XListView mListView;

    @ViewInject(R.id.tv_nonet)
    private TextView mTextView;

    @ViewInject(R.id.pb_netloading)
    private ProgressBar mProgressBar;

    /**
     * 判断是否是加载更多
     */
    private boolean isLoadMore=false;
    private NetVideoAdapter netVideoAdapter;

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.netvideo_fragment, null);
        //第一个参数是：NetVideoFragment.this 第二个参数:布局
        x.view().inject(NetVideoFragment.this, view);
        MyOnItemClickListener listener=new MyOnItemClickListener();
        mListView.setOnItemClickListener(listener);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(new MyXListViewListener());
        return view;
    }
    class MyXListViewListener implements  XListView.IXListViewListener{

        /**
         * 上拉刷新
         */
        @Override
        public void onRefresh() {
            getDataForNet();//联网请求方法
            onLoad();//得到系统时间
        }

        /**
         * 加载更多
         */
        @Override
        public void onLoadMore() {
            getMoreDataForNet();
        }
    }

    /**
     * 加载更多
     */
    private void getMoreDataForNet() {
        RequestParams params = new RequestParams(Constant.NET_URL);
        //使用 xUtils 进行联网请求
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isLoadMore=true;
                //解析 JSON 数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(getSystemTime());
    }

    private String getSystemTime() {
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //3、传递播放列表数据到 SystemVideo 中去（传递列表数据---对象----序列化）
            Intent intent = new Intent(mContext,SystemVideoPlayer.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);

            //还得传递要播放的列表中的具体位置
            intent.putExtra("position", position-1);//因为加载了下拉刷新头
            startActivity(intent);
        }
    }

    @Override
    public void initDate() {
        super.initDate();
        getDataForNet();
    }

    private void getDataForNet() {
        RequestParams params = new RequestParams(Constant.NET_URL);
        //使用 xUtils 进行联网请求
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //解析 JSON 数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 使用系统接口解析 JSON　并绑定数据
     *
     * @param json
     */
    private void processData(String json) {
        if( !isLoadMore ){
            //加载数据
            mediaItems = parseJson(json);
            showData();
        }else {
            //加载更多，把要加载的数据添加到原来的集合中
//            ArrayList<MediaItem> moreDatas=parseJson(json);//重新解析得到的数据
            mediaItems.addAll(parseJson(json));//将得到的数据加载到原来的数据集合中
            //刷新适配器
            netVideoAdapter.notifyDataSetChanged();

            onLoad();
            isLoadMore=false;
        }

    }

    /**
     * 显示数据
     */
    private void showData() {
        //设置适配器
        if(mediaItems!=null&&mediaItems.size()>0){
            //有数据
            //设置适配器
            netVideoAdapter=new NetVideoAdapter(mContext,mediaItems);
            mListView.setAdapter(netVideoAdapter);
            //隐藏文本
            mTextView.setVisibility(View.GONE);
        }else {
            //没有数据
            //显示文本
            mTextView.setVisibility(View.VISIBLE);
        }
        //progressBar 隐藏
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 使用系统提供的 json 解析数据
     *
     * @param result
     * @return
     */
    private ArrayList<MediaItem> parseJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if (jsonObjectItem != null) {
                        MediaItem mediaItem = new MediaItem();
                        String movieName = jsonObjectItem.optString("movieName");
                        mediaItem.setName(movieName);

                        String videoTitle = jsonObjectItem.optString("summary");//desc
                        mediaItem.setDesc(videoTitle);

                        String imageUrl = jsonObjectItem.optString("coverImg");//imageUrl
                        mediaItem.setImageUrl(imageUrl);

                        String hightUrl = jsonObjectItem.optString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        mediaItems.add(mediaItem);//将数据添加到集合中去
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

}
