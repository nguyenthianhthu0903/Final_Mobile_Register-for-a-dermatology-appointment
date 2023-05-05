package com.example.myfinal.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myfinal.Patient.PatientAccountFragment;
import com.example.myfinal.Patient.PatientChatFragment;
import com.example.myfinal.Patient.PatientHomeFragment;
import com.example.myfinal.Patient.PatientScheduleFragment;

public class ViewPagerPatientAdapter extends FragmentStatePagerAdapter {
    public ViewPagerPatientAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PatientHomeFragment();
            case 1:
                return new PatientScheduleFragment();
            case 2:
                return new PatientChatFragment();
            case 3:
                return new PatientAccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
