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
    private int nId=0;
    public static final String myPreference = "in.shapps.todoapp.myPref" ;
    private SharedPreferences sp;

    private int pendingId=0;
    public static final String myPendingPreference = "in.shapps.todoapp.pendingIdPref" ;
    private SharedPreferences pendingSp;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    void doReminderWork(Intent intent) {
        /*NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this.getApplicationContext(), MainActivity.class);
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        Notification note = new Notification(R.drawable.ic_add_white_48dp, "Alarm",
                System.currentTimeMillis());
        note.setLatestEventInfo(this, "Title", "Text", pi);
        note.defaults |= Notification.DEFAULT_ALL;
        note.flags |= Notification.FLAG_AUTO_CANCEL;
        int id = 123456789;
        manager.notify(id, note);*/
        //if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()))
        //{
            /*SQLController dbController=new SQLController(this);
            dbController.open();
            Cursor cursor=dbController.fetchTimes();
            while(!cursor.isAfterLast()){
                Log.d("DEBUG1", "Restarting alarm for time " + cursor.getString(1));
                Intent myIntent = new Intent(AlarmService.this, OnAlarmReceiver.class);
                myIntent.putExtra(DBHelper.TODO_DESC, cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DESC)));
                sp=getSharedPreferences(myPreference, Context.MODE_PRIVATE);
                pendingId=sp.getInt("pendingId",0);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmService.this, pendingId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingId++;
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("pendingId", pendingId);
                editor.commit();
                Calendar calendar;
                Log.d("DEBUG1 DATE","date obtained from db: "+cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME))+" DESC: "+cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DESC)));
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String strDate = simpleFormat.format(cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME)));
                Date date=new Date(strDate);
                calendar=Calendar.getInstance();
                calendar.setTime(date);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                cursor.moveToNext();
            }*/
        //}
        sp=getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        nId=sp.getInt("nId",0);
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
        pendingSp=getSharedPreferences(myPendingPreference, Context.MODE_PRIVATE);
        pendingId=sp.getInt("pendingId",0);
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
