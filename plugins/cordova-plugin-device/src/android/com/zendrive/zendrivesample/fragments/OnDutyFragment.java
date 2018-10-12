package com.zendrive.zendrivesample.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zendrive.zendrivesample.MainActivity;
import com.zendrive.zendrivesample.R;
import com.zendrive.zendrivesample.utils.TripManager;

import timber.log.Timber;

public class OnDutyFragment extends Fragment implements View.OnClickListener {

    private TextView currentStateTextView;
    private Button pickupAPassengerButton;
    private Button cancelRequestButton;
    private Button dropAPassengerButton;
    private Button offDutyButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_onduty, container, false);

        currentStateTextView = layout.findViewById(R.id.currentStateTextView);

        layout.findViewById(R.id.acceptNewRideReqButton).setOnClickListener(this);

        pickupAPassengerButton = layout.findViewById(R.id.pickupAPassengerButton);
        pickupAPassengerButton.setOnClickListener(this);

        cancelRequestButton = layout.findViewById(R.id.cancelRequestButton);
        cancelRequestButton.setOnClickListener(this);

        dropAPassengerButton = layout.findViewById(R.id.dropAPassengerButton);
        dropAPassengerButton.setOnClickListener(this);

        offDutyButton = layout.findViewById(R.id.offDutyButton);
        offDutyButton.setOnClickListener(this);
        refreshUI();
        return layout;
    }

    @SuppressLint("DefaultLocale")
    private void refreshUI() {
        TripManager.State tripManagerState = TripManager.sharedInstance().getTripManagerState();
        int passengersInCar = tripManagerState.getPassengersInCar();
        int passengerWaitingForPickup = tripManagerState.getPassengersWaitingForPickup();

        int insurancePeriod = 0;
        if (passengersInCar > 0) {
            insurancePeriod = 3;
        }
        else if (passengerWaitingForPickup > 0) {
            insurancePeriod = 2;
        }
        else if (tripManagerState.isUserOnDuty()) {
            insurancePeriod = 1;
        }

        currentStateTextView.setText(
                String.format("Insurance Period: %d\nPassengers In Car: %d" +
                        "\nPassengers Waiting For Pickup: %d", insurancePeriod, passengersInCar,
                        passengerWaitingForPickup));

        pickupAPassengerButton.setEnabled(passengerWaitingForPickup > 0);
        cancelRequestButton.setEnabled(passengerWaitingForPickup > 0);
        dropAPassengerButton.setEnabled(passengersInCar > 0);
        offDutyButton.setEnabled(passengersInCar == 0 && passengerWaitingForPickup == 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.acceptNewRideReqButton:
                Timber.i("acceptNewRideReqButton tapped");
                TripManager.sharedInstance().acceptNewPassengerRequest();
                break;
            case R.id.pickupAPassengerButton:
                Timber.i("pickupAPassengerButton tapped");
                TripManager.sharedInstance().pickupAPassenger();
                break;
            case R.id.cancelRequestButton:
                Timber.i("cancelRequestButton tapped");
                TripManager.sharedInstance().cancelARequest();
                break;
            case R.id.dropAPassengerButton:
                Timber.i("dropAPassengerButton tapped");
                TripManager.sharedInstance().dropAPassenger();
                break;
            case R.id.offDutyButton:
                Timber.i("offDutyButton tapped");
                TripManager.sharedInstance().goOffDuty();
                ((MainActivity)getActivity()).replaceFragment(new OffDutyFragment());
                break;
        }
        refreshUI();
    }
}
