package com.google.ar.sceneform.samples.gltf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.samples.gltf.adapter.ViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class home extends AppCompatActivity{


    BottomNavigationView bottomMenu;
    ViewPager viewPager;
    FloatingActionButton favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        setUpViewPager();

        bottomMenu = findViewById(R.id.bottommenu);
        favorite = findViewById(R.id.favorite);

        favorite.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, favorite.class);
            intent.putExtra("category", "favorite");
            startActivity(intent);
        });

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homemenu:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.feedmenu:
                        viewPager.setCurrentItem(1);
                        break;
                    default:
                        viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });


//        LoadData();

    }

    private void setUpViewPager() {
        viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomMenu.setSelectedItemId(R.id.homemenu);
                        break;
                    case 1:
                        bottomMenu.setSelectedItemId(R.id.feedmenu);
                        break;
                    default:
                        bottomMenu.setSelectedItemId(R.id.homemenu);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}
