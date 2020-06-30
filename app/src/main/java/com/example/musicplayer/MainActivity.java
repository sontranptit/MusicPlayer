package com.example.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //
    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // check for this stupid permission :)
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        }
        findView();
        createDrawerToggle();
        showDisplayWithIndex(0);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                // change view here, maybe?
                String newViewName = (String) menuItem.getTitle();

                if (newViewName.equals("Home")) {
                    showDisplayWithIndex(0);
                } else if (newViewName.equals("Equalizer")) {
                    showDisplayWithIndex(1);
                } else if (newViewName.equals("Sleep Timer")) {
                    showDisplayWithIndex(2);
                } else if (newViewName.equals("Favourites")) {
                    showDisplayWithIndex(3);
                } else if (newViewName.equals("Playlists")) {
                    showDisplayWithIndex(4);
                }
                return true;
            }
        });


    }


    void showDisplayWithIndex(int index) {
        Fragment fragment = null;

        switch (index) {
            case 0: {
                fragment = new HomeView();
                break;
            }
            case 1: {
                fragment = new EqualizerView();
                break;
            }
            case 2: {
                fragment = new SleepTimerView();
                break;
            }
            case 3: {
                fragment = new FavouritesView();
                break;
            }
            case 4: {
                fragment = new PlaylistView();
                break;
            }
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    protected void findView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

    }

//    private void changeView(LinearLayout defaultView, int newView) {
//        LayoutInflater inflater = getLayoutInflater();
//        defaultView.removeAllViews();
//        inflater.inflate(newView, defaultView);
//        defaultView = (LinearLayout) findViewById(newView);
//    }


    private void createDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            // this is listener for the Hamburger icon
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        // do stuff
                    } else {

                    }
                }
            }
        }
    }

}
