package com.chris.mordic_pure.protocol;


import com.chris.mordic_pure.conf.Constants;
import com.chris.mordic_pure.utils.FileUtils;
import com.chris.mordic_pure.utils.IOUtils;
import com.chris.mordic_pure.utils.LogUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Administrator
 * @version $Rev: 41 $
 * @time 2015-7-16 下午4:40:08
 * @des TODO
 * @updateAuthor $Author: admin $
 * @updateDate $Date: 2015-07-19 14:23:49 +0800 (星期日, 19 七月 2015) $
 * @updateDes TODO
 */
public abstract class BaseProtocol<T> {
    public T loadData(String index) throws Exception {

		/*=============== 读取本地缓存 ===============*/
/*        T localData = getDataFromLocal(index);
        if (localData != null) {
            LogUtils.sf("###读取本地文件-->" + getCacheFile(index).getAbsolutePath());
            return localData;
        }*/

		/*=============== 发送网络请求，获取jsonString ===============*/
        String jsonString = getJsonFromNet(index);
        System.out.println("BaseProtocol jsonString:"+jsonString);
        /*=============== json解析 ===============*/
        T dataBean = parseJson(jsonString);
        return dataBean;
    }

    private T getDataFromLocal(String index) {
        /*
			//读取本地文件
			if(文件存在){
				//读取插入时间
				//判断是否过期
				if(未过期){
					//读取缓存内容
					//Json解析解析内容
					if(不为null){
						//返回并结束
					}	
				}
			}
		 */
        File cacheFile = getCacheFile(index);
        if (cacheFile.exists()) {// 文件存在
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));

                // //读取插入时间
                String timeTimeMillis = reader.readLine();
                if (System.currentTimeMillis() - Long.parseLong(timeTimeMillis) < Constants.PROTOCOLTIMEOUT) {

                    // 读取缓存内容
                    String jsonString = reader.readLine();
                    System.out.println("本地jsonstring :"+jsonString);
                    // Json解析解析内容
                    return parseJson(jsonString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(reader);
            }
        }
        return null;
    }

    public File getCacheFile(String index) {
        String dir = FileUtils.getDir("json");// sdcard/Android/data/包名/word_bean
        // 如果是详情页（DetailActivity） interfacekey+"."+包名
        Map<String, String> extraParmas = getExtraParmas();
        String name = "";
        if (extraParmas == null) {
            name = getInterfaceKey() + "." + index;// interfacekey+"."+index,
        } else {// 详情页
            for (Map.Entry<String, String> info : extraParmas.entrySet()) {
                String key = info.getKey();//"packageName"
                String value = info.getValue();//com.itheima.www
                name = getInterfaceKey() + "." + value;// interfacekey+"."+com.itheima.www
            }
        }
        File cacheFile = new File(dir, name);
        return cacheFile;
    }

    /**
     * @des 返回额外的参数
     * @call 默认在基类里面返回是null, 但是如果子类需要返回额外的参数的时候, 覆写该方法
     */
    //默认是http://localhost:8080/GooglePlayServer/interface?index=0的格式，只用传index的值就行了。
    //但app详情的activity的URL为
    //http://localhost:8080/GooglePlayServer/detail?packageName=com.itheima.www
    //这里把packageName=com.itheima.www当作额外的参数处理，代替掉一般的index=0
    public Map<String, String> getExtraParmas() {//DetailProtocol会覆写此方法
        return null;
    }

    /**
     * 从URL获取jsonString
     *
     * @param index 要查询的单词
     */
    public String getJsonFromNet(String index) throws HttpException, IOException {
        //
        HttpUtils httpUtils = new HttpUtils();
        // http://localhost:8080/GooglePlayServer/home?index=0
        //String url = URLS.BASEURL + getInterfaceKey();
        String url = Constants.URLS.DSURL;//http://open.iciba.com/dsapi/

        RequestParams parmas = new RequestParams();
        // http://localhost:8080/GooglePlayServer/detail?packageName=com.itheima.www
        // http://localhost:8080/GooglePlayServer/interface?index=0
        Map<String, String> extraParmas = getExtraParmas();
        if (extraParmas == null) {
            switch (getInterfaceKey()) {
                case "daily":
                    url = Constants.URLS.DSURL;
                    if(index!=null){

                        //在url后面添加index=0格式的字符串
                        parmas.addQueryStringParameter("date", index + "");
                        //parmas.addQueryStringParameter("key",Constants.URLS.JINSHANKEY);
                    }
                    break;
                case "gank":
                    url = Constants.URLS.GANKURL+index;
                    break;
                case "gankpic":
                    url = Constants.URLS.GANKPIC+index;
                    break;
                default:
                    break;
            }
        } else {// 子类覆写了getExtraParmas(),返回了额外的参数


            switch (getInterfaceKey()) {
                case "word":
                    //http://dict-co.iciba.com/api/dictionary.php?w=go&type=json&key=B2EC2F4BEC106D78BE4A3D61FBDB91C9
                    url = Constants.URLS.BASEURL;//http://dict-co.iciba.com/api/dictionary.php
                    parmas.addQueryStringParameter("w", index);
                    // 取出额外的参数
                    for (Map.Entry<String, String> info : extraParmas.entrySet()) {
                        String name = info.getKey();// 参数的key
                        String value = info.getValue();// 参数的value

                        parmas.addQueryStringParameter(name, value);
                    }
                    break;
            }

        }

        //注意此方法是在LoadPager.java中的LoadDataTask子线程中运行的，所以用sendSync（sendSync表示同步，而不另开子线程）
        ResponseStream responseStream = httpUtils.sendSync(HttpMethod.GET, url, parmas);
        System.out.println("before log");
        LogUtils.i(Constants.URLS.HTTP, responseStream.getRequestUrl()+"");// 打印请求的url
        System.out.println("after log");
        System.out.println("*******httpurl:"+responseStream.getRequestUrl());
        // 读取网络数据
        String jsonString = responseStream.readString();
        LogUtils.i(Constants.URLS.HTTP, jsonString);// 打印返回的结果
		/*--------------- 保存网络数据到本地 ---------------*/
        File cacheFile = getCacheFile(index);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(cacheFile));
            writer.write(System.currentTimeMillis() + "");// 第一行写入插入时间
            writer.write("\r\n");// 换行
            writer.write(jsonString);
        } catch (IOException e) {
            System.out.println("******io exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("******http exception");
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
            responseStream.close();
        }
		/*	
			if(网络数据加载成功){
				//加载网络数据
				//保存网络数据到本地
				//Json解析内容
				//返回并结束
		
			}else{
				//返回null
			}*/
        System.out.println("*******json:" + jsonString);
        return jsonString;
    }

    public abstract String getInterfaceKey();

    public abstract T parseJson(String jsonString);

}
