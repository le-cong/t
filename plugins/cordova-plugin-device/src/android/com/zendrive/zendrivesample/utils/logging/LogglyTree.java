package com.zendrive.zendrivesample.utils.logging;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.inrista.loggliest.Loggly;

import timber.log.Timber;

/**
 * Created by yogesh on 12/8/17.
 */

public class LogglyTree extends Timber.Tree {
    private static final String LOG_KEY = "rawmsg";

    public LogglyTree(Context context, String LOGGLY_TOKEN, String defaultTag, String userId) {
        Loggly.with(context, LOGGLY_TOKEN)
                .appendDefaultInfo(true)
                .uploadIntervalLogCount(10)
                .uploadIntervalSecs(100)
                .maxSizeOnDisk(500000)
                .appendStickyInfo("userId", userId)
                .tag(defaultTag)
                .init();
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        /*
        NOTE: tag can also be passed along, one way to do that is the following:

        JSONObject logObj = new JSONObject();
        logObj.put("component", tag);
        logObj.put("message", message);
        Loggly.v(LOG_KEY, logObj); // Or whatever method is relevant
        */
        switch (priority) {
            case Log.VERBOSE:
                Loggly.v(LOG_KEY, message);
                break;
            case Log.DEBUG:
                Loggly.d(LOG_KEY, message);
                break;
            case Log.INFO:
                Loggly.i(LOG_KEY, message);
                break;
            case Log.WARN:
                Loggly.w(LOG_KEY, message);
                break;
            case Log.ERROR:
                Loggly.e(LOG_KEY, message);
                break;
            default:
                break;
        }
    }
}
