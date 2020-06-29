package com.example.musicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView txtName, txtCurrentTime, txtTotalTime;
    ImageButton btnShuffle, btnPrev, btnPlay, btnNext, btnRep;
    ImageView imgCover;
    SeekBar seekBar;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<Song> arrayListSong = new ArrayList<>();
    String[] supportedExtensions = {".mp3", ".m4a"};
    int pos = 0;

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
        arrayListSong = readSongs(Environment.getExternalStorageDirectory());
        initMediaPlayer();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                // change view here, maybe?
                String currentViewName = (String) menuItem.getTitle();

                if (currentViewName.equals("Home")) {

                } else if (currentViewName.equals("Equalizer")) {

                } else if (currentViewName.equals("Sleep Timer")) {

                } else if (currentViewName.equals("Favourites")) {

                } else if (currentViewName.equals("Playlists")) {


                }


                return true;
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.play);
                } else {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.stop);
                }
                updateSongProgress();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos++;
                boolean check = mediaPlayer.isPlaying();
                if (pos > arrayListSong.size() - 1) {
                    pos = 0;
                }
                mediaPlayer.stop();
                mediaPlayer.reset();
                initMediaPlayer();
                if (check) {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.stop);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos--;
                boolean check = mediaPlayer.isPlaying();
                if (pos < 0) {
                    pos = arrayListSong.size() - 1;
                }
                mediaPlayer.stop();
                mediaPlayer.reset();
                initMediaPlayer();
                if (check) {
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.stop);
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource(arrayListSong.get(pos).getFile());
            mediaPlayer.prepare();
            txtName.setText(arrayListSong.get(pos).getTitle());
            Bitmap tmpBitmap = null;
            tmpBitmap = arrayListSong.get(pos).getBitmap();
            if (tmpBitmap != null) {
                imgCover.setImageBitmap(tmpBitmap);
            } else {
                imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
            }
            setTotalTimer();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    protected void findView() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        txtName = findViewById(R.id.textViewName);
        txtCurrentTime = findViewById(R.id.textViewCurrentTime);
        txtTotalTime = findViewById(R.id.textViewTotalTime);
        btnShuffle = findViewById(R.id.imageButtonShuffle);
        btnPrev = findViewById(R.id.imageButtonPrev);
        btnPlay = findViewById(R.id.imageButtonPlay);
        btnNext = findViewById(R.id.imageButtonNext);
        btnRep = findViewById(R.id.imageButtonRep);
        seekBar = findViewById(R.id.seekBar);
        imgCover = findViewById(R.id.imageCover);
    }

    private void setTotalTimer(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        txtTotalTime.setText(timeFormat.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void updateSongProgress(){
        final Handler handler = new Handler();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtCurrentTime.setText(timeFormat.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition() );
                // check if finish
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pos++;
                        boolean check = mediaPlayer.isPlaying();
                        if (pos > arrayListSong.size() - 1) {
                            pos = 0;
                        }
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        initMediaPlayer();
                        if (check) {
                            mediaPlayer.start();
                            btnPlay.setImageResource(R.drawable.stop);
                        }
                        mediaPlayer.start();
                    }
                });
                handler.postDelayed(this, 100);
            }
        }, 100);
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

    private void createDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                        // do stuff
                    } else {
                        Toast.makeText(this, "Still no permission! WTF?", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            // this is listener for the Hamburger icon
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
