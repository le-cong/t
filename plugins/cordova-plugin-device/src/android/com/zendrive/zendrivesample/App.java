package com.zendrive.zendrivesample;

import android.app.Application;

import com.zendrive.zendrivesample.utils.logging.Logger;
import com.zendrive.zendrivesample.utils.SharedPrefsManager;
import com.zendrive.zendrivesample.utils.ZendriveManager;

import timber.log.Timber;

public class App extends Application {
    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize default loggers
        Logger.initializeDefaultLoggers(getApplicationContext());

        // Get current driver information and initialize Zendrive if driver information is available
        String driverId = SharedPrefsManager.sharedInstance().getDriverId();
        if (driverId != null) {
            Logger.initializeLogglyLogger(getApplicationContext(), driverId);
            ZendriveManager.sharedInstance().initializeZendriveSDK(driverId, null);
        }
        Timber.i("App onCreate");
    }
}
