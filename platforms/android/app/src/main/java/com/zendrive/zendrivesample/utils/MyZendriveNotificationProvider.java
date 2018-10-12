package com.zendrive.zendrivesample.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zendrive.sdk.ZendriveNotificationContainer;
import com.zendrive.sdk.ZendriveNotificationProvider;

public class MyZendriveNotificationProvider implements ZendriveNotificationProvider {
    @NonNull
    @Override
    public ZendriveNotificationContainer getMaybeInDriveNotificationContainer(@NonNull Context context) {
        return new ZendriveNotificationContainer(
                NotificationUtility.FOREGROUND_MODE_NOTIFICATION_ID,
                NotificationUtility.getMaybeInDriveNotification(context));
    }

    @NonNull
    @Override
    public ZendriveNotificationContainer getInDriveNotificationContainer(@NonNull Context context) {
        return new ZendriveNotificationContainer(
                495,
                NotificationUtility.getInDriveNotification(context));
    }
}
