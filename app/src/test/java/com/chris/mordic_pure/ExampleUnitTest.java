package com.chris.mordic_pure;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void test() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public  void getpreDate() {
        String currentDate = "2013-05-01";int dayOffset =1 ;
        try {
            String predate;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(currentDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            for(int i =0;i<dayOffset;i++){
                cal.add(Calendar.DATE, -1);//日期减一天
            }
            date = cal.getTime();
            predate = simpleDateFormat.format(date);
            System.out.println(predate);

        } catch (ParseException e) {
            e.printStackTrace();

        }
    }
    @Test
    public void str(){
        String str = "2015/08/06";
        System.out.println("string = " + str);
        String replaceStr = str.replace('/', '-');
        System.out.println("new string = " + replaceStr);
        System.out.println(String.valueOf(10));
    }
    @Test
    public void substr(){
        String string = "2016-06-08T11:56:11.8Z";

        System.out.println(string.substring(0,10));
        System.out.println(string.substring(4));
    }

}