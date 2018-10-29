//package com.sdsu.cs646.shameetha.alarmclocktest;
//
//import android.content.Context;
//import android.os.PowerManager;
//
///**
// * Created by Shameetha on 5/4/15.
// */
//public class StaticWakeLock {
//    private static PowerManager.WakeLock wl = null;
//
//    public static void lockOn(Context context) {
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
////        if (wl == null)
////            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "PUZZLE_ALARM");
////        wl.acquire();
//    }
//
//    public static void lockOff(Context context) {
//        try {
////            if (wl != null)
////                wl.release();
//        } catch (Exception e) {
//        }
//    }
//}