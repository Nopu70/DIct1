package com.nopu70.fragment.home;

import android.app.Fragment;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nopu70.dict.R;
import com.nopu70.services.EbbinghausService;

/**
 * Created by nopu70 on 15-12-21.
 */
public class SettingFragment extends PreferenceFragment {


    Preference exit;
    SwitchPreference setBN;
    EbbinghausService.MBinder binder;
    ServiceConnection con = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (EbbinghausService.MBinder)service;
            binder.startBackNotifi();
            getActivity().unbindService(con);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        exit = (Preference)findPreference("exit");
        setBN = (SwitchPreference)findPreference("setBackNotifi");

        setBN.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean)newValue){
                    Intent intent = new Intent("com.nopu70.services.EBHSSERVICE");
                    intent.setPackage(getActivity().getPackageName());
                    getActivity().bindService(intent, con, Service.BIND_AUTO_CREATE);
                }
                return true;
            }
        });

        exit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().finish();
                return false;
            }
        });



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
