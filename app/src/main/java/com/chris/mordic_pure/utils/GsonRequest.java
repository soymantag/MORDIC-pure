package com.chris.mordic_pure.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

//自定义一个gson的request,参考StringRequest
public class GsonRequest<T> extends Request<T> {
	Class<T>			clazz;		// 存放解析结果的具体bean的类型
	private Listener<T> mListener;
//多加一个Class<T> clazz，用来传入保存结果的bean
	public GsonRequest(int method, String url, ErrorListener listener, Listener<T> mListener, Class<T> clazz) {
		super(method, url, listener);
		this.clazz = clazz;
		this.mListener = mListener;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		String jsonString;
		try {
			jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			jsonString = new String(response.data);
		}
		// jsonString-->XXX.bean
		T obj;
		try {
			Gson gson = new Gson();
			obj = gson.fromJson(jsonString, clazz);
			// 返回结果
			return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));//HttpHeaderParser.parseCacheHeaders(response)是解析成一个可缓存的条目
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			// 返回结果
			return Response.error(new ParseError());
		}
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}
}
