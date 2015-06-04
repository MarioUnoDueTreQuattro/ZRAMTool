package com.thepriest.andrea.zramtool;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Andrea on 29/05/2015.
 */
public class ZRAMToolApp extends Application implements OnSharedPreferenceChangeListener {
    private static final String TAG = ZRAMToolApp.class.getSimpleName();
    SharedPreferences prefs;
    static public int iRefreshFrequency;
    static public String sZRAMDirectory;
    static public boolean bShowNotification;
    static public int iDiskNum;
    static public int iZRAMSize, iZRAMComprDataSize, iZRAMTotalMemoryUsed, iZRAMMaximumUsage;
    static public int iZRAMUsage;
    static public int iMaximumZRAMUsage;
    static public int iSwappiness;
    static public int iVFSCachePressure;
    static public int iDiskSize0, iDiskSize1, iDiskSize2, iDiskSize3;
    static public int iOrigDataSize0, iOrigDataSize1, iOrigDataSize2, iOrigDataSize3;
    static public int iMemUsedTotal0, iMemUsedTotal1, iMemUsedTotal2, iMemUsedTotal3;
    static public int iComprDataSize0, iComprDataSize1, iComprDataSize2, iComprDataSize3;


    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //if (BuildConfig.DEBUG) Log.d(TAG, "The log msg");
        Log.d(TAG, "onCreate");
        sZRAMDirectory = "/sys/devices/virtual/block";
        // Read SharedPreferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        String prefString = prefs.getString("pref_ZRAM_directory", "1");
        int ipref = Integer.parseInt(prefString);
        if (ipref == 1) {
            sZRAMDirectory = "/sys/devices/virtual/block";
        }
        if (ipref == 0) sZRAMDirectory = "/dev/block";
        Log.d(TAG, "pref_ZRAM_directory= " + sZRAMDirectory);
        prefString = prefs.getString("refresh_frequency", "5");
        ipref = Integer.parseInt(prefString);
        Log.d(TAG, "ipref= " + ipref);
        iRefreshFrequency = ipref * 1000;
        if (ipref == -1) iRefreshFrequency = ipref * 3600000;
        Log.d(TAG, "refresh_frequency= " + iRefreshFrequency);
        bShowNotification = prefs.getBoolean("enable_notification", true);
        Log.d(TAG, "enable_notification= " + bShowNotification);
        if (bShowNotification) startService(new Intent(this, NotificationService.class));
        else stopService(new Intent(this, NotificationService.class));
    }


    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     * <p/>
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged -> key= " + key);
        sZRAMDirectory = "/sys/devices/virtual/block";
        // Read SharedPreferences
        // prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefString = prefs.getString("pref_ZRAM_directory", "1");
        int ipref = Integer.parseInt(prefString);
        if (ipref == 1) {
            sZRAMDirectory = "/sys/devices/virtual/block";
        }
        if (ipref == 0) sZRAMDirectory = "/dev/block";
        Log.d(TAG, "pref_ZRAM_directory= " + sZRAMDirectory);
        prefString = prefs.getString("refresh_frequency", "5");
        ipref = Integer.parseInt(prefString);
        Log.d(TAG, "ipref= " + ipref);
        iRefreshFrequency = ipref * 1000;
        if (ipref == -1) iRefreshFrequency = ipref * 3600000;
        Log.d(TAG, "refresh_frequency= " + iRefreshFrequency);
        bShowNotification = prefs.getBoolean("enable_notification", true);
        Log.d(TAG, "enable_notification= " + bShowNotification);
        // passes values to MainActivity
        MainActivity.sZRAMDirectory = sZRAMDirectory;
        MainActivity.iRefreshFrequency = iRefreshFrequency;
        MainActivity.bShowNotification = bShowNotification;
        // Starts or stops the service
        if (bShowNotification) {
            startService(new Intent(this, NotificationService.class));
            NotificationService.iRefreshFrequency = iRefreshFrequency;
        } else {
            stopService(new Intent(this, NotificationService.class));
        }
    }

    public static boolean hasZRAM0() {
//        Log.d(TAG,sZRAMDirectory + "/zram0/disksize " + new File(sZRAMDirectory + "/zram0/disksize").exists());
        return new File(sZRAMDirectory + "/zram0/disksize").exists();
    }

    public static boolean hasZRAM1() {
        return new File(sZRAMDirectory + "/zram1/disksize").exists();
    }

    public static boolean hasZRAM2() {
        return new File(sZRAMDirectory + "/zram2/disksize").exists();
    }

    public static boolean hasZRAM3() {
        return new File(sZRAMDirectory + "/zram3/disksize").exists();
    }

    public static void printZRAMStatus() {
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

        int diskNum = 0;
        if (hasZRAM0() == true) diskNum++;
        if (hasZRAM1() == true) diskNum++;
        if (hasZRAM2() == true) diskNum++;
        if (hasZRAM3() == true) diskNum++;
        // textViewDiskNum.setText(getString(R.string.ZRAM_disk_number) + diskNum);
        iDiskNum = diskNum;
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/swappiness"));
            String line;

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                // textViewSwappiness.setText("Swappiness: " + line);
                iSwappiness = Integer.parseInt(line);

            }
