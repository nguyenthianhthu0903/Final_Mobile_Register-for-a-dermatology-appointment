package com.example.myfinal.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myfinal.Doctor.DoctorAccountFragment;
import com.example.myfinal.Doctor.DoctorChatFragment;
import com.example.myfinal.Doctor.DoctorHomeFragment;
import com.example.myfinal.Doctor.ApptUpcomingFragment;

public class ViewPagerDoctorAdapter extends FragmentStatePagerAdapter {
    public ViewPagerDoctorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DoctorHomeFragment();
            case 1:
                return new ApptUpcomingFragment();
            case 2:
                return new DoctorChatFragment();
            case 3:
                return new DoctorAccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
