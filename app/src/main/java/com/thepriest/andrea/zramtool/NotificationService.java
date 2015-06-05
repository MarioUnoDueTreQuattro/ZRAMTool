package com.thepriest.andrea.zramtool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Andrea on 29/05/2015.
 */
public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();
    private Updater updater;
    public boolean bIsRunning = false;
    static public int iRefreshFrequency;
    static public int iZRAMUsage, iMaximumZRAMUsage;

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (!bIsRunning) {
            updater.running = true;
            updater.start();
            bIsRunning = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * @param intent
     * @param startId
     * @deprecated Implement {@link #onStartCommand(Intent, int, int)} instead.
     */
    @Override
    public synchronized void onStart(Intent intent, int startId) {
        Log.d(TAG, "onStart - deprecated");
        super.onStart(intent, startId);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public synchronized void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        updater.running = false;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
        try {
            this.wait(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (bIsRunning)
            try {
                updater.stop();
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        /**
         * Remove this service from foreground state, allowing it to be killed if more memory is needed.
         */
        stopForeground(true);
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        updater = new Updater();
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    public void setNotification() {
        if (ZRAMToolApp.bShowNotification) {
            ZRAMToolApp.updateZRAMStatus3();
            iZRAMUsage=ZRAMToolApp.iZRAMUsage;
            iMaximumZRAMUsage=ZRAMToolApp.iZRAMMaximumUsage;
            NotificationCompat.Builder appLaunch = new NotificationCompat.Builder(this);
            appLaunch.setSmallIcon(R.drawable.ic_launcher_48);
            appLaunch.setContentTitle("ZRAMTool");
            appLaunch.setContentText("ZRAM: " + iZRAMUsage + " - Max ZRAM: " + iMaximumZRAMUsage);
            //appLaunch.setAutoCancel(true);
            appLaunch.setOngoing(true);
            appLaunch.setUsesChronometer(true);
            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            appLaunch.setContentIntent(contentIntent);
            //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationManager.cancelAll();
            //mNotificationManager.notify(0, appLaunch.build());
            Notification note= appLaunch.build();
            startForeground(1337, note);
        }
    }

    class Updater extends Thread {
        volatile boolean running = true;
        //static final long DELAY = 10000;

        /**
         * Calls the <code>run()</code> method of the Runnable object the receiver
         * holds. If no Runnable is set, does nothing.
         *
         * @see Thread#start
         */
        @Override
        public void run() {
            super.run();
//            if (BuildConfig.DEBUG) Log.d(TAG, "Updater run");
            while (true) {
                try {
                    setNotification();
                    if (BuildConfig.DEBUG) Log.d(TAG, "Updater run loop..................");
                    this.sleep(ZRAMToolApp.iRefreshFrequency);
                    if (!running) return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