/*
            mounts.close();
            //mounts = new BufferedReader(new FileReader("/sys/block/zram0/disksize"));
            mounts = new BufferedReader(new FileReader("/sys/devices/virtual/block/zram0/disksize"));

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewTotalMemoryUsed.setText("ZRAM: " + line);


            }
            */
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            //textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            //textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        try {
            BufferedReader mounts = new BufferedReader(new FileReader("/proc/sys/vm/vfs_cache_pressure"));
            String line;

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                //textViewSwappiness.setText("Swappiness: " + line);
                //textViewVFS_cache_pressure.setText("VFS cache pressure: " + line);
                iVFSCachePressure = Integer.parseInt(line);

            }
/*
            mounts.close();
            //mounts = new BufferedReader(new FileReader("/sys/block/zram0/disksize"));
            mounts = new BufferedReader(new FileReader("/sys/devices/virtual/block/zram0/disksize"));

            while ((line = mounts.readLine()) != null) {
                // do some processing here
                textViewTotalMemoryUsed.setText("ZRAM: " + line);


            }
            */
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
            // textViewComprDataSize.setText("FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
            // textViewComprDataSize.setText("IOException");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        Shell shell = new Shell();
        String result1 = "";
        String result2 = "";
        String result3 = "";
        String result4 = "";
        int r1num = 0;
        int r2num = 0;
        int r3num = 0;
        int r4num = 0;
        int ZRAMSizeTot = 0;
        try {
            if (hasZRAM0() == true || hasZRAM0() == false) {
                //result1 = Shell.sudo("cat /sys/devices/virtual/block/zram0/disksize");
                //result2 = Shell.sudo("cat /sys/devices/virtual/block/zram0/size");
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram0/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram0/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram0/compr_data_size");
                //r1num = r1num + Integer.parseInt(result1.toString());
                r1num = getZRAMDiskSize(0);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize0 = r1num;
                iOrigDataSize0 = Integer.parseInt(result2.toString());
                iMemUsedTotal0 = Integer.parseInt(result3.toString());
                iComprDataSize0 = Integer.parseInt(result4.toString());
            }
            if (hasZRAM1() == true) {
                //result1 = Shell.sudo("cat /sys/devices/virtual/block/zram1/disksize");
                //result2 = Shell.sudo("cat /sys/devices/virtual/block/zram0/size");
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram1/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram1/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram1/compr_data_size");
                r1num = getZRAMDiskSize(1);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize1 = r1num;
                iOrigDataSize1 = Integer.parseInt(result2.toString());
                iMemUsedTotal1 = Integer.parseInt(result3.toString());
                iComprDataSize1 = Integer.parseInt(result4.toString());
            }
            if (hasZRAM2() == true) {
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram1/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram1/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram1/compr_data_size");
                r1num = getZRAMDiskSize(2);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize2 = r1num;
                iOrigDataSize1 = Integer.parseInt(result2.toString());
                iMemUsedTotal1 = Integer.parseInt(result3.toString());
                iComprDataSize1 = Integer.parseInt(result4.toString());
            }
            if (hasZRAM3() == true) {
                result3 = Shell.sudo("cat /sys/devices/virtual/block/zram1/orig_data_size");
                result2 = Shell.sudo("cat /sys/devices/virtual/block/zram1/mem_used_total");
                result4 = Shell.sudo("cat /sys/devices/virtual/block/zram1/compr_data_size");
                r1num = getZRAMDiskSize(3);
                ZRAMSizeTot += r1num;
                r2num = r2num + Integer.parseInt(result2.toString());
                r3num = r3num + Integer.parseInt(result3.toString());
                r4num = r4num + Integer.parseInt(result4.toString());
                iDiskSize3 = r1num;
                iOrigDataSize1 = Integer.parseInt(result2.toString());
                iMemUsedTotal1 = Integer.parseInt(result3.toString());
                iComprDataSize1 = Integer.parseInt(result4.toString());
            }
        } catch (java.lang.NullPointerException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) Log.d(TAG, "java.lang.NullPointerException");
        } catch (Shell.ShellException e) {
            e.printStackTrace();
            if (BuildConfig.DEBUG) Log.d(TAG, "Shell.ShellException");
        } catch (NumberFormatException nfe) {
            if (BuildConfig.DEBUG) Log.d(TAG, "NumberFormatException: can't parse " + nfe);
        } finally {
            try {
                iZRAMSize = ZRAMSizeTot / 1024 / 1024;
//                r1num = Integer.parseInt(result1.toString());
                r1num = r1num / 1024 / 1024;
                //              r2num = Integer.parseInt(result2.toString());
                r2num = r2num / 1024 / 1024;
                //            r3num = Integer.parseInt(result3.toString());
                r3num = r3num / 1024 / 1024;
                //          r4num = Integer.parseInt(result4.toString());
                r4num = r4num / 1024 / 1024;
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
            }
        }
        //iZRAMSize = r1num;
        iZRAMComprDataSize = r4num;
        iZRAMTotalMemoryUsed = r2num;
        //textViewTotalSize.setText(getString(R.string.ZRAM_size) + r1num + " MB");
        //textViewTotalMemoryUsed.setText(getString(R.string.ZRAM_total) + r2num + " MB");
        //textViewOrigDataSize.setText(getString(R.string.ZRAM_original) + r3num + " MB");
        //textViewComprDataSize.setText(getString(R.string.ZRAM_compressed) + r4num + " MB");
        int iMemory[] = new int[5];
        iMemory = getMemoryInfo();
        //textViewFreeRam.setText("Free memory: " + iMemory[0] + " MB");
        //textViewCached.setText("Cached: " + iMemory[2] + " MB");
        //textViewBuffers.setText("Buffers: " + iMemory[1] + " MB");
        //textViewTotalFree.setText("Total free memory: " + iMemory[3] + " MB");
        //textViewTotal.setText("Total memory: " + iMemory[4] + " MB");
        iZRAMUsage = r3num;
        if (iZRAMUsage > iZRAMMaximumUsage) iZRAMMaximumUsage = iZRAMUsage;
        if (r3num > iMaximumZRAMUsage) iMaximumZRAMUsage = r3num;
        //textViewMaxZRAMUsage.setText("Maximum ZRAM usage: " + iMaximumZRAMUsage + " MB");
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

    public static int getZRAMDiskSize(int disk) {
        String path = "/sys/devices/virtual/block/zram";
        path += disk;
        path += "/disksize";
        try {
            BufferedReader mounts = new BufferedReader(new FileReader(path));
            String line;
            while ((line = mounts.readLine()) != null) {
                disk = Integer.parseInt(line);
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot find...");
        } catch (IOException e) {
            Log.d(TAG, "Ran into problems reading...");
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: Could not parse " + nfe);
        }
        return disk;
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
}
