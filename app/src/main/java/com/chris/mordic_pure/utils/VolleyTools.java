package com.chris.mordic_pure.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;


public class VolleyTools {
	/*--------------- 盒子begin ---------------*/

	RequestQueue mQueue;
	ImageLoader mImageLoader;
	ImageCache mImageCache;
	
	/**请求队列*/
	public RequestQueue getQueue() {
		return mQueue;
	}

	/**图片加载器*/
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	/**自定义内存缓存*/
	public ImageCache getImageCache() {
		return mImageCache;
	}

	/*--------------- 盒子end ---------------*/
	private static VolleyTools	instance;	// 1.变量

	private VolleyTools(Context context) {// 2.私有化构造方法
		mQueue = Volley.newRequestQueue(context);
		mImageCache = new MyImageCache();
		mImageLoader = new ImageLoader(mQueue, mImageCache);
	};

	public static VolleyTools getInstance(Context context) {// 3.暴露公共方法
		if (instance == null) {
			synchronized (VolleyTools.class) {
				if (instance == null) {
					instance = new VolleyTools(context);
				}
			}
		}
		return instance;
	}
}
