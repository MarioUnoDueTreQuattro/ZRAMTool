package com.thepriest.andrea.zramtool;

import android.app.ActivityManager;
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
import android.widget.RemoteViews;

import java.util.List;

/**
 * Created by Andrea on 29/05/2015.
 */
public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();
    private Updater updater;
    public boolean bIsRunning = false;
    static public int iRefreshFrequency;
    static public int iZRAMUsage, iMaximumZRAMUsage, iCounter;

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
        iCounter = 0; //more than 5 if i want cleanDropCache() called on first run of Updater thread
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
            ZRAMToolApp.updateStatus();
            //ZRAMToolApp.updateRAMStatus();
            iZRAMUsage = ZRAMToolApp.iZRAMUsage;
            iMaximumZRAMUsage = ZRAMToolApp.iZRAMMaximumUsage;
            NotificationCompat.Builder appLaunch = new NotificationCompat.Builder(this);
            String sDrawable = "m";
            int iDrawable = (ZRAMToolApp.iTotalFreeMemory / 5) * 5;
            sDrawable += iDrawable;
            int drawableResourceId = this.getResources().getIdentifier(sDrawable, "drawable", this.getPackageName());
            appLaunch.setSmallIcon(drawableResourceId);
//            appLaunch.setSmallIcon(R.drawable.ic_launcher_48);
            //appLaunch.setContentText("Total Free: " + ZRAMToolApp.iTotalFreeMemory + " - Free: " + ZRAMToolApp.iFreeMemory + " - Cached: " + ZRAMToolApp.iCachedMemory + " - Buffers: " + ZRAMToolApp.iBuffersMemory);
            //appLaunch.setContentTitle("ZRAM used: " + iZRAMUsage + " - Max ZRAM: " + iMaximumZRAMUsage);
            appLaunch.setContentText(getString(R.string.Total_Free) + ZRAMToolApp.iTotalFreeMemory + getString(R.string._Free) + ZRAMToolApp.iFreeMemory + getString(R.string._Cached) + ZRAMToolApp.iCachedMemory + getString(R.string._Buffers) + ZRAMToolApp.iBuffersMemory);
            appLaunch.setContentTitle(getString(R.string.ZRAM_used) + iZRAMUsage + getString(R.string._Max_ZRAM) + iMaximumZRAMUsage);
            //appLaunch.setAutoCancel(true);
            appLaunch.setTicker(getString(R.string.Launched_ZRAMTool_background_service));
            appLaunch.setOngoing(true);
            appLaunch.setUsesChronometer(true);
            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            appLaunch.setContentIntent(contentIntent);
            //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationManager.cancelAll();
            //mNotificationManager.notify(0, appLaunch.build());
            Notification note = appLaunch.build();
            startForeground(1337, note);
        }
    }

    public void setNotification2() {
        if (ZRAMToolApp.bShowNotification) {
            ZRAMToolApp.updateStatus();
            //ZRAMToolApp.updateRAMStatus();
            iZRAMUsage = ZRAMToolApp.iZRAMUsage;
            iMaximumZRAMUsage = ZRAMToolApp.iZRAMMaximumUsage;
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
            //Intent intent = new Intent(this, NotificationView.class);
            // Send data to NotificationView Class
            //intent.putExtra("title", "strtitle");
            //intent.putExtra("text", "strtext");
            remoteViews.setTextViewText(R.id.textViewRAMFree, getString(R.string.Total_Free) + ZRAMToolApp.iTotalFreeMemory);
            remoteViews.setTextViewText(R.id.textViewRAMDetails, getString(R.string.Free) + ZRAMToolApp.iFreeMemory + getString(R.string._Cached) + ZRAMToolApp.iCachedMemory + getString(R.string._Buffers) + ZRAMToolApp.iBuffersMemory);
            remoteViews.setTextViewText(R.id.textViewZRAM, getString(R.string.ZRAM_used) + iZRAMUsage + getString(R.string._Max_ZRAM) + iMaximumZRAMUsage);
            NotificationCompat.Builder appLaunch = new NotificationCompat.Builder(this);
            //Resources res = this.getResources();
            //appLaunch.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_96));
            appLaunch.setTicker(getString(R.string.Launched_ZRAMTool_background_service));
            //appLaunch.setSmallIcon(R.drawable.ic_launcher_48);
            String sDrawable = "m";
            int iDrawable = (ZRAMToolApp.iTotalFreeMemory / 5) * 5;
            sDrawable += iDrawable;
            int drawableResourceId = this.getResources().getIdentifier(sDrawable, "drawable", this.getPackageName());
            appLaunch.setSmallIcon(drawableResourceId);
/*
            byte [] encodeByte= Base64.decode(":", Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Drawable d = new BitmapDrawable(bitmap);
            appLaunch.setLargeIcon(bitmap);
*/
            //appLaunch.setContentText(getString(R.string.Total_Free) + ZRAMToolApp.iTotalFreeMemory + getString(R.string._Free) + ZRAMToolApp.iFreeMemory + getString(R.string._Cached) + ZRAMToolApp.iCachedMemory + getString(R.string._Buffers) + ZRAMToolApp.iBuffersMemory);
            //appLaunch.setContentTitle(getString(R.string.ZRAM_used) + iZRAMUsage + getString(R.string._Max_ZRAM) + iMaximumZRAMUsage);
            //appLaunch.setAutoCancel(true);
            appLaunch.setOngoing(true);
            //appLaunch.setUsesChronometer(true);
            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            appLaunch.setContentIntent(contentIntent);
            appLaunch.setContent(remoteViews);
//            PendingIntent buttonIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.buttonCleanAll, buttonIntent);
            //NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationManager.cancelAll();
            //mNotificationManager.notify(0, appLaunch.build());
            Notification note = appLaunch.build();
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
                if (BuildConfig.DEBUG) Log.d(TAG, "NotificationService::run()");
                ZRAMToolApp.appendLog("NotificationService::run()");
                iCounter++;
                try {
                    if (ZRAMToolApp.bShowAdvancedNotification) setNotification2();
                    else setNotification();
                    if (ZRAMToolApp.iTotalFreeMemory < ZRAMToolApp.iMemoryLimitToDropCache && ZRAMToolApp.bEnableDropCache && iCounter > 5) {
                        iCounter = 0;
                        cleanMemoryKeepingRecents();
                        cleanDropCache();
                    }
                    Updater.sleep(ZRAMToolApp.iRefreshFrequency);
                    if (!running) return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cleanDropCache() {
        try {
            Shell.sudo("sync");
            Shell.sudo("echo 3 > /proc/sys/vm/drop_caches");
        } catch (Shell.ShellException e) {
            e.printStackTrace();
        } finally {
        }
        // Toast.makeText(getApplicationContext(), "Drop Cache cleaned.", Toast.LENGTH_LONG).show();
        if (BuildConfig.DEBUG) Log.d(TAG, "cleanDropCache");
    }

    private void cleanMemoryKeepingRecents() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();    //
        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(30, 0);
        int recentCount = recentTasks.size();
        int procCount = procInfos.size();
        String sProcName = "";
        String sRecentPackageName = "";
        boolean bProcIsInRecentLimit = true;
        //final  ArrayList<ApplicationInfo> recents = new  ArrayList<ApplicationInfo>();
        ZRAMToolApp.appendLog("recentCount= " + recentCount + " - Process limit= " + ZRAMToolApp.iProcessLimit);
        Log.d(TAG, "recentCount= " + recentCount + " - Process limit= " + ZRAMToolApp.iProcessLimit);
        for (int i = 0; i < procCount; i++) {
            //if (procInfos.get(i).processName.equals("com.android.music")) {
            //Toast.makeText(null, "music is running",
            //      Toast.LENGTH_LONG).show();
            sProcName = procInfos.get(i).processName;
            bProcIsInRecentLimit = false;
            for (int iRec = 0; iRec < ZRAMToolApp.iProcessLimit && iRec < recentCount; iRec++) {
                Intent intent = recentTasks.get(iRec).baseIntent;
                sRecentPackageName = intent.getComponent().getPackageName();
                //Log.d(TAG, "-> cleanMemoryKeepingRecents() \"" + sRecentPackageName + "\"" + " " + "\"" + sProcName + "\"");
                if (sRecentPackageName.equals(sProcName)) {
                    bProcIsInRecentLimit = true;
                    //Log.d(TAG, "sRecentPackageName == sProcName NOT killBackgroundProcesses= " + sProcName);
                    ZRAMToolApp.appendLog("NOT kill " + sProcName);

                }
            }
            if (bProcIsInRecentLimit == false) {
                activityManager.killBackgroundProcesses(sProcName);
                ZRAMToolApp.appendLog("KILL " + sProcName);

                //   Log.d(TAG, "killBackgroundProcesses= " + sProcName);
            } else {
                //   Log.d(TAG, "NOT killBackgroundProcesses= " + sProcName);
            }
        }
/*
        if (recentCount > ZRAMToolApp.iProcessLimit)
            for (int i = ZRAMToolApp.iProcessLimit; i < recentCount; i++) {
                Intent intent = recentTasks.get(i).baseIntent;
                String recentPackageName = intent.getComponent().getPackageName();
                Log.d(TAG, "cleanMemoryKeepingRecents() " + recentPackageName);
                activityManager.killBackgroundProcesses(recentPackageName);
            }
*/
    }
}
