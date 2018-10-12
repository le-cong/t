package com.zendrive.zendrivesample.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zendrive.zendrivesample.MainActivity;
import com.zendrive.zendrivesample.R;
import com.zendrive.zendrivesample.utils.TripManager;

import timber.log.Timber;

public class OffDutyFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_offduty, container, false);
        layout.findViewById(R.id.goOnDutyButton).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goOnDutyButton:
                goOnDutyButtonClicked();
                break;
        }
    }

    private void goOnDutyButtonClicked() {
        Timber.i("goOnDutyButtonClicked");
        TripManager.sharedInstance().goOnDuty();
        ((MainActivity)getActivity()).replaceFragment(new OnDutyFragment());
    }
}
