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
public class OnAlarmReceiver extends BroadcastReceiver {
    private Cursor cursor;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.d("DEBUG1", "Inside BroadCast receiver -> to Restart Service");
            Intent pushIntent = new Intent(context, AlarmRestartService.class);
            context.startService(pushIntent);
            return;
        } else {
            Log.d("DEBUG1", "Inside BroadCast receiver -> to Alarm Service");
            Intent intent1 = new Intent(context, AlarmService.class);
            intent1.putExtra(DBHelper.TODO_DESC, intent.getStringExtra(DBHelper.TODO_DESC));
            context.startService(intent1);
        }
    }
}
