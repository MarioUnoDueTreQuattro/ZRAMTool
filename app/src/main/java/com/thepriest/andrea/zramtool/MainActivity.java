package com.thepriest.andrea.zramtool;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    private static boolean b_isActivityVisible;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TIME_INTERVAL = 2000; // used for onBackPressed()
    private long mBackPressed;
    //Timer timer = new Timer();
    //RefreshTask refreshTask;
    Handler mHandler;
    //TimerTask timerTask;
    TextView textViewTotalSize, textViewTotalMemoryUsed, textViewOrigDataSize, textViewComprDataSize, textViewSwappiness, textViewFreeRam,
            textViewBuffers, textViewCached, textViewTotalFree, textViewTotal;
    TextView textViewDiskNum, textViewVFS_cache_pressure, textViewMaxZRAMUsage;
    Button buttonDisableZRAM, buttonEnableZRAM, buttonCleanMemory, buttonCleanDropCache, buttonCleanAll;
    ProgressBar progressBarTotalMemoryUsed, progressBarOrigDataSize, progressBarComprDataSize, progressBarMaxZRAMUsage;
    static public int iSwappiness, iZRAMSize, iDiskNum, iVFSCachePressure, iZRAMUsage, iMaximumZRAMUsage;
    static public int iRefreshFrequency;
    static public String sZRAMDirectory;
    static public boolean bShowNotification;
    static public boolean bUpdateStatus;
    static public boolean bTimerStarted = false;
    static private int iUpdatesCount = 0;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            //super.onBackPressed();
//            if (bShowNotification) moveTaskToBack(true);
//            else super.onBackPressed();
            //finish();
            //System.exit(0);
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Tap back button twice to hide.\nUse \"Menu->Exit\" to exit.", Toast.LENGTH_LONG).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ZRAMToolApp.printZRAMStatus();
        //if (BuildConfig.DEBUG) Log.d(TAG, "The log msg");
        Log.d(TAG, "onCreate()");
        //this.setTheme(R.style.Black);
        //Debug.startMethodTracing();
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_launcher_48);
        getSupportActionBar().setLogo(R.drawable.ic_launcher_96);

/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
*/
        bUpdateStatus = true;
        ZRAMToolApp app = ((ZRAMToolApp) this.getApplication());
        sZRAMDirectory = app.sZRAMDirectory;
        iRefreshFrequency = app.iRefreshFrequency;
        bShowNotification = app.bShowNotification;
/*
        sZRAMDirectory = "/sys/devices/virtual/block";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefString = prefs.getString("pref_ZRAM_directory", "1");
        int ipref = Integer.parseInt(prefString);
        if (ipref == 1) {
            sZRAMDirectory = "/sys/devices/virtual/block";
            //System.out.println("prefString=/sys/devices/virtual/block= " + prefString + "\t");
        }
        if (ipref == 0) sZRAMDirectory = "/dev/block";
        Log.d(TAG, "pref_ZRAM_directory= " + sZRAMDirectory);
*/
/*
        try {
            Shell.sudo("chmod 644 " + sZRAMDirectory + "/zram0/disksize");
            Shell.sudo("chown system.system " + sZRAMDirectory + "/zram0/disksize");
        } catch (com.thepriest.andrea.zramtool.Shell.ShellException e) {
            e.printStackTrace();
        }
*/
        //iRefreshFrequency=5000;
/*
        Set<String> set = prefs.getStringSet("pref_refresh_frequency_values", new HashSet<String>());
        prefString=set.toString();
        Log.d(TAG, "prefString= " + prefString);

        //prefString=prefs.getString("pref_refresh_frequency_values", "5");
        ipref=Integer.parseInt(prefString);
        Log.d(TAG, "ipref= " + ipref);
        iRefreshFrequency =ipref * 5000;
        if (ipref==-1) iRefreshFrequency =ipref * 3600000;
        Log.d(TAG, "pref_refresh_frequency_values= " + iRefreshFrequency);
*/
/*
        prefString = prefs.getString("refresh_frequency", "5");
        ipref = Integer.parseInt(prefString);
        Log.d(TAG, "ipref= " + ipref);
        iRefreshFrequency = ipref * 1000;
        if (ipref == -1) iRefreshFrequency = ipref * 3600000;
        Log.d(TAG, "refresh_frequency= " + iRefreshFrequency);
        bShowNotification = prefs.getBoolean("enable_notification", true);
        Log.d(TAG, "enable_notification= " + bShowNotification);
*/
        //System.out.println("prefString= " + prefString + "\t");
