package com.zendrive.zendrivesample.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zendrive.zendrivesample.App;
import com.zendrive.zendrivesample.MainActivity;
import com.zendrive.zendrivesample.R;
import com.zendrive.zendrivesample.utils.logging.Logger;
import com.zendrive.zendrivesample.utils.SharedPrefsManager;
import com.zendrive.zendrivesample.utils.ZendriveManager;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText idEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_login, container, false);
        idEditText = layout.findViewById(R.id.idEditText);
        layout.findViewById(R.id.signupButton).setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signupButton:
                signupButtonClicked();
                break;
        }
    }

    private void signupButtonClicked() {
        final String driverId = idEditText.getText().toString();
        if (!driverId.equals("")) {
            // Initialize Logger
            Logger.initializeLogglyLogger(App.instance.getApplicationContext(), driverId);

            // Save driver information
            SharedPrefsManager.sharedInstance().setDriverId(driverId);

            // Initialize ZendriveSDK
            ZendriveManager.sharedInstance().initializeZendriveSDK(driverId, null);

            // Load UI
            ((MainActivity)getActivity()).replaceFragment(new OffDutyFragment());
        }
    }
}
