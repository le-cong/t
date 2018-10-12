package com.zendrive.zendrivesample.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

// import com.zendrive.sdk.ZendriveLocationSettingsResult;
import com.onestepride.tttt.MainActivity;
import com.onestepride.tttt.R;

public class NotificationUtility {
    private static final String FOREGROUND_CHANNEL_KEY = "Foreground";
    private static final String ISSUES_CHANNEL_KEY = "Issues";
    static int FOREGROUND_MODE_NOTIFICATION_ID = 1;
    public static int LOCATION_PERMISSION_DENIED_NOTIFICATION_ID = 2;
    static int LOCATION_DISABLED_NOTIFICATION_ID = 3;
    public static int ZENDRIVE_FAILED_NOTIFICATION_ID = 4;

    static Notification getMaybeInDriveNotification(@NonNull Context context) {
        createNotificationChannels(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_KEY)
                //     .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setDefaults(0)
                    .setPriority(Notification.PRIORITY_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText("Detecting Possible Drive.").build();
        }
        return new NotificationCompat.Builder(context)
                        // .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(context.
                                getString(R.string.app_name))
                        .setContentText("Detecting Possible Drive.")
                        .build();
    }

    static Notification getInDriveNotification(@NonNull Context context) {
        createNotificationChannels(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_KEY)
                //     .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentText("Drive Active.").build();
        }
        return new NotificationCompat.Builder(context)
                        // .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(context.
                                getString(R.string.app_name))
                        .setContentText("Drive Active.")
                        .build();
    }

    private static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            NotificationChannel foregroundNotificationChannel = new NotificationChannel
                    (FOREGROUND_CHANNEL_KEY, "Zendrive trip tracking",
                            NotificationManager.IMPORTANCE_DEFAULT);
            foregroundNotificationChannel.setShowBadge(false);

            NotificationChannel issuesNotificationChannel = new NotificationChannel
                    (ISSUES_CHANNEL_KEY, "Issues",
                            NotificationManager.IMPORTANCE_DEFAULT);
            issuesNotificationChannel.setShowBadge(true);

            if (manager != null) {
                manager.createNotificationChannel(foregroundNotificationChannel);
                manager.createNotificationChannel(foregroundNotificationChannel);
            }
        }
    }

    /**
     * Create a notification when location permission is denied to the application.
     * @param context App context
     * @return the created notifcation.
     */
    static Notification createLocationPermissionDeniedNotification(Context context) {
        createNotificationChannels(context);
        Intent callGPSSettingIntent = new Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, callGPSSettingIntent, 0);
        // Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
        //         R.drawable.notification_icon);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Location Permission Not Available")
                .setTicker("Location Permission Not Available")
                .setContentText("Grant Location Permission For Reliable Performance")
                // .setSmallIcon(R.drawable.notification_icon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notifBuilder.setCategory(Notification.CATEGORY_ERROR);
        }
        return notifBuilder.build();
    }

    /**
     * Create a notification when high accuracy location is disabled on the device.
     * @param context App context
     * @param settingsResult to get potential resolution from play services
     * @return the created notifcation.
     */
//     public static Notification createLocationSettingDisabledNotification(
//             Context context, ZendriveLocationSettingsResult settingsResult) {
//         createNotificationChannels(context);
//         Intent callGPSSettingIntent = new Intent(
//                 android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//         PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                 callGPSSettingIntent, 0);
//         // Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);

//         NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
//                 .setContentTitle("Location Settings Disabled")
//                 .setTicker("Location Settings Disabled")
//                 .setContentText("Please Enable Location Settings For Reliable Performance")
//                 // .setSmallIcon(R.drawable.notification_icon)
//                 .setPriority(NotificationCompat.PRIORITY_MAX)
//                 .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
//                 .setContentIntent(pendingIntent);
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//             notifBuilder.setCategory(Notification.CATEGORY_ERROR);
//         }
//         return notifBuilder.build();
//     }

    static void displayZendriveSetupFailureNotification(Context context) {
        createNotificationChannels(context);
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.NOTIFICATION_ID, ZENDRIVE_FAILED_NOTIFICATION_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, ISSUES_CHANNEL_KEY)
                .setContentTitle("Failed To Enable Insurance Benefits")
                .setTicker("Failed To Enable Insurance Benefits")
                .setContentText("Tap This Notification To Retry")
                // .setSmallIcon(R.drawable.notification_icon)
                // .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notifBuilder.setCategory(Notification.CATEGORY_ERROR);
        }
        Notification notification = notifBuilder.build();

        // Display notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(ZENDRIVE_FAILED_NOTIFICATION_ID, notification);
        }
    }

    public static void hideZendriveSetupFailureNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(ZENDRIVE_FAILED_NOTIFICATION_ID);
        }
    }

}
