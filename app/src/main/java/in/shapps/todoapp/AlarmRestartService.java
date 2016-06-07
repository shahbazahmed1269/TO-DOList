package in.shapps.todoapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
public class AlarmRestartService extends WakeIntentService {
    private int nId=0;
    public static final String myPreference = "in.shapps.todoapp.myPref" ;
    private SharedPreferences sp;

    private int pendingId=0;
    public static final String myPendingPreference = "in.shapps.todoapp.pendingIdPref" ;
    private SharedPreferences pendingSp;

    public AlarmRestartService() {
        super("AlarmRestartService");
    }

    @Override
    void doReminderWork(Intent intent){
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
        //enabling the broadcast receiver programmatically
        ComponentName receiver = new ComponentName(getApplicationContext(), OnAlarmReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

            SQLController dbController=new SQLController(this);
            dbController.open();
            Cursor cursor=dbController.fetchTimes();
            //cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Log.d("DEBUG1", "Check Restarting alarm for time " + cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME)));
                try {
                    if (!new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").parse(cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME))).before(new Date())) {
                        Intent myIntent = new Intent(AlarmRestartService.this, OnAlarmReceiver.class);
                        myIntent.putExtra(DBHelper.TODO_DESC, cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DESC)));
                        sp = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
                        pendingId = sp.getInt("pendingId", 0);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmRestartService.this, pendingId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        pendingId++;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("pendingId", pendingId);
                        editor.commit();
                        Calendar calendar;
                        Log.d("DEBUG1_DATE", "date obtained from db: " + cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME)) + " DESC: " + cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DESC)));
                        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                        Date date;
                        String s = "";
                        String s1 = "";
                        date = simpleFormat.parse(cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME)));
                        calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        s1 += calendar.getTime() + "\n";
                        s += cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DESC)) + "-> " + cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME)) + "\n\n";
                        //Date date=new Date(cursor.getString(cursor.getColumnIndex(DBHelper.TODO_DATETIME)).toString());
                        Log.d("DEBUG1_DATE", s);
                        Log.d("DEBUG1_DATE", "After date convert+" + s1);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                        Log.d("DEBUG1_DATE", "calendar.getTime: " + calendar.getTime());
                    }
                }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                cursor.moveToNext();
                }
        this.stopSelf();
    }
}
