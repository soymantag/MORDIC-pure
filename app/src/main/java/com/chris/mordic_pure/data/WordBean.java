package com.chris.mordic_pure.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chris on 7/9/16.
 * Email: soymantag@163.coom
 */
public class WordBean implements Serializable{
    public String word_name;
    public String is_CRI;
    public class exchange implements Serializable{
        public List<String> word_pl;//复数
        public List<String> word_third;//第三人称单数
        public List<String> word_past;//过去式
        public List<String> word_done;//过去分词
        public List<String> word_ing;//现在分词
        public String word_er;//比较级
        public String word_est;//最高级
    }
    public List<symbol> symbols;
    public class symbol implements Serializable{
        public String ph_en;//英
        public String ph_am;//美
        public String ph_other;//其他
        public String ph_en_mp3;
        public String ph_am_mp3;
        public String ph_tts_mp3;
        public List<Part> parts;


    }
    public class Part implements Serializable{
        public String part;
        public List<String> means;
    }



}
