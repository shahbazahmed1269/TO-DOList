package in.shapps.todoapp;
/**
 * Created by James on 2/6/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLController {
    private in.shapps.todoapp.DBHelper dbHelper;
    private Context ourContext;
    private SQLiteDatabase db;

    public SQLController(Context c) {
        ourContext=c;
    }
    public SQLController open() throws SQLException {
        dbHelper=new in.shapps.todoapp.DBHelper(ourContext);
        db=dbHelper.getWritableDatabase();
        return this;
    }
    public Cursor query (
            String table, String[] columns, String selection, String[] selectionArgs, String groupBy,
            String having, String orderBy, String limit
    ){
        Cursor cursor=db.rawQuery("select * from "+ in.shapps.todoapp.DBHelper.TABLE_NAME_TASK,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public void close() {
        dbHelper.close();
    }
    public int insertTask(ContentValues contentValue) {
        db.insert(in.shapps.todoapp.DBHelper.TABLE_NAME_TASK, null, contentValue);
        Cursor c=db.rawQuery("SELECT last_insert_rowid()", null);
        c.moveToFirst();
        return c.getInt(0);
    }
    public Cursor fetchAllTask(int listId) {
        Cursor cursor=db.rawQuery(
                "select * from "+ in.shapps.todoapp.DBHelper.TABLE_NAME_TASK+" where "+
                        in.shapps.todoapp.DBHelper.TODO_LIST_ID+"="+listId+";",null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchTask(int listId,int taskId) {
        String[] columns = new String[] { in.shapps.todoapp.DBHelper.TASK_ID };
        Cursor cursor=db.rawQuery(
                "select * from " + in.shapps.todoapp.DBHelper.TABLE_NAME_TASK + " where " +
                        in.shapps.todoapp.DBHelper.TODO_LIST_ID + "=" + listId + " AND " +
                        DBHelper.TASK_ID + "=" + taskId + ";", null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetchTimes() {
        Cursor cursor=db.rawQuery(
                "select "+DBHelper.TODO_DATETIME+", "+DBHelper.TODO_DESC+" from " +
                        in.shapps.todoapp.DBHelper.TABLE_NAME_TASK + " where " +
                        DBHelper.TODO_ALARM_STATUS + "=\'on\'", null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public int updateTask(long _id, ContentValues contentValue) {
        int i = db.update(in.shapps.todoapp.DBHelper.TABLE_NAME_TASK, contentValue,
                in.shapps.todoapp.DBHelper.TASK_ID + " = " + _id, null);
        return (int)_id;
    }

    public int deleteTask(long _id) {
        db.delete(
                in.shapps.todoapp.DBHelper.TABLE_NAME_TASK,
                in.shapps.todoapp.DBHelper.TASK_ID + "=" + _id,
                null
        );
        return (int)_id;
    }

    public int insertList(ContentValues contentValue) {
        db.insert(in.shapps.todoapp.DBHelper.TABLE_NAME_LIST, null, contentValue);
        Cursor c=db.rawQuery("SELECT last_insert_rowid()",null);
        c.moveToFirst();
        return c.getInt(0);
    }
    public void deleteList(long _id) {
        db.delete(
                in.shapps.todoapp.DBHelper.TABLE_NAME_LIST,
                in.shapps.todoapp.DBHelper.LIST_ID + "=" + _id,
                null
        );
    }
    public Cursor fetchAllList() {
        String[] columns = new String[] {
                in.shapps.todoapp.DBHelper.TASK_ID,
                in.shapps.todoapp.DBHelper.TODO_LIST_NAME
        };
        Cursor cursor = db.query(in.shapps.todoapp.DBHelper.TABLE_NAME_LIST, columns, null,
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchList(int listId) {
        String[] columns = new String[] { DBHelper.TASK_ID, DBHelper.TODO_LIST_NAME};

        Cursor cursor = db.query(
                DBHelper.TABLE_NAME_LIST, columns, DBHelper.LIST_ID, new String[]{listId+""},
                null, null, null
        );
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public int deleteList(int listId) {
        db.delete(DBHelper.TABLE_NAME_TASK, DBHelper.TODO_LIST_ID + "=" + listId, null);
        db.delete(DBHelper.TABLE_NAME_LIST, DBHelper.LIST_ID + "=" + listId, null);
        return listId;
    }
}