//        Set<String> sZRAMDirectory =prefs.getStringSet("pref_ZRAM_directory",new HashSet<String>());
//        if(sZRAMDirectory != null){
//
//            Iterator<String> iterator = sZRAMDirectory.iterator();
//
//            while(iterator.hasNext()){
//
//                String id = iterator.next();
//
//                //int start = id.indexOf("[") + 1;
//                //int end = id.indexOf("]")-1;
//

//                //String items = String.copyValueOf(id.toCharArray(), start, end);
//                System.out.println(id + "\t");
//
//            }
//        }
        setContentView(R.layout.activity_main);
        progressBarTotalMemoryUsed = (ProgressBar) findViewById(R.id.progressBarTotalMemoryUsed);
        progressBarOrigDataSize = (ProgressBar) findViewById(R.id.progressBarOrigDataSize);
        progressBarComprDataSize = (ProgressBar) findViewById(R.id.progressBarComprDataSize);
        progressBarMaxZRAMUsage = (ProgressBar) findViewById(R.id.progressBarMaxZRAMUsage);
        textViewFreeRam = (TextView) findViewById(R.id.textViewFreeRam);
        textViewBuffers = (TextView) findViewById(R.id.textViewBuffers);
        textViewCached = (TextView) findViewById(R.id.textViewCached);
        textViewTotalFree = (TextView) findViewById(R.id.textViewTotalFree);
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewTotalSize = (TextView) findViewById(R.id.textViewTotalSize);
        textViewTotalMemoryUsed = (TextView) findViewById(R.id.textViewTotalMemoryUsed);
        textViewOrigDataSize = (TextView) findViewById(R.id.textViewOrigDataSize);
        textViewComprDataSize = (TextView) findViewById(R.id.textViewComprDataSize);
        textViewSwappiness = (TextView) findViewById(R.id.textViewSwappiness);
        textViewDiskNum = (TextView) findViewById(R.id.textViewDiskNum);
        textViewVFS_cache_pressure = (TextView) findViewById(R.id.textViewVFS_cache_pressure);
        textViewMaxZRAMUsage = (TextView) findViewById(R.id.textViewMaxZRAMUsage);
/*
        buttonDisableZRAM = (Button) findViewById(R.id.buttonDisableZRAM);
        buttonDisableZRAM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Shell.sudo("swapoff /dev/block/zram0");
                    buttonDisableZRAM.setText("Disabling ZRAM0...");
                    Shell.sudo("echo 1 > /sys/block/zram0/reset");
                    buttonDisableZRAM.setText("Disabled ZRAM0");
                    Shell.sudo("swapoff /dev/block/zram1");
                    buttonDisableZRAM.setText("Disabling ZRAM1...");
                    Shell.sudo("echo 1 > /sys/block/zram1/reset");
                    buttonDisableZRAM.setText("Disabled ZRAM1");
                    Shell.sudo("swapoff /dev/block/zram2");
                    buttonDisableZRAM.setText("Disabling ZRAM2...");
                    Shell.sudo("echo 1 > /sys/block/zram2/reset");
                    buttonDisableZRAM.setText("Disabled ZRAM2");
                    Shell.sudo("swapoff /dev/block/zram3");
                    buttonDisableZRAM.setText("Disabling ZRAM3...");
                    Shell.sudo("echo 1 > /sys/block/zram3/reset");
                    buttonDisableZRAM.setText(R.string.disableZRAM);
                    Shell.sudo("echo 0 > /proc/sys/vm/swappiness");
                    Toast.makeText(getApplicationContext(), "ZRAM Disabled.", Toast.LENGTH_LONG).show();
                } catch (Shell.ShellException e) {
                    e.printStackTrace();
                }
            }
        });
*/

        buttonEnableZRAM = (Button) findViewById(R.id.buttonEnableZRAM);
        buttonEnableZRAM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Enable_ZRAM_Activity.class));
            }
        });
        buttonCleanMemory = (Button) findViewById(R.id.buttonCleanMemory);
        buttonCleanMemory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanMemory();
            }
        });
        buttonCleanDropCache = (Button) findViewById(R.id.buttonCleanDropCache);
        buttonCleanDropCache.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanDropCache();
            }
        });
        buttonCleanAll = (Button) findViewById(R.id.buttonCleanAll);
        buttonCleanAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanMemoryAndDropCache();
            }
        });
        b_isActivityVisible = true;
        printZRAMStatus();


