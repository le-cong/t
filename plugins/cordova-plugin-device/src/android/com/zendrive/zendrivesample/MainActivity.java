package com.zendrive.zendrivesample;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.zendrive.sdk.ZendriveLocationSettingsResult;
import com.zendrive.zendrivesample.fragments.LoginFragment;
import com.zendrive.zendrivesample.fragments.OffDutyFragment;
import com.zendrive.zendrivesample.fragments.OnDutyFragment;
import com.zendrive.zendrivesample.utils.Constants;
import com.zendrive.zendrivesample.utils.NotificationUtility;
import com.zendrive.zendrivesample.utils.SharedPrefsManager;
import com.zendrive.zendrivesample.utils.TripManager;
import com.zendrive.zendrivesample.utils.ZendriveManager;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadFirstFragment();
        registerForBroadcasts();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, -1);
        if (notificationId > 0) {
            // Activity started from notification
            handleNotification(notificationId);
        }
    }

    private void handleNotification(int notificationId) {
        if (notificationId == NotificationUtility.ZENDRIVE_FAILED_NOTIFICATION_ID) {
            // Retry Zendrive setup (if required)
            String driverId = SharedPrefsManager.sharedInstance().getDriverId();
            if (driverId != null) {
                ZendriveManager.sharedInstance().initializeZendriveSDK(driverId, null);
            }
            else {
                NotificationUtility.hideZendriveSetupFailureNotification(getApplicationContext());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(receiver);
    }

    private void registerForBroadcasts() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.
                getInstance(this.getApplicationContext());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processIntent(intent);
            }
        };
        localBroadcastManager.registerReceiver(receiver, getIntentFilterForLocalBroadcast());
    }

    /**
     * returns intent filters, which this class is interested in.
     */
    private static IntentFilter getIntentFilterForLocalBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.LOCATION_PERMISSION_CHANGE_BROADCAST);
        intentFilter.addAction(Constants.LOCATION_SETTINGS_CHANGE_BROADCAST);
        return intentFilter;
    }

    /**
     * Processes intents and update application UI.
     */
    private void processIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case Constants.LOCATION_SETTINGS_CHANGE_BROADCAST:
                    ZendriveLocationSettingsResult result =
                            intent.getParcelableExtra(Constants.DATA);
                    if (result != null && !result.isSuccess()) {
                        resolveLocationSettings(this, result);
                    }
                    // Maybe ask user to re-enable the location settings.
                    break;
                case Constants.LOCATION_PERMISSION_CHANGE_BROADCAST:
                    boolean granted =
                            intent.getBooleanExtra(Constants.LOCATION_PERMISSION_CHANGE_BROADCAST,
                                    false);
                    requestLocationPermission(granted);
                    break;
            }
        }
    }

    private void requestLocationPermission(boolean granted) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!granted) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.LOCATION_PERMISSION_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(
                            Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NotificationUtility.
                        LOCATION_PERMISSION_DENIED_NOTIFICATION_ID);
            }
        }
    }

    private void resolveLocationSettings(Activity activity, ZendriveLocationSettingsResult locationSettingsResult) {
        if (locationSettingsResult.isSuccess()) {
            return;
        }
        for (ZendriveLocationSettingsResult.Error error : locationSettingsResult.errors) {
            switch (error) {
                case GOOGLE_PLAY_SERVICES_ERROR_RESULT: {
                    LocationSettingsResult result = locationSettingsResult.locationSettingsResultFromGooglePlayService;
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // Should not happen
                            Timber.e("Success received when expected" +
                                    "error from Google Play " +
                                    "Services");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        activity,
                                        Constants.GOOGLE_PLAY_SERVICES_REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        }
    }

    private void loadFirstFragment() {
        Fragment firstFragment;
        if (SharedPrefsManager.sharedInstance().getDriverId() != null) {
            if (TripManager.sharedInstance().getTripManagerState().isUserOnDuty()) {
                firstFragment = new OnDutyFragment();
            }
            else {
                firstFragment = new OffDutyFragment();
            }
        }
        else {
            firstFragment = new LoginFragment();
        }
        replaceFragment(firstFragment);
    }

    public void replaceFragment(Fragment newFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainContentView, newFragment);
        ft.commit();
    }
}
