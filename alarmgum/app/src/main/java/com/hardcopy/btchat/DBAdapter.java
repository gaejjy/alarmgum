package com.hardcopy.btchat;

/**
 * Created by MoonJeein on 2015-12-15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


//1. SQLiteOpenHelper class 생성
//2. SQLiteDatabase 객체 생성
public class DBAdapter {
    private final Context context;
    static final String DB = "nabakalarm";//스키마의 이름이다.
    static final String TABLE_ALARM = "alarm";//테이블의 이름이다.

    //////////////////////////////////////////////////////////////////
    static final int DB_VERSION = 1;



    // alarm table 상수들의 모음이다.
    public static final String ALARM_ON        = "onoff";     // ON / OFF
    public static final String PRODUCT_NAME ="productname";
    public static final String DEVICE_NAME="devicename";
    public static final String ALARM_RINGTONE  = "ring";   // ����
    public static final String ALARM_VIBRATE   = "vibrate";// vibrate











    ///////////////////////////////////////////////////////////////////
    static final String CREATE_ALARM = "create table " + TABLE_ALARM +
            " (_id integer primary key autoincrement, " +
            ALARM_ON + " integer, " +
            PRODUCT_NAME + " text, "+
            DEVICE_NAME + " text, "+
            ALARM_VIBRATE + " integer, " +
            ALARM_RINGTONE + " text);";


    static final String DROP = "drop table ";
    private SQLiteDatabase db;
    private NoteOpenHelper dbHelper;

    public DBAdapter(Context ctx) {// 디비를 관리하는 DBAdapter class
        context = ctx;//DB Adapter는 데이터베이스에 접근하여 수행하는 작업들을 추상화시켜주는 역할을 합니다.
    }

    private static class NoteOpenHelper extends SQLiteOpenHelper {
        public NoteOpenHelper(Context c) {
            super(c, DB, null, DB_VERSION);   //... DBHelper 생성자. 두번째 값은 생성할 DB의 파일명
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

    public DBAdapter open() throws SQLException {
        dbHelper = new NoteOpenHelper(context);
        db = dbHelper.getWritableDatabase();//읽기, 쓰기 모드로 데이터베이스를 오픈. 만약 처음으로 호출되는 경우에는 onCreate()가 호출. 만약 데이터베이스 스키마가 달라지는 경우에는 onUpgrade()가 호출.
        return this;
    }

    public void close() {
        dbHelper.close();
    }//db를 닫는다.

    public Cursor fetchAllAlarm() {////쿼리문을 반납한다.
        return db.query(TABLE_ALARM, null, null, null, null, null, PRODUCT_NAME + " asc, " +DEVICE_NAME + " asc");
		/*안드로이드에서는 DB에서 가져온 데이터를 쉽게 처리하기 위해서 Cursor 라는 인터페이스를 제공해 줍니다. Cursor는 기본적으로 DB에서 값을 가져와서 마치 실제 Table의 한 행(Row), 한 행(Row)
		을 참조하는 것 처럼 사용 할 수 있게 해 줍니다. 개발자는 마치 그 행(Row) 을 가지고 행(Row)에 있는 데이터를 가져다가 쓰는 것 처럼 사용하면 되는 편의성을 제공받게 됩니다.
		 */
    }
    ////////////////////////////////////////////////////////////

    public Cursor fetchAllPhoto() {////쿼리문을 반납한다.
        return db.query(TABLE_ALARM, null, null, null, null, null, PRODUCT_NAME + " asc, " +DEVICE_NAME + " asc");
		/*안드로이드에서는 DB에서 가져온 데이터를 쉽게 처리하기 위해서 Cursor 라는 인터페이스를 제공해 줍니다. Cursor는 기본적으로 DB에서 값을 가져와서 마치 실제 Table의 한 행(Row), 한 행(Row)
		을 참조하는 것 처럼 사용 할 수 있게 해 줍니다. 개발자는 마치 그 행(Row) 을 가지고 행(Row)에 있는 데이터를 가져다가 쓰는 것 처럼 사용하면 되는 편의성을 제공받게 됩니다.
		 */
    }
    public Cursor fetchAllIndex() {////쿼리문을 반납한다.
        return db.query(TABLE_ALARM, null, null, null, null, null, PRODUCT_NAME + " asc, " +DEVICE_NAME + " asc");
		/*안드로이드에서는 DB에서 가져온 데이터를 쉽게 처리하기 위해서 Cursor 라는 인터페이스를 제공해 줍니다. Cursor는 기본적으로 DB에서 값을 가져와서 마치 실제 Table의 한 행(Row), 한 행(Row)
		을 참조하는 것 처럼 사용 할 수 있게 해 줍니다. 개발자는 마치 그 행(Row) 을 가지고 행(Row)에 있는 데이터를 가져다가 쓰는 것 처럼 사용하면 되는 편의성을 제공받게 됩니다.
		 */
    }






    //////////////////////////////////////////////////////////////
    public String addAlarm(int on, String devicename,String productname, int vib, String ring) {//데이터베이스에 추가한다.
        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);
        /*values.put(ALARM_APDAY, day);
        values.put(ALARM_HOUR, hour);
        values.put(ALARM_MINUTE, min);*/
        values.put(PRODUCT_NAME,productname);
        values.put(DEVICE_NAME,devicename);
        values.put(ALARM_RINGTONE, ring);
        values.put(ALARM_VIBRATE, vib);

        long id = db.insert(TABLE_ALARM, null, values);//insert query 문을 실행한다.
        if (id < 0) {
            return "";
        }
        return Long.toString(id);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    public void delAlarm(String id) {
        db.delete(TABLE_ALARM, "_id = ?", new String[] {id});
    }//데이터베이스에서 삭제하는 메서드이다.

    public void modifyAlarm(long id, int on, String devicename, String productname, int vib, String ring) {
        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);
        /*
        values.put(ALARM_APDAY, day);
        values.put(ALARM_HOUR, hour);
        values.put(ALARM_MINUTE, min);
        */
        values.put(PRODUCT_NAME,productname);
        values.put(DEVICE_NAME,devicename);
        values.put(ALARM_RINGTONE, ring);
        values.put(ALARM_VIBRATE, vib);

        db.update(TABLE_ALARM, values, "_id" + "='" + id + "'", null);//updqte 쿼리문을 실행한다.
    }

    public void modifyAlarmOn(long id, int on) {
        ContentValues values = new ContentValues();
        values.put(ALARM_ON, on);

        db.update(TABLE_ALARM, values, "_id" + "='" + id + "'", null);
    }



}
