package com.chris.mordic.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class MyImageCache implements ImageCache {
	LruCache<String, Bitmap> mLruCache;
	// 存储结构/容器/集合缓存的最大值
	// 1.告知缓存的具体大小
	private int					maxSize	= 5 * 1024 * 1024;	// 5242880 byte
															// private int maxSize = 5; // 5m

	public MyImageCache() {
		mLruCache = new LruCache<String, Bitmap>(maxSize) {
			// 2.覆写sizeOf方法
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 返回每一个entry对应的大小
				// 具体大小需要和我们定义的maxsize单位统一
				// return value.getByteCount();
				return value.getByteCount();
			}
		};
	}

	@Override
	public Bitmap getBitmap(String url) {// 取图片
		return mLruCache.get(url);//key=url,取value
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {// 存图片
		mLruCache.put(url, bitmap);//key=url,value=bitmap
	}

}
