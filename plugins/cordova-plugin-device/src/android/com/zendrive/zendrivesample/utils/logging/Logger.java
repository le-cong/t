package com.zendrive.zendrivesample.utils.logging;

import android.content.Context;

import com.zendrive.zendrivesample.BuildConfig;

import timber.log.Timber;

/**
 * Created by yogesh on 12/7/17.
 */

public class Logger {
    private static final String LOGGLY_TOKEN = null; // Replace with your loggly "Customer Token"
    private static LogglyTree logglyTree = null;

    public static void initializeDefaultLoggers(Context context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        // You may also want to initialize crashlytics and/or file logger here
    }

    public static void initializeLogglyLogger(Context context, String userId) {
        if (logglyTree != null) {
            Timber.uproot(logglyTree);
            logglyTree = null;
        }
        if (LOGGLY_TOKEN != null) {
            String defaultTag = "AndroidSample"; // Typically appName is used, this is used to
            // categorize the logs in loggly, this has a length limit of 64 Characters
            logglyTree = new LogglyTree(context, LOGGLY_TOKEN, defaultTag, userId);
            Timber.plant(logglyTree);
        }
    }
}
