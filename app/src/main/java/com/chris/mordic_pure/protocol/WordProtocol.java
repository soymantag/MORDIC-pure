package com.chris.mordic_pure.protocol;

import com.chris.mordic_pure.conf.Constants;
import com.chris.mordic_pure.data.WordBean;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chris on 7/9/16.
 * Email: soymantag@163.coom
 */
public class WordProtocol extends BaseProtocol<WordBean> {
    @Override
    public String getInterfaceKey() {
        return "word";
    }

    @Override
    public Map<String, String> getExtraParmas() {
        Map<String, String> extraParmas = new HashMap<String, String>();
        extraParmas.put("type", "json");
        extraParmas.put("key", Constants.JINSHANKEY);
        return extraParmas;
    }

    @Override
    public WordBean parseJson(String jsonString) {
        Gson gson = new Gson();
        //http://localhost:8080/GooglePlayServer/home?index=0
        return gson.fromJson(jsonString, WordBean.class);
    }
}
