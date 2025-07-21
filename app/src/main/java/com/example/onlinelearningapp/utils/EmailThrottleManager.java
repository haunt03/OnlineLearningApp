package com.example.onlinelearningapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class EmailThrottleManager {
    private static final String PREF_NAME = "EmailPrefs";
    private static final String KEY_LAST_SENT_TIME = "last_sent_time";
    private static final long THROTTLE_DURATION = 2 * 60 * 1000; // 2 phút (ms)

    public static boolean canSend(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long lastTime = prefs.getLong(KEY_LAST_SENT_TIME, 0);
        return System.currentTimeMillis() - lastTime >= THROTTLE_DURATION;
    }

    public static void updateLastSentTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_LAST_SENT_TIME, System.currentTimeMillis()).apply();
    }
}
