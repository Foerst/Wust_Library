package com.chen.library.NetWorkState;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 
 * @author chen
 * 	// 获取网络状态，要求一定的权限，详情见应用配置文件
 * 该类获取网络状态
 *
 */
public class NetworkUtils {

	
	
	/****
	 * 
	 * @param context 上下文对象
	 * @return boolean，代表网络连接状态
	 */
	public static  boolean getNetWorkState(Context context) {
		ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// do nothing;do what is required to do;
			return true;
		} else {
			return false;
		}
	}

}
