package com.zendrive.zendrivesample.utils;

public class TripManager {

    public class State {
        private boolean isUserOnDuty;
        private int passenegersWaitingForPickup;
        private int passenegersInCar;
        private String trackingId;

        public boolean isUserOnDuty() {
            return isUserOnDuty;
        }

        public int getPassengersWaitingForPickup() {
            return passenegersWaitingForPickup;
        }

        public int getPassengersInCar() {
            return passenegersInCar;
        }

        public String getTrackingId() {
            return trackingId;
        }

        State(boolean isUserOnDuty, int passenegersWaitingForPickup,
              int passenegersInCar, String trackingId) {
            this.isUserOnDuty = isUserOnDuty;
            this.passenegersWaitingForPickup = passenegersWaitingForPickup;
            this.passenegersInCar = passenegersInCar;
            this.trackingId = trackingId;
        }

        State(State another) {
            this.isUserOnDuty = another.isUserOnDuty;
            this.passenegersWaitingForPickup = another.passenegersWaitingForPickup;
            this.passenegersInCar = another.passenegersInCar;
            this.trackingId = another.trackingId;
        }
    }

    private static TripManager sharedInstance;

    public static synchronized TripManager sharedInstance() {
        if (sharedInstance == null) {
            sharedInstance = new TripManager();
        }
        return sharedInstance;
    }

    private State state;
    private TripManager() {
        state = new State(SharedPrefsManager.sharedInstance().isUserOnDuty(),
                SharedPrefsManager.sharedInstance().passengersWaitingForPickup(),
                SharedPrefsManager.sharedInstance().passengersInCar(),
                SharedPrefsManager.sharedInstance().getTrackingId());
    }

    public synchronized void acceptNewPassengerRequest() {
        state.passenegersWaitingForPickup += 1;
        SharedPrefsManager.sharedInstance().
                setPassengersWaitingForPickup(state.passenegersWaitingForPickup);
        updateTrackingIdIfNeeded();
        ZendriveManager.sharedInstance().updateZendriveInsurancePeriod();
    }

    public synchronized void pickupAPassenger() {
        state.passenegersWaitingForPickup -= 1;
        SharedPrefsManager.sharedInstance().
                setPassengersWaitingForPickup(state.passenegersWaitingForPickup);

        state.passenegersInCar += 1;
        SharedPrefsManager.sharedInstance().
                setPassengersInCar(state.passenegersInCar);
        updateTrackingIdIfNeeded();
        ZendriveManager.sharedInstance().updateZendriveInsurancePeriod();
    }

    public synchronized void cancelARequest() {
        state.passenegersWaitingForPickup -= 1;
        SharedPrefsManager.sharedInstance().
                setPassengersWaitingForPickup(state.passenegersWaitingForPickup);
        updateTrackingIdIfNeeded();
        ZendriveManager.sharedInstance().updateZendriveInsurancePeriod();
    }

    public synchronized void dropAPassenger() {
        state.passenegersInCar -= 1;
        SharedPrefsManager.sharedInstance().setPassengersInCar(state.passenegersInCar);
        updateTrackingIdIfNeeded();
        ZendriveManager.sharedInstance().updateZendriveInsurancePeriod();
    }

    public synchronized void goOnDuty() {
        state.isUserOnDuty = true;
        SharedPrefsManager.sharedInstance().setIsUserOnDuty(state.isUserOnDuty);
        updateTrackingIdIfNeeded();
        ZendriveManager.sharedInstance().updateZendriveInsurancePeriod();
    }

    public synchronized void goOffDuty() {
        state.isUserOnDuty = false;
        SharedPrefsManager.sharedInstance().setIsUserOnDuty(state.isUserOnDuty);
        updateTrackingIdIfNeeded();
        ZendriveManager.sharedInstance().updateZendriveInsurancePeriod();
    }

    private void updateTrackingIdIfNeeded() {
        if (state.passenegersWaitingForPickup > 0 || state.passenegersInCar > 0) {
            // We need trackingId
            if (state.trackingId == null) {
                state.trackingId = ((Long)System.currentTimeMillis()).toString();
                SharedPrefsManager.sharedInstance().setTrackingId(state.trackingId);
            }
        }
        else {
            state.trackingId = null;
            SharedPrefsManager.sharedInstance().setTrackingId(state.trackingId);
        }
    }

    public synchronized State getTripManagerState() {
        return new State(state);
    }
}
