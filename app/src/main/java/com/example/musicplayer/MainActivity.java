package com.example.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    //
    ArrayList<Song> arrayListSong = new ArrayList<>();
    String[] supportedExtensions = {".mp3", ".m4a"};
    int pos = 0;
    MediaPlayer mediaPlayer = new MediaPlayer();

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
        arrayListSong = readSongs(Environment.getExternalStorageDirectory());
        createDrawerToggle();
        initMediaPlayer(0);
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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pos++;
                if (pos > arrayListSong.size() - 1) {
                    pos = 0;
                }
                mediaPlayer.stop();
                mediaPlayer.reset();
                initMediaPlayer(pos);
                mediaPlayer.start();
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

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    protected void findView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

    }



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

    private ArrayList<Song> readSongs(File root) {
        ArrayList<Song> temp = new ArrayList<>();
        File files[] = root.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                temp.addAll(readSongs(f));
            } else {
                for (int i = 0; i < supportedExtensions.length; i++) {
                    if (f.getName().endsWith(supportedExtensions[i])) {
                        String tmpPath = f.getAbsolutePath();
                        String tmpName = f.getName();
                        Bitmap bitmap = null;
                        // try to extract bitmap using codes from stackoverflow. Edited a little bit
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(tmpPath);
                        byte[] artBytes = mmr.getEmbeddedPicture();
                        if (artBytes != null) {
                            InputStream is = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                            bitmap = BitmapFactory.decodeStream(is);
                        }
//                        else
//                        {
//                            imgArt.setImageDrawable(getResources().getDrawable(R.drawable.adele));
//                        }
                        // ok it ends here
                        temp.add(new Song(tmpName, tmpPath, bitmap));
                    }
                }

            }
        }
        return temp;
    }

    public void initMediaPlayer(int position) {
        try {
            mediaPlayer.setDataSource(arrayListSong.get(position).getFile());
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
