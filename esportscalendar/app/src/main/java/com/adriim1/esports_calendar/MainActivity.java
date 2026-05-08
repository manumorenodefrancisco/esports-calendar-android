package com.adriim1.esports_calendar;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ImageView navHome, navNotifications, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        navHome = findViewById(R.id.nav_home);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        navHome.setOnClickListener(v -> viewPager.setCurrentItem(0));
        navNotifications.setOnClickListener(v -> viewPager.setCurrentItem(1));
        navProfile.setOnClickListener(v -> viewPager.setCurrentItem(2));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                navHome.setAlpha(0.5f);
                navNotifications.setAlpha(0.5f);
                navProfile.setAlpha(0.5f);
                
                switch (position) {
                    case 0:
                        navHome.setAlpha(1.0f);
                        break;
                    case 1:
                        navNotifications.setAlpha(1.0f);
                        break;
                    case 2:
                        navProfile.setAlpha(1.0f);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        navHome.setAlpha(1.0f);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager) {
            super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new HomeFragment();
                case 1: return new NotificationsFragment();
                case 2: return new PerfilFragment();
                default: return new HomeFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
