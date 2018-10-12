package com.zendrive.zendrivesample.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.zendrive.sdk.AccidentInfo;
import com.zendrive.sdk.AnalyzedDriveInfo;
import com.zendrive.sdk.DriveResumeInfo;
import com.zendrive.sdk.DriveStartInfo;
import com.zendrive.sdk.EstimatedDriveInfo;
import com.zendrive.sdk.ZendriveBroadcastReceiver;
import com.zendrive.sdk.ZendriveLocationSettingsResult;
import com.zendrive.zendrivesample.App;

import timber.log.Timber;

public class MyZendriveBroadcastReceiver extends ZendriveBroadcastReceiver {
    @Override
    public void onDriveStart(Context context, DriveStartInfo driveStartInfo) {
        Timber.d("onDriveStart");
        System.out.println("");
    }

    @Override
    public void onDriveEnd(Context context, EstimatedDriveInfo estimatedDriveInfo) {
        Timber.d("onDriveEnd");
    }

    @Override
    public void onDriveAnalyzed(Context context, AnalyzedDriveInfo analyzedDriveInfo) {
        Timber.d("onDriveAnalyzed");
    }

    @Override
    public void onDriveResume(Context context, DriveResumeInfo driveResumeInfo) {
        Timber.d("onDriveResume");
    }

    @Override
    public void onAccident(Context context, AccidentInfo accidentInfo) {
        Timber.d("onAccident");
    }

    @Override
    public void onLocationPermissionsChange(Context context, boolean granted) {
        displayOrHideLocationPermissionNotification(context, granted);
        Intent intent = new Intent(Constants.LOCATION_PERMISSION_CHANGE_BROADCAST);
        intent.putExtra(Constants.DATA, granted);
        LocalBroadcastManager.getInstance(App.instance.getApplicationContext()).
                sendBroadcast(intent);
    }

    @Override
    public void onLocationSettingsChange(
            Context context, ZendriveLocationSettingsResult settingsResult) {
        displayOrHideLocationSettingNotification(context, settingsResult);
        Intent intent = new Intent(Constants.LOCATION_SETTINGS_CHANGE_BROADCAST);
        intent.putExtra(Constants.DATA, settingsResult);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void displayOrHideLocationPermissionNotification(
            Context context, boolean isLocationPermissionGranted) {
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            if (isLocationPermissionGranted) {
                // Remove the displayed notification if any
                mNotificationManager.cancel(NotificationUtility.
                        LOCATION_PERMISSION_DENIED_NOTIFICATION_ID);
            } else {
                // Notify user
                Notification notification = NotificationUtility.
                        createLocationPermissionDeniedNotification(context);
                mNotificationManager.notify(NotificationUtility.
                        LOCATION_PERMISSION_DENIED_NOTIFICATION_ID, notification);
            }
        }
    }

    private void displayOrHideLocationSettingNotification(
            Context context, ZendriveLocationSettingsResult settingsResult) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            if (settingsResult.isSuccess()) {
                // Remove the displayed notification if any
                mNotificationManager.cancel(NotificationUtility.LOCATION_DISABLED_NOTIFICATION_ID);
            } else {
                // Notify user
                Notification notification = NotificationUtility.
                        createLocationSettingDisabledNotification(context, settingsResult);
                mNotificationManager.notify(NotificationUtility.LOCATION_DISABLED_NOTIFICATION_ID,
                        notification);
            }
        }
    }
}
