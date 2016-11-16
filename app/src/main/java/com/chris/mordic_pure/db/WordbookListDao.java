package com.chris.mordic_pure.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chris.mordic_pure.conf.Constants;
import com.chris.mordic_pure.data.WordbookBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/12/16.
 * Email: soymantag@163.coom
 */
public class WordbookListDao {
    private WordDbOpenHelper mWordDbOpenHelper;

    public WordbookListDao(Context context) {
        mWordDbOpenHelper = new WordDbOpenHelper(context);
    }

    public void add (String wordbook,int index_disordered,int index_ordered,int sum){
        delete(wordbook);
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("wordbook",wordbook);
        values.put("index_disordered",index_disordered);
        values.put("index_ordered",index_ordered);
        values.put("word_sum",sum);
        db.insert(Constants.Database.WordbookListTable,null,values);
        db.close();
    }
    public void replace (int index,String wordbook){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(wordbook==null){
            db.replace(Constants.Database.WordbookListTable,null,values);//word没有赋值,但系统会自动赋值null.所以结果和下面else一样.
        }else {
            values.put("wordbook",wordbook);
            db.replace(Constants.Database.WordbookListTable,null,values);
        }
        db.close();
    }
    public void delete(String wordbook){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        db.delete(Constants.Database.WordbookListTable,"wordbook=?",new String[]{wordbook});
        db.close();
    }
    public void update (String wordbook,int index_disordered,int index_ordered,int sum){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("wordbook",wordbook);
        values.put("index_disordered",index_disordered);
        values.put("index_ordered",index_ordered);
        values.put("word_sum",sum);
        db.update(Constants.Database.WordbookListTable,values,"wordbook=?",new String[]{wordbook});
        db.close();
    }
    public int getTotalRows(){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from "+Constants.Database.WordbookListTable,null);
        cursor.moveToNext();
        int totalRows = cursor.getInt(0);
        cursor.close();
        db.close();
        return totalRows;
    }
    public List<WordbookBean> getAllWordbook(){
        List<WordbookBean> Wordbooks = new ArrayList<>();
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select wordbook,index_disordered,index_ordered,word_sum from "+Constants.Database.WordbookListTable+" order by _id desc",null);
        while(cursor.moveToNext()){
            WordbookBean wordbookBean=new WordbookBean();
            wordbookBean.setBookName(cursor.getString(0));
            wordbookBean.setIndex_disordered(cursor.getInt(1));
            wordbookBean.setIndex_ordered(cursor.getInt(2));
            wordbookBean.setSum(cursor.getInt(3));
            Wordbooks.add(wordbookBean);
        }
        cursor.close();
        db.close();
        return Wordbooks;
    }
    public WordbookBean getData(String wordbook){

        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select index_disordered,index_ordered,word_sum from "
                +Constants.Database.WordbookListTable+" where wordbook=?",new String[]{wordbook});
        WordbookBean wordbookBean=null;
        if(cursor.moveToNext()){
            wordbookBean=new WordbookBean(wordbook,cursor.getInt(0),cursor.getInt(1),cursor.getInt(2));
        }

        cursor.close();
        db.close();
        return wordbookBean;
    }
    public void deleteWordbook(String wordbook){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        db.execSQL("drop table "+wordbook);
        delete(wordbook);
        db.close();
    }

}
