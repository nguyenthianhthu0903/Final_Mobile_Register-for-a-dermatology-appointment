package com.example.myfinal.Patient;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myfinal.Adapter.ViewPagerPatientAdapter;
import com.example.myfinal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PatientBottomNavigationBar extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_main);
        bottomNavigationView = findViewById(R.id.bottom_menu);
        viewPager = findViewById(R.id.view_pager);

        setUpViewPager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.bottom_schedule:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.bottom_chat:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.bottom_account:
                    viewPager.setCurrentItem(3);
                    break;
//                    case R.id.bottom_schedule:
//                        break;
//                    case R.id.bottom_discover:
//                        break;
//                    case R.id.bottom_chat:
//                        break;
//                    case R.id.bottom_account:
//                        break;
            }
            return true;
        });
    }

    public void setUpViewPager() {
        ViewPagerPatientAdapter viewPagerPatientAdapter = new ViewPagerPatientAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerPatientAdapter);
    }
}
