package in.shapps.todoapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by James on 2/23/2016.
 */
public abstract class WakeIntentService extends IntentService {
    abstract void doReminderWork(Intent intent);
    public static final String LOCK_NAME_STATIC="in.shapps.todoapp.static";
    private static PowerManager.WakeLock lockStatic = null;
    /*public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }
    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager powManager = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            lockStatic = powManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        if (lockStatic != null) {
            Log.v("DEBUG1", "Releasing wakelock");
            try {
                lockStatic.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        } else {
            // should never happen during normal workflow
            Log.e("DEBUG1", "Wakelock reference is null");
        }
        return lockStatic;
    }*/

    public WakeIntentService(String name) {
        super(name);
    }

    @Override
    final protected void onHandleIntent(Intent intent) {
       // try {
            doReminderWork(intent);
        //} finally {
            //getLock(this).release();
        //}
    }

}

