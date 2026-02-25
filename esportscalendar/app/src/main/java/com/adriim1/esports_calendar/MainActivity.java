package com.adriim1.esports_calendar;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageView navHome, navNotifications, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        navHome = findViewById(R.id.nav_home);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        navHome.setOnClickListener(v -> viewPager.setCurrentItem(0));
        navNotifications.setOnClickListener(v -> viewPager.setCurrentItem(1));
        navProfile.setOnClickListener(v -> viewPager.setCurrentItem(2));

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                navHome.setAlpha(0.5f);//semi-transparencia
                navNotifications.setAlpha(0.5f);
                navProfile.setAlpha(0.5f);
                
                switch (position) {
                    case 0:
                        navHome.setAlpha(1.0f);//opacidad total
                        break;
                    case 1:
                        navNotifications.setAlpha(1.0f);
                        break;
                    case 2:
                        navProfile.setAlpha(1.0f);
                        break;
                }
            }
        });

        navHome.setAlpha(1.0f);//por defecto
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new HomeFragment();
                case 1: return new NotificationsFragment();
                case 2: return new PerfilFragment();
                default: return new HomeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
