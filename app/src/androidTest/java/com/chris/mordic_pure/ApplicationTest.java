package com.chris.mordic_pure;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.chris.mordic_pure.db.WordbookDao;
import com.chris.mordic_pure.db.WordDao;
import com.chris.mordic_pure.db.WordbookListDao;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    public void test_create() throws Exception {
        WordbookDao dao = new WordbookDao(getContext());
        dao.createTable("testtb1");
    }
    public void test_add() throws Exception {
        WordbookDao dao = new WordbookDao(getContext());
        for(int i=0;i<100;i++){
            dao.add("testtb1","apple"+i,"unlearned");
        }
    }
    public void test_getwords() throws Exception {
        WordDao dao = new WordDao(getContext());
        System.out.println("totalrow:"+dao.getTotalRows());
    }
    public void test_getrows() throws Exception {
        WordDao dao = new WordDao(getContext());
        System.out.println("totalrow:"+dao.getTotalRows());
    }

    public void test_replace() throws Exception {
        WordbookDao dao = new WordbookDao(getContext());
            dao.replace("testtb1",1,null,"unlearned");

    }

    public void test_add1() throws Exception {
        WordbookListDao dao = new WordbookListDao(getContext());
        for(int i=0;i<100;i++){
            dao.add("book"+i,0,0,0);
        }
    }
    public void test_getall1() throws Exception {
        WordbookListDao dao = new WordbookListDao(getContext());
        System.out.println(dao.getAllWordbook());
    }
    public void test_del1() throws Exception {
        WordbookListDao dao = new WordbookListDao(getContext());
       dao.delete("book1");
    }
    public void test_update1() throws Exception {
        WordbookListDao dao = new WordbookListDao(getContext());
        dao.update("book0",0,0,1);
    }
    public void test_getdata1() throws Exception {
        WordbookListDao dao = new WordbookListDao(getContext());
        System.out.println(dao.getData("book0"));
    }

}