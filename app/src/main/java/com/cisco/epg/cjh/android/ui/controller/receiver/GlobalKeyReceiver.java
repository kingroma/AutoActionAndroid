package com.cisco.epg.cjh.android.ui.controller.receiver;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.cisco.epg.cjh.android.activity.MainActivity;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

// https://wsym.tistory.com/entry/%EC%99%B8%EB%B6%80-%EB%B8%8C%EB%A1%9C%EB%93%9C-%EC%BA%90%EC%8A%A4%ED%8A%B8-%EB%B0%9B%EC%95%84%EC%84%9C-%EC%95%A1%ED%8B%B0%EB%B9%84%ED%8B%B0-%EC%8B%A4%ED%96%89
public class GlobalKeyReceiver extends BroadcastReceiver {
    private static final String TAG = GlobalKeyReceiver.class.getSimpleName();
    public static final String ACTION_GLOBAL_BUTTON = "android.intent.action.GLOBAL_BUTTON";

//    public GlobalKeyReceiver(){
//        Log.d(TAG,"GlobalKeyReceiver constructor");
//    }


//    public static void doRestart(Context c) {
//        try {
//            //check if the context is given
//            if (c != null) {
//                //fetch the packagemanager so we can get the default launch activity
//                // (you can replace this intent with any other activity if you want
//                PackageManager pm = c.getPackageManager();
//                //check if we got the PackageManager
//                if (pm != null) {
//                    //create the intent with the default start activity for your application
//                    Intent mStartActivity = pm.getLaunchIntentForPackage(
//                            c.getPackageName()
//                    );
//                    if (mStartActivity != null) {
//                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        //create a pending intent so the application is restarted after System.exit(0) was called.
//                        // We use an AlarmManager to call this intent in 100ms
//                        int mPendingIntentId = 223344;
//                        PendingIntent mPendingIntent = PendingIntent
//                                .getActivity(c, mPendingIntentId, mStartActivity,
//                                        PendingIntent.FLAG_CANCEL_CURRENT);
//                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
//                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                        //kill the application
//                        System.exit(0);
//                    } else {
//                        Log.e(TAG, "Was not able to restart application, mStartActivity null");
//                    }
//                } else {
//                    Log.e(TAG, "Was not able to restart application, PM null");
//                }
//            } else {
//                Log.e(TAG, "Was not able to restart application, Context null");
//            }
//        } catch (Exception ex) {
//            Log.e(TAG, "Was not able to restart application");
//        }
//    }

    private boolean isActivityForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        Log.d(TAG, "isActivityForeground" + info.get(0).topActivity.getClassName());
        if(info.get(0).topActivity.getClassName().equals("com.cisco.epg.cjh.android.activity.MainActivity")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // Log.d(TAG, "onReceive: action: " + action);
        if (ACTION_GLOBAL_BUTTON.equals(action)) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int eventAction = event.getAction();
            // Log.d(TAG, "onReceive: event.getAction: " + eventAction);
            // Log.d(TAG, "onReceive: event.keyCode: " + event.getKeyCode());

            if (eventAction == KeyEvent.ACTION_DOWN && MainActivity.isRecording == false ) {
                Log.d(TAG,"Recording Start");
                // if ( MainActivity.status.equals("D") ) {
                if ( !isActivityForeground(context) ) {
                    Log.d(TAG,"new MainActivity Start");
                    Intent mintent = new Intent(context, MainActivity.class);
                    mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(mintent);
                }
                // Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                // Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|
                // FLAG_ACTIVITY_NEW_TASK
                // Intent.FLAG_ACTIVITY_SINGLE_TOP
                // Intent.FLAG_ACTIVITY_NO_HISTORY
                // Intent.FLAG_ACTIVITY_CLEAR_TOP


                MainActivity.stopVideo();
                MainActivity.stopRecording();
                MainActivity.startRecording();
            } else if (eventAction == KeyEvent.ACTION_UP && MainActivity.isRecording == true ) {
                // TODO 되냐 ?
                new Runnable (){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        }catch (Exception e) {

                        }
                        Log.d(TAG,"Recording End ");
                        MainActivity.stopRecording();
                    }
                } .run();

            }
        }
    }
}