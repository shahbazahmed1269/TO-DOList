package in.shapps.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by James on 1/13/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    // Table Name
    public static final String TABLE_NAME_TASK = "TASK1";
    public static final String TABLE_NAME_LIST = "LIST1";
    // Table columns for LIST table
    public static final String LIST_ID = "_id";
    public static final String TODO_LIST_NAME = "list_name";
    // Table columns for TASK table
    public static final String TASK_ID = "_id";
    public static final String TODO_SUBJECT = "subject";
    public static final String TODO_DATETIME="datetime";
    public static final String TODO_LIST_ID="list_id";
    public static final String TODO_DESC = "description";
    public static final String TODO_ALARM_STATUS = "alarm_status";
    public static final String TODO_TASK_STATUS= "task_status";
    public static final String TODO_TASK_FAVOURTIE= "task_favourite";
    // Database Information
    static final String DB_NAME = "SHAPPS_TODO.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating LIST table query
    private static final String CREATE_TABLE_LIST = "create table " + TABLE_NAME_LIST + "(" + LIST_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TODO_LIST_NAME + " TEXT NOT NULL);";
    // Creating TASK table query
    private static final String CREATE_TABLE_TASK = "create table " + TABLE_NAME_TASK + "(" + TASK_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TODO_SUBJECT + " TEXT, "
            + TODO_DESC + " TEXT NOT NULL, "
            +TODO_LIST_ID + " INTEGER, "
            + TODO_ALARM_STATUS + " TEXT DEFAULT 'off', "
            + TODO_DATETIME + " TEXT, "
            + TODO_TASK_STATUS + " TEXT DEFAULT 'incomplete', "
            +TODO_TASK_FAVOURTIE+ " TEXT DEFAULT 'false', "
            +"  FOREIGN KEY( " + TODO_LIST_ID+") REFERENCES "+ TABLE_NAME_LIST +"(" + LIST_ID+"));";
    public DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LIST);
        //db.rawQuery("insert into list1(list_name) values(\'general\');",null);
        db.execSQL(CREATE_TABLE_TASK);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_TASK);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_LIST);
        onCreate(db);
    }

}
