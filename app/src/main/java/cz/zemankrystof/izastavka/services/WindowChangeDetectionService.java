package cz.zemankrystof.izastavka.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

import cz.zemankrystof.izastavka.MainActivity;
import cz.zemankrystof.izastavka.ShellExecutor;
import cz.zemankrystof.izastavka.Splash;

public class WindowChangeDetectionService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );
                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity) {
                    Log.e("CurrentActivity", componentName.flattenToShortString());
                    if (!event.getPackageName().toString().contains("cz.zemankrystof.izastavka")) {
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainActivity);
                    }
                }

            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public void onInterrupt() {}

    public void killPackageProcesses(String packagename) {
        int pid = 0;
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am
                .getRunningAppProcesses();
        Log.e("List", "Size" + pids.size());
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);
            Log.e("Proccess", "" + info.processName);
            if (info.processName.equalsIgnoreCase(packagename)) {
                pid = info.pid;
            }
        }
        android.os.Process.killProcess(pid);
        Log.e("Killing", "" + pid);
    }

    public void kill(String name){
        ShellExecutor exe = new ShellExecutor();

        String outp = exe.Executor("am force-stop" + name);
        Log.e("MRDAT", ""+ outp +" " + outp.length());
    }

}
