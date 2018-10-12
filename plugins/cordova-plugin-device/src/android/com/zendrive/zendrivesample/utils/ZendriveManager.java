package com.zendrive.zendrivesample.utils;

import android.content.Context;

import com.zendrive.sdk.Zendrive;
import com.zendrive.sdk.ZendriveConfiguration;
import com.zendrive.sdk.ZendriveDriveDetectionMode;
import com.zendrive.sdk.ZendriveOperationCallback;
import com.zendrive.sdk.ZendriveOperationResult;
import com.zendrive.sdk.insurance.ZendriveInsurance;
import com.zendrive.zendrivesample.App;

import timber.log.Timber;

public class ZendriveManager {

    private class InsuranceInfo {
        int insurancePeriod;
        String trackingId;

        InsuranceInfo(int insurancePeriod, String trackingId) {
            this.insurancePeriod = insurancePeriod;
            this.trackingId = trackingId;
        }
    }

    // Zendrive SDK setup
    private static final String ZENDRIVE_SDK_KEY = "CV7m4UTV3gHYNWITD9xPqUYvlrqCQMZ6";   // Your Zendrive SDK Key

    private static ZendriveManager sharedInstance = new ZendriveManager();

    public static synchronized ZendriveManager sharedInstance() {
        return sharedInstance;
    }

    public void initializeZendriveSDK(String driverId, ZendriveOperationCallback callback) {
        initializeZendriveSDK(driverId, 1, 3, callback);
    }

    private void initializeZendriveSDK(final String driverId, final int retryCount,
                                       final int totalRetries,
                                       final ZendriveOperationCallback callback) {
        Timber.i("initializeZendriveSDK called");
        final ZendriveConfiguration zendriveConfiguration = new ZendriveConfiguration(
                ZENDRIVE_SDK_KEY, driverId, ZendriveDriveDetectionMode.AUTO_OFF);
        final Context applicationContext = getApplicationContext();

        Zendrive.setup(
                applicationContext,
                zendriveConfiguration,
                null, //MyZendriveBroadcastReceiver.class,
                MyZendriveNotificationProvider.class,
                new ZendriveOperationCallback() {
                    @Override
                    public void onCompletion(ZendriveOperationResult result) {
                        if (result.isSuccess()) {
                            Timber.i("ZendriveSDK setup success");

                            // Update periods
                            updateZendriveInsurancePeriod();
                            // Hide error if visible
                            // NotificationUtility.hideZendriveSetupFailureNotification(
                            //         applicationContext);
                            if (callback != null) {
                                callback.onCompletion(result);
                            }
                        } else {
                            // Retry
                            if (retryCount <= totalRetries) {
                                initializeZendriveSDK(driverId, (retryCount + 1), totalRetries,
                                        callback);
                            } else {
                                Timber.i("ZendriveSDK setup failed %s",
                                        result.getErrorCode().toString());

                                // Display error
                                // NotificationUtility.displayZendriveSetupFailureNotification(
                                //         applicationContext);
                                if (callback != null) {
                                    callback.onCompletion(result);
                                }
                            }
                        }
                    }
                }
        );
    }

    void updateZendriveInsurancePeriod() {
        Context applicationContext = getApplicationContext();
        ZendriveOperationCallback insuranceCalllback = new ZendriveOperationCallback() {
            @Override
            public void onCompletion(ZendriveOperationResult zendriveOperationResult) {
                if (!zendriveOperationResult.isSuccess()) {
                    Timber.e("Insurance period switch failed, error: %s",
                            zendriveOperationResult.getErrorCode().name());
                }
            }
        };
        InsuranceInfo insuranceInfo = currentlyActiveInsurancePeriod();
        if (insuranceInfo == null) {
            Timber.i("updateZendriveInsurancePeriod with NO period");
            ZendriveInsurance.stopPeriod(applicationContext, insuranceCalllback);
        } else if (insuranceInfo.insurancePeriod == 3) {
            Timber.i("updateZendriveInsurancePeriod with period %d and id: %s",
                    insuranceInfo.insurancePeriod,
                    insuranceInfo.trackingId);
            ZendriveInsurance.startDriveWithPeriod3(applicationContext, insuranceInfo.trackingId,
                    insuranceCalllback);
        } else if (insuranceInfo.insurancePeriod == 2) {
            Timber.i("updateZendriveInsurancePeriod with period %d and id: %s",
                    insuranceInfo.insurancePeriod,
                    insuranceInfo.trackingId);
            ZendriveInsurance.startDriveWithPeriod2(applicationContext, insuranceInfo.trackingId,
                    insuranceCalllback);
        } else {
            Timber.i("updateZendriveInsurancePeriod with period %d",
                    insuranceInfo.insurancePeriod);
            ZendriveInsurance.startPeriod1(applicationContext, insuranceCalllback);
        }
    }

    private InsuranceInfo currentlyActiveInsurancePeriod() {
       TripManager.State state = TripManager.sharedInstance().getTripManagerState();
       if (!state.isUserOnDuty()) {
           return null;
       }
       else if (state.getPassengersInCar() > 0) {
           return new InsuranceInfo(3, state.getTrackingId());
       }
       else if (state.getPassengersWaitingForPickup() > 0) {
           return new InsuranceInfo(2, state.getTrackingId());
       }
       else {
           return new InsuranceInfo(1, null);
       }
    }

    private Context getApplicationContext() {
        //return App.instance.getApplicationContext();
        return this.cordova.getActivity().getApplicationContext()
    }
}
