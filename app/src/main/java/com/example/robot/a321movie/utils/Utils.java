package com.example.robot.a321movie.utils;

import android.os.Message;

import java.util.Formatter;
import java.util.Locale;

import static android.net.TrafficStats.getTotalRxBytes;

public class Utils {

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	private long lastTotalRxBytes = 0 ;
	private long lastTimeStamp = 0;

	public Utils() {
		// 转换成字符串的时间
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

	}

	/**
	 * 把毫秒转换成：1:20:30这里形式
	 * @param timeMs
	 * @return
	 */
	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;

		int minutes = (totalSeconds / 60) % 60;

		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
					.toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	/**
	 * 判断传入的 uri 是否有效
	 * @param uri
	 * @return
     */
	public boolean isNetUri(String uri){
		boolean result=false;
		if(uri.toLowerCase().startsWith("http")||uri.toLowerCase().startsWith("rtsp")||
				uri.toLowerCase().startsWith("mms")){
			result=true;
		}
		return result;
	}

	/**
	 * 实时显示网速
	 */
	public String showNetSpeed() {
		String netSpeed="0 kb/s";
		long nowTotalRxBytes = getTotalRxBytes();
		long nowTimeStamp = System.currentTimeMillis();
		long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
		netSpeed=String.valueOf(speed)+" kb/s";
		return netSpeed;
	}
}
