/*
    ShengDao Android Client, ActivityPageManager
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package com.feimeng.fdroid.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.feimeng.fdroid.mvp.FDActivity;

import java.util.Iterator;
import java.util.Stack;

/**
 * Activity页面管理器
 * Created by feimeng on 2017/1/20.
 */
public class ActivityPageManager {
    private static ActivityPageManager instance = new ActivityPageManager();
    private static Stack<FDActivity> activities = new Stack<>();

    private ActivityPageManager() {
    }

    public Stack<FDActivity> all() {
        return activities;
    }

    /**
     * get the AppManager instance, the AppManager is singleton.
     */
    public static ActivityPageManager getInstance() {
        return instance;
    }

    /**
     * add Activity to Stack
     */
    public void addActivity(FDActivity activity) {
        activities.add(activity);
    }


    /**
     * remove Activity from Stack
     */
    public void removeActivity(FDActivity activity) {
        activities.remove(activity);
    }

    /**
     * get current activity from Stack
     */
    @Nullable
    public FDActivity currentActivity() {
        return activities.size() == 0 ? null : activities.lastElement();
    }

    public void finishActivity() {
        finishActivity(currentActivity());
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            activities.remove(activity);
            activity.finish();
        }
    }

    public void finishActivity(Class<?> cls) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    public void finishAllActivity() {
        finishAllActivity(null);
    }

    /**
     * @param activity 保留的Activity
     */
    public void finishAllActivity(Activity activity) {
        Iterator<FDActivity> iterator = activities.iterator();
        while (iterator.hasNext()) {
            FDActivity fdActivity = iterator.next();
            if (activity != null && fdActivity == activity) continue;
            fdActivity.finish();
            iterator.remove();
        }
    }

    /**
     * exit System
     *
     * @param context
     */
    public void exit(Context context) {
        exit(context, true);
    }

    /**
     * exit System
     *
     * @param context
     * @param isClearCache
     */
    @SuppressWarnings("deprecation")
    public void exit(Context context, boolean isClearCache) {
        try {
            finishAllActivity();
            /*if(context != null){
                ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				activityMgr.restartPackage(context.getPackageName());
			}*/
            /*if(isClearCache){
                LruCacheManager.getInstance().evictAll();
				CacheManager.clearAll();
			}*/
//			System.exit(0);
//			android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
