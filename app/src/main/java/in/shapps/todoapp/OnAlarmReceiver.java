package in.shapps.todoapp;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by James on 2/23/2016.
 */
public class OnAlarmReceiver extends BroadcastReceiver{
    private Cursor cursor;

    /*private static final String CONTENT_AUTHORITY = "in.shapps.todoapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIST = "list1";
    public static final String PATH_TASK = "task1";
    private static final Uri LIST_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_LIST);
    private static final Uri TASK_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_TASK);*/

    @Override
    public void onReceive(Context context, Intent intent) {
        //WakeIntentService.acquireStaticLock(context);
        /*if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
        {
            Intent intent1 = new Intent(context, AlarmService.class);
            intent1.putExtra(DBHelper.TODO_DESC, intent.getStringExtra(DBHelper.TODO_DESC));
            Log.d("DEBUG1","Restarting OnAlarmReceiver Service");
            intent1.putExtra("restartAlarm",true);
            context.startService(intent1);
        }

        else {*/
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("DEBUG1","Inside BroadCast receiver -> to Restart Service");
            Intent pushIntent = new Intent(context, AlarmRestartService.class);
            context.startService(pushIntent);
            return;
            }
        else {
            Log.d("DEBUG1", "Inside BroadCast receiver -> to Alarm Service");
            Intent intent1 = new Intent(context, AlarmService.class);
            intent1.putExtra(DBHelper.TODO_DESC, intent.getStringExtra(DBHelper.TODO_DESC));
            context.startService(intent1);
        }
        //}
    }
}
