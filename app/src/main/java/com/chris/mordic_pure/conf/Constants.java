package com.chris.mordic_pure.conf;

/**
 * Email: soymantag@163.coom
 */
public class Constants {

    public static final int PROTOCOLTIMEOUT = 60 * 60 * 1000;        // 1小时
    public static final int PAGESIZE = 10;
    public static final String JINSHANKEY = "B2EC2F4BEC106D78BE4A3D61FBDB91C9";

    public static final class URLS {
        public static final String BASEURL = "http://dict-co.iciba.com/api/dictionary.php";
        public static final String JINSHANKEY = "B2EC2F4BEC106D78BE4A3D61FBDB91C9";
        public static final String DSURL = "http://open.iciba.com/dsapi/";
        public static final String GANKURL = "http://gank.io/api/day/";
        public static final String GANKPIC = "http://gank.io/api/data/福利/" + PAGESIZE + "/";//http://gank.io/api/data/福利/10/1  第一页，每次10张
        public static final String HTTP = "HTTP";
    }

    public static final class URLType {
        public static final int DAILY = 0;
        public static final int GANK = 1;
        public static final int WORD = 2;
    }

    public static final class Database {
        public static final String wordtb = "WordTable";
        public static final String id = "_id";
        public static final String word = "word";
        public static final String word_bean = "word_bean";
        public static final String notes = "word_notes";
        public static final String WordbookListTable = "WordbookListTable";
        public static final String flag = "FLAG";
    }

    public static String SPFILE = "config";//sp的配置文件名字
    public static String IsWordListImported = "IsWordListImported";

    public static final class wordbook {

        public static final String learn_state = "learn_state";
        public static final String unlearned = "unlearned";
        public static final String learned = "learned";
        public static final String recite = "recite";


    }
}
