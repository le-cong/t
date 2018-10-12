package com.zendrive.zendrivesample.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zendrive.zendrivesample.App;

@SuppressLint("ApplySharedPref")
public class SharedPrefsManager {

    private static String kDriverIdKey = "driverId";
    private static String kIsUserOnDutyKey = "isUserOnDuty";
    private static String kPassengersInCarKey = "passengersInCar";
    private static String kPassengersWaitingForPickupKey = "passengersWaitingForPickup";
    private static String kTrackingIdKey = "trackingId";

    private static SharedPrefsManager sharedInstance;

    public static synchronized SharedPrefsManager sharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new SharedPrefsManager(App.instance.getApplicationContext());
        }
        return sharedInstance;
    }

    private SharedPreferences prefs;
    private SharedPrefsManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getDriverId() {
        return prefs.getString(kDriverIdKey, null);
    }

    public void setDriverId(String driverId) {
        SharedPreferences.Editor pEditor = prefs.edit();
        if (driverId == null) {
            pEditor.remove(kDriverIdKey);
        }
        else {
            pEditor.putString(kDriverIdKey, driverId);
        }
        pEditor.commit();
    }

    Boolean isUserOnDuty() {
        return prefs.getBoolean(kIsUserOnDutyKey, false);
    }

    void setIsUserOnDuty(boolean isUserOnDuty) {
        SharedPreferences.Editor pEditor = prefs.edit();
        pEditor.putBoolean(kIsUserOnDutyKey, isUserOnDuty);
        pEditor.commit();
    }

    Integer passengersInCar() {
        return prefs.getInt(kPassengersInCarKey, 0);
    }

    void setPassengersInCar(int passengersInCar) {
        SharedPreferences.Editor pEditor = prefs.edit();
        pEditor.putInt(kPassengersInCarKey, passengersInCar);
        pEditor.commit();
    }

    Integer passengersWaitingForPickup() {
        return prefs.getInt(kPassengersWaitingForPickupKey, 0);
    }

    void setPassengersWaitingForPickup(int passengersWaitingForPickup) {
        SharedPreferences.Editor pEditor = prefs.edit();
        pEditor.putInt(kPassengersWaitingForPickupKey, passengersWaitingForPickup);
        pEditor.commit();
    }

    String getTrackingId() {
        return prefs.getString(kTrackingIdKey, null);
    }

    void setTrackingId(String trackingId) {
        SharedPreferences.Editor pEditor = prefs.edit();
        if (trackingId == null) {
            pEditor.remove(kTrackingIdKey);
        }
        else {
            pEditor.putString(kTrackingIdKey, trackingId);
        }
        pEditor.commit();
    }
}
