package com.hardcopy.btchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Imagehelper{

    private static final String DATABASE_NAME="abhi.db";
    private static final int SCHEMA_VERSION=1;
    private final Context context;
    public static final String DEVICE_NAME="devicename";
    public static final String IMAGE_NAME ="imageblob";

    private SQLiteDatabase db;
    private NoteOpenHelper dbHelper;
    //////////////////////////////////////////////////////
    static final String CREATE_ALARM = "CREATE TABLE Image(_id INTEGER PRIMARY KEY AUTOINCREMENT,imageblob BLOB,devicename text);";





    //////////////////////////////////////////////////




    public Imagehelper(Context ctx) {

        context = ctx;
    }




    private static class NoteOpenHelper extends SQLiteOpenHelper {
        public NoteOpenHelper(Context c) {
            super(c, DATABASE_NAME, null, SCHEMA_VERSION);   //... DBHelper 생성자. 두번째 값은 생성할 DB의 파일명
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ALARM);

        }//데이터베이스를 생성한다.

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {//업그레이드를 하는 메서드 구현은 되지 않음.
            // TODO Auto-generated method stub
        }
    }



    public Imagehelper open() throws SQLException {
        dbHelper = new NoteOpenHelper(context);
        db = dbHelper.getWritableDatabase();//읽기, 쓰기 모드로 데이터베이스를 오픈. 만약 처음으로 호출되는 경우에는 onCreate()가 호출. 만약 데이터베이스 스키마가 달라지는 경우에는 onUpgrade()가 호출.
        return this;
    }

    public void close() {
        dbHelper.close();
    }//db를 닫는다.

    public Cursor fetchAllAlarm() {////쿼리문을 반납한다.
        return db.query("Image",null,null,null,null,null,null);

    }
    public Cursor getAll() {
        return(db.rawQuery("SELECT imageblob FROM Image",null));
    }

    public void insert(byte[] bytes, String devicename)
    {
        ContentValues cv=new ContentValues();

        cv.put("imageblob",bytes);
        cv.put("devicename",devicename);
        Log.e("inserted", "inserted");
        db.insert("Image", null, cv);//insert query 문을 실행한다.


    }
    public byte[] getImage(Cursor c)
    {
        return(c.getBlob(1));
    }
}