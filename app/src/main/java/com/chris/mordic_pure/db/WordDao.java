package com.chris.mordic_pure.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.chris.mordic_pure.conf.Constants;

/**
 * Created by chris on 16-5-25.
 * Email: soymantag@163.coom
 */
public class WordDao {
    private WordDbOpenHelper mWordDbOpenHelper;

    public WordDao(Context context){
        this.mWordDbOpenHelper = new WordDbOpenHelper(context);
    }


    public void add(String word,byte[] bean,String notes){
        delete(word);
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word",word);
        values.put("word_bean",bean);
        values.put("word_notes", notes);
        db.insert("WordTable",null,values);
        //insert into wordtb values (word,bean,notes)
        db.close();
    }
    public void replace(String word,byte[] bean,String notes){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word",word);
        values.put("word_bean",bean);
        values.put("word_notes", notes);
        db.replace("WordTable",null,values);
/*        db.execSQL("replace into wordtb VALUES ("
                +""+word+", "
                +""+json+","
                +""+notes+");");*/
        db.close();
    }

    public boolean delete(String word){

        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        db.delete(Constants.Database.wordtb, Constants.Database.word+"=?;",new String[]{word});
        //delete from wordtb where word=word
        db.close();
        return true;
    }

    public void update (String word,byte[] bean,String notes){
        SQLiteDatabase db = this.mWordDbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.Database.word_bean,bean);
        values.put(Constants.Database.notes,notes);
        db.update(Constants.Database.wordtb,values,Constants.Database.word+"=?",new String[]{word});
        db.close();
    }
    public byte[] getBean(String word){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+Constants.Database.word_bean +" from "+ Constants.Database.wordtb+" where "+ Constants.Database.word+" = ?",new String[]{word});
        byte[] bean;
        if(cursor.moveToNext()){//查到此单词
            bean = cursor.getBlob(0);
        }else {//数据库中没有此单词
            bean = null;
        }
        cursor.close();
        db.close();
        return bean;
    }
    public int getTotalRows(){
        SQLiteDatabase db = mWordDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from "+Constants.Database.wordtb,null);
        int totalRows =0;
        if (cursor.moveToNext())
            totalRows = cursor.getInt(0);
        cursor.close();
        db.close();
        return totalRows;
    }

    //create table tb1(name text,age integer)
    public void exeCommand(String command){
        SQLiteDatabase db = mWordDbOpenHelper.getWritableDatabase();
        db.execSQL(command);
        db.close();
    }

}
