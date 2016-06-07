package in.shapps.todoapp;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.*;
import java.util.List;

public class TaskProvider extends ContentProvider {
    private static final String CONTENT_AUTHORITY = "in.shapps.todoapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIST = "list1";
    public static final String PATH_TASK = "task1";
    private static final Uri LIST_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_LIST);
    private static final Uri TASK_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_TASK);
    private static final int LIST = 1;
    private static final int LIST_ID = 2;
    private static final int TASK = 3;
    private static final int TASK_ALL = 4;
    private SQLController dbController;
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;
    private static final UriMatcher uriMatcher = getUriMatcher();
    private static UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, "list1", LIST);
        uriMatcher.addURI(CONTENT_AUTHORITY, "list1/#", LIST_ID);
        uriMatcher.addURI(CONTENT_AUTHORITY, "task1/#", TASK);
        uriMatcher.addURI(CONTENT_AUTHORITY, "task1", TASK_ALL);

        return uriMatcher;
    }
    @Override
    public boolean onCreate() {
        dbController=new SQLController(getContext());
        dbController.open();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case TASK:
                java.util.List<String>l1=uri.getPathSegments();
                int listId=Integer.parseInt(l1.get(1));
                cursor=dbController.fetchAllTask(listId);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case TASK_ALL:
                cursor=dbController.query(DBHelper.TABLE_NAME_TASK, projection, selection,
                        selectionArgs, null, null, sortOrder, null);
                /*cursor=dbController.query(DBHelper.TABLE_NAME_TASK, columns, selection,
                        selectionArgs, groupBy, having, orderBy, limit);*/
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case LIST:
                cursor=dbController.fetchAllList();
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case LIST_ID:
                l1=uri.getPathSegments();
                listId=Integer.parseInt(l1.get(1));
                cursor=dbController.fetchList(listId);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case LIST:
                return CONTENT_TYPE;
            case LIST_ID:
                return CONTENT_ITEM_TYPE;
            case TASK:
                return CONTENT_TYPE;
            case TASK_ALL:
                return CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int listId;
        switch (uriMatcher.match(uri)) {
            case LIST:
                listId=dbController.insertList(values);
                Uri returnUri = ContentUris.withAppendedId(LIST_CONTENT_URI, listId);
                return returnUri;
            case TASK:
                int taskId=dbController.insertTask(values);
                returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, taskId);
                return returnUri;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int listId;
        switch (uriMatcher.match(uri)) {
            case LIST_ID:
                List<String> list=uri.getPathSegments();
                int id=Integer.parseInt(list.get(1));
                listId=dbController.deleteList(id);
                return listId;
            case TASK:
                list=uri.getPathSegments();
                int taskId=Integer.parseInt(list.get(1));
                id=dbController.deleteTask(taskId);
                return id;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int listId;
        switch (uriMatcher.match(uri)) {
            case TASK:
                List<String> list=uri.getPathSegments();
                int taskId=Integer.parseInt(list.get(1));
                listId=dbController.updateTask(taskId,values);
                return listId;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}