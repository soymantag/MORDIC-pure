package com.chris.mordic_pure.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chris on 16-5-25.
 * Email: soymantag@163.coom
 */
public class WordDbOpenHelper extends SQLiteOpenHelper{

    public WordDbOpenHelper(Context context) {
        super(context, "wordDb", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("create table wordtb(word text primary key,word_bean BLOB,word_notes text)");
        db.execSQL("create table WordTable(word text primary key,word_bean BLOB,word_notes text)");
        db.execSQL("create table WordbookListTable(_id integer primary key autoincrement," +
                "wordbook text,index_disordered integer,index_ordered integer,word_sum integer)");
        db.execSQL("create table recite_table(_id integer primary key autoincrement,word text)");
        db.execSQL("create table learned_table(_id integer primary key autoincrement,word text)");
        db.execSQL("create table unlearned_table(_id integer primary key autoincrement,word text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table WordbookTable");
        onCreate(db);
    }

}
