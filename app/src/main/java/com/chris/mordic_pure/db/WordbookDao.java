package com.chris.mordic_pure.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 7/12/16.
 * Email: soymantag@163.coom
 */
public class WordbookDao {
    private WordDbOpenHelper mWordDbOpenHelper;

    public WordbookDao(Context context) {
        mWordDbOpenHelper = new WordDbOpenHelper(context);
    }
    public void createTable(String table){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        //db.execSQL("create table wordtb(word text primary key,word_bean BLOB,word_notes text)");
        db.execSQL("create table "+table+"(_id integer primary key autoincrement,word text,learn_state text)");
        db.close();
    }
    public void add (String wordbook,String word,String learnState){
        delete(wordbook,word);
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word",word);
        values.put("learn_state",learnState);
        db.insert(wordbook,null,values);
        db.close();
    }
    public void replace (String wordbook,int index,String word,String learnState){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("_id",index);
        values.put("learn_state",learnState);
        if(word==null){
            db.replace(wordbook,null,values);//word没有赋值,但系统会自动赋值null.所以结果和下面else一样.
        }else {
            values.put("word",word);
            db.replace(wordbook,null,values);
        }
        db.close();
    }
    public void delete(String wordbook,String word){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        db.delete(wordbook,"word=?",new String[]{word});
        db.close();
    }
    public void update(String wordbook,String word,String learn_state){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("learn_state", learn_state);
        db.update(wordbook,values,"word=?",new String[]{word});
        db.close();
    }
    public int getTotalRows(String table){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from "+table,null);
        int totalRows =0;
        if(cursor.moveToNext())
        totalRows = cursor.getInt(0);
        cursor.close();
        db.close();
        return totalRows;
    }
    public List<String> getAllWords(String table){
        List<String> words = new ArrayList<>();
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select word from "+table,null);

        while(cursor.moveToNext()){
            words.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return words;
    }
    public String getLearnState(String table,String word){
        String state="";
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select learn_state from "+table+" where word=?" ,new String[]{word});
        while(cursor.moveToNext()){
            state=cursor.getString(0);
        }
        cursor.close();
        db.close();
        return state;
    }
    public List<String> getUnlearnedWords(String table){
        List<String> words = new ArrayList<>();
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select word from "+table+" where learn_state='unlearned'",null);
        while(cursor.moveToNext()){
            words.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return words;
    }
    public int getUnlearnedWordsNum(String table){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from "+table+" where learn_state='unlearned'",null);
        int num =0;
        if(cursor.moveToNext()){
            num =cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return num;
    }
    public List<String> getReciteWords(String table){
        List<String> words = new ArrayList<>();
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select word from "+table+" where learn_state='recite'",null);
        while(cursor.moveToNext()){
            words.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return words;
    }
    public int getReciteWordsNum(String table){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from "+table+" where learn_state='recite'",null);
        int num =0;
        if(cursor.moveToNext()){
            num =cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return num;
    }
    public List<String> getLearnedWords(String table){
        List<String> words = new ArrayList<>();
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select word from "+table+" where learn_state='learned'",null);
        while(cursor.moveToNext()){
            words.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return words;
    }
    public int getLearnedWordsNum(String table){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from "+table+" where learn_state='learned'",null);
        int num =0;
        if(cursor.moveToNext()){
            num =cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return num;
    }

}