/*
        if (timer != null) {
                        timer.cancel();
                       //timer = null;
                    }
*/
        mHandler = null;
        mHandler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                if (mHandler == _h0) {
                    tick();
                    mHandler.postDelayed(this, iRefreshFrequency);
                }
            }

            private final Handler _h0 = mHandler;
        };
        r.run();
/*
        try {
            r.wait(5000);
        }catch (InterruptedException e){}
*/
        //  timer.cancel();
        //     timer.purge();
        //Timer timer = new Timer();
/*
        if (bTimerStarted==false) {
            timer.schedule(new updateTask(), iRefreshFrequency, iRefreshFrequency);
            bTimerStarted=true;
        }
        else
        {
            //timer.cancel();
            timer.schedule(new updateTask(), iRefreshFrequency, iRefreshFrequency);

        }
*/
        //refreshTask.execute();
        // new RefreshTask().execute();
/*        if (bShowNotification) {
            NotificationCompat.Builder appLaunch = new NotificationCompat.Builder(this);
            appLaunch.setSmallIcon(R.drawable.ic_launcher_48);
            appLaunch.setContentTitle("ZRAMTool");
            appLaunch.setContentText("Click to open ZRAMTool");
            //appLaunch.setAutoCancel(true);
            appLaunch.setOngoing(true);
            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            appLaunch.setContentIntent(contentIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //mNotificationManager.cancelAll();
            mNotificationManager.notify(0, appLaunch.build());
        } else {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
        }
 */
        //    Debug.startMethodTracing();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        // _handler = null;
        super.onStop();
        // Debug.stopMethodTracing();

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        mHandler = null;
        super.onDestroy();
        //Debug.stopMethodTracing();
    }


    private void tick() {
        if (!ZRAMToolApp.bShowNotification) ZRAMToolApp.printZRAMStatus();
        printZRAMStatus();
        iUpdatesCount++;
        // textViewVFS_cache_pressure.setText(""+iUpdatesCount);
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        b_isActivityVisible = false;
        //finish();
        //timer.cancel();
        //timer.purge();
        /*
        try {
            timer.wait();
            timer.cancel();
        timer.purge();
        } catch (InterruptedException e1) {
        }
*/
        super.onPause();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        b_isActivityVisible = true;
        printZRAMStatus();
//timer = new Timer();
        //timerTask = new updateTask();
        //timer.schedule(new updateTask(),iRefreshFrequency, iRefreshFrequency);
        //        try{
        //timer.notify();
//        } catch (IllegalThreadStateException ex) {
//            ex.printStackTrace();
//        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_exit) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
            if (bShowNotification) stopService(new Intent(this, NotificationService.class));
            System.exit(0);
            return true;
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(this, About.class));
            return true;
        }
        if (id == R.id.action_changelog) {
//Launch change log dialog
            ChangeLogDialog _ChangelogDialog = new ChangeLogDialog(this);
            _ChangelogDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        int initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");

            for (String num : arrayOfString) {
                //System.out.println(num + "\t");
            }
            //total Memory
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue();
            initial_memory = initial_memory / 1024;

            System.out.println(initial_memory);
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return initial_memory;
    }

    public static int[] getMemoryInfo() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        int memory[] = new int[5];
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
/*
            for (String num : arrayOfString) {
               System.out.println(num + "\t");
            }
*/
            memory[4] = Integer.valueOf(arrayOfString[1]).intValue();
            memory[4] = memory[4] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            memory[0] = Integer.valueOf(arrayOfString[1]).intValue();
            memory[0] = memory[0] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            memory[1] = Integer.valueOf(arrayOfString[1]).intValue();
            memory[1] = memory[1] / 1024;
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            memory[2] = Integer.valueOf(arrayOfString[1]).intValue();
            memory[2] = memory[2] / 1024;
            memory[3] = memory[0] + memory[1] + memory[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return memory;
    }

    private void cleanMemoryAndDropCache() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            //if (procInfos.get(i).processName.equals("com.android.music")) {
            //Toast.makeText(null, "music is running",
            //      Toast.LENGTH_LONG).show();
            activityManager.killBackgroundProcesses(procInfos.get(i).processName);
        }
        String result1 = "";
        try {
            result1 = Shell.sudo("sync");
            result1 = Shell.sudo("echo 3 > /proc/sys/vm/drop_caches");
        } catch (com.thepriest.andrea.zramtool.Shell.ShellException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
        Toast.makeText(getApplicationContext(), "Memory and Drop Cache cleaned.", Toast.LENGTH_LONG).show();
        return;
    }

    private void cleanDropCache() {
        String result1 = "";
        try {
            result1 = Shell.sudo("sync");
            result1 = Shell.sudo("echo 3 > /proc/sys/vm/drop_caches");
        } catch (Shell.ShellException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
        Toast.makeText(getApplicationContext(), "Drop Cache cleaned.", Toast.LENGTH_LONG).show();
        return;
    }

    private void cleanMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context
                .ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            //if (procInfos.get(i).processName.equals("com.android.music")) {
            //Toast.makeText(null, "music is running",
            //      Toast.LENGTH_LONG).show();
            activityManager.killBackgroundProcesses(procInfos.get(i).processName);
        }
        Toast.makeText(getApplicationContext(), "Memory cleaned.", Toast.LENGTH_LONG).show();
        return;
    }

    private boolean hasSuperuserApk() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private boolean hasZRAM0() {
//        Log.d(TAG,sZRAMDirectory + "/zram0/disksize " + new File(sZRAMDirectory + "/zram0/disksize").exists());
        return new File(sZRAMDirectory + "/zram0/disksize").exists();
    }

    private boolean hasZRAM1() {
        return new File(sZRAMDirectory + "/zram1/disksize").exists();
    }

    private boolean hasZRAM2() {
        return new File(sZRAMDirectory + "/zram2/disksize").exists();
    }

    private boolean hasZRAM3() {
        return new File(sZRAMDirectory + "/zram3/disksize").exists();
    }

    private int getDiskNum() {
        String result1;
        int diskNum = 0;
        try {
            result1 = Shell.sudo("-d /sys/devices/virtual/block/zram0");
/*
            File file = this.getApplicationContext().getFileStreamPath("/sys/devices/virtual/block/zram0/disksize");
            if(file.exists())
            {
               iDiskNum=iDiskNum+1;
            }
*/
            textViewDiskNum.setText("" + diskNum);
            textViewDiskNum.setText(result1);
            //BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/swappiness"));
            //String line;

            //while ((line = mounts.readLine()) != null) {
            // do some processing here
            //   textViewSwappiness.setText("Swappiness: " + line);

            //}
/*
            mounts.close();
            //mounts = new BufferedReader(new FileReader("/sys/block/zram0/disksize"));
            mounts = new BufferedReader(new FileReader("/sys/devices/virtual/block/zram0/disksize"));

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewTotalMemoryUsed.setText("ZRAM: " + line);


            }
            */
        } catch (Shell.ShellException e) {
            e.printStackTrace();
        }
/*
        catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            textViewComprDataSize.setText("FileNotFoundException");
        }
        catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            textViewComprDataSize.setText("IOException");
        }
*/
        return 0;
    }

    private void printZRAMStatus() {

/*
       try
        {
            //BufferedReader reader = new BufferedReader("/proc/sys/vm/swappiness", "r");
            RandomAccessFile reader = new RandomAccessFile("/proc/sys/vm/swappiness", "r");
            String load = reader.readLine();
            while (load != null)
            {
                textViewTotalSize.setText(load);
                Log.d("swappiness", "swappiness: " + load);
                load = reader.readLine();

            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            textViewTotalMemoryUsed.setText("IOException");
        }
*/
        // if (b_isActivityVisible==false) return;
        // Log.d(TAG, "printZRAMStatus()");


//        int diskNum = 0;
//        if (hasZRAM0() == true) diskNum++;
//        if (hasZRAM1() == true) diskNum++;
//        if (hasZRAM2() == true) diskNum++;
//        if (hasZRAM3() == true) diskNum++;
        iDiskNum = ZRAMToolApp.iDiskNum;
        textViewDiskNum.setText(getString(R.string.ZRAM_disk_number) + iDiskNum);
/*        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/swappiness"));
            String line;

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewSwappiness.setText("Swappiness: " + line);
                iSwappiness = Integer.parseInt(line);

            }
*//*
            mounts.close();
            //mounts = new BufferedReader(new FileReader("/sys/block/zram0/disksize"));
            mounts = new BufferedReader(new FileReader("/sys/devices/virtual/block/zram0/disksize"));

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewTotalMemoryUsed.setText("ZRAM: " + line);


            }
            *//*
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }*/
        try {
            iSwappiness = ZRAMToolApp.iSwappiness;
            textViewSwappiness.setText("Swappiness: " + iSwappiness);
            textViewVFS_cache_pressure.setText("VFS cache pressure: " + ZRAMToolApp.iVFSCachePressure);
        } catch (Resources.NotFoundException exception) {
            exception.printStackTrace();
        }
        //Shell shell = new Shell();
        String result1 = "";
        String result2 = "";
        String result3 = "";
        String result4 = "";
        int r1num = 0;
        int r2num = 0;
        int r3num = 0;
        int r4num = 0;

        iZRAMSize = ZRAMToolApp.iZRAMSize;
        textViewTotalSize.setText(getString(R.string.ZRAM_size) + iZRAMSize + " MB");
        textViewTotalMemoryUsed.setText(getString(R.string.ZRAM_total) + ZRAMToolApp.iZRAMTotalMemoryUsed + " MB");
        textViewOrigDataSize.setText(getString(R.string.ZRAM_original) + ZRAMToolApp.iZRAMUsage + " MB");
        textViewComprDataSize.setText(getString(R.string.ZRAM_compressed) + ZRAMToolApp.iZRAMComprDataSize + " MB");
        int iMemory[] = new int[5];
        iMemory = getMemoryInfo();
        textViewFreeRam.setText("Free memory: " + iMemory[0] + " MB");
        textViewCached.setText("Cached: " + iMemory[2] + " MB");
        textViewBuffers.setText("Buffers: " + iMemory[1] + " MB");
        textViewTotalFree.setText("Total free memory: " + iMemory[3] + " MB");
        textViewTotal.setText("Total memory: " + iMemory[4] + " MB");
        iZRAMUsage = r3num;
        if (r3num > iMaximumZRAMUsage) iMaximumZRAMUsage = r3num;
        textViewMaxZRAMUsage.setText("Maximum ZRAM usage: " + ZRAMToolApp.iMaximumZRAMUsage + " MB");
        progressBarTotalMemoryUsed.setMax(iZRAMSize);
        progressBarOrigDataSize.setMax(iZRAMSize);
        progressBarComprDataSize.setMax(iZRAMSize);
        progressBarMaxZRAMUsage.setMax(iZRAMSize);
        progressBarTotalMemoryUsed.setProgress(ZRAMToolApp.iZRAMTotalMemoryUsed);
        progressBarOrigDataSize.setProgress(ZRAMToolApp.iZRAMUsage);
        progressBarComprDataSize.setProgress(ZRAMToolApp.iZRAMComprDataSize);
        progressBarMaxZRAMUsage.setProgress(ZRAMToolApp.iMaximumZRAMUsage);


        // progressBarTotalMemoryUsed, progressBarOrigDataSize,progressBarComprDataSize,progressBarMaxZRAMUsage


/*
        if (bShowNotification) {
            NotificationCompat.Builder appLaunch = new NotificationCompat.Builder(this);
            appLaunch.setSmallIcon(R.drawable.ic_launcher_48);
            appLaunch.setContentTitle("ZRAMTool");
            appLaunch.setContentText("ZRAM: " + r3num + " - Max ZRAM: " + iMaximumZRAMUsage);
            //appLaunch.setAutoCancel(true);
            appLaunch.setOngoing(true);
            appLaunch.setUsesChronometer(true);
            Intent targetIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            appLaunch.setContentIntent(contentIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mNotificationManager.cancelAll();
            mNotificationManager.notify(0, appLaunch.build());
        }
*/

    }

    class updateTask extends TimerTask {

        @Override
        public void run() {
            // Log.d(TAG,"updateTask->run()");

            MainActivity.this.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Log.d(TAG, "updateTask->run()->runOnUiThread - iRefreshFrequency= " + iRefreshFrequency + " - b_isActivityVisible= " + b_isActivityVisible);
                                                    printZRAMStatus();
                                                }
                                            }
            );
        }
    }

    class RefreshTask extends AsyncTask {

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            //String text = String.valueOf(System.currentTimeMillis());
            //myTextView.setText(text);
            Log.d(TAG, "onProgressUpdate: iRefreshFrequency= " + iRefreshFrequency + " - b_isActivityVisible= " + b_isActivityVisible + " - bShowNotification= " + bShowNotification);
            printZRAMStatus();
        }

        @Override
        protected Object doInBackground(Object... params) {
            while (bUpdateStatus) {
                try {
                    //sleep for 1s in background...
                    Thread.sleep(iRefreshFrequency);
                    //and update textview in ui thread
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
                //               return null;
            }
            return null;
        }
    }

}
