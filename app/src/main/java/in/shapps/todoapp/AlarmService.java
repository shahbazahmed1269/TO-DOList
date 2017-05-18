package in.shapps.todoapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by James on 2/23/2016.
 */
public class AlarmService extends WakeIntentService {
    private int nId = 0;
    public static final String myPreference = "in.shapps.todoapp.myPref";
    private SharedPreferences sp;

    private int pendingId = 0;
    public static final String myPendingPreference = "in.shapps.todoapp.pendingIdPref";
    private SharedPreferences pendingSp;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    void doReminderWork(Intent intent) {
        sp = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        nId = sp.getInt("nId", 0);
        nId++;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("nId", nId);
        editor.commit();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_arrow_downward_white_24dp)
                        .setContentTitle("ToDo Task Reminder")
                        .setContentText(intent.getStringExtra(DBHelper.TODO_DESC));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        pendingSp = getSharedPreferences(myPendingPreference, Context.MODE_PRIVATE);
        pendingId = sp.getInt("pendingId", 0);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        pendingId,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(nId, mBuilder.build());
        this.stopSelf();
    }
}
