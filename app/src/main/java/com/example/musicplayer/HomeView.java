package com.example.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomeView extends Fragment {
    View rootView;
    TextView txtName, txtCurrentTime, txtTotalTime;
    ImageButton btnShuffle, btnPrev, btnPlay, btnNext, btnRep;
    ViewSwitcher viewSwitcher;
    ImageView imgCover;
    SeekBar seekBar;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ArrayList<Song> arrayListSong = new ArrayList<>();
    String[] supportedExtensions = {".mp3", ".m4a"};
    int pos = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        arrayListSong = readSongs(Environment.getExternalStorageDirectory());
        initMediaPlayer();
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
        return rootView;
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
        }
    }

    private void init() {
        txtName = rootView.findViewById(R.id.textViewName);
        txtCurrentTime = rootView.findViewById(R.id.textViewCurrentTime);
        txtTotalTime = rootView.findViewById(R.id.textViewTotalTime);
        btnShuffle = rootView.findViewById(R.id.imageButtonShuffle);
        btnPrev = rootView.findViewById(R.id.imageButtonPrev);
        btnPlay = rootView.findViewById(R.id.imageButtonPlay);
        btnNext = rootView.findViewById(R.id.imageButtonNext);
        btnRep = rootView.findViewById(R.id.imageButtonRep);
        seekBar = rootView.findViewById(R.id.seekBar);
        imgCover = rootView.findViewById(R.id.imageCover);
    }

    private void setTotalTimer() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        txtTotalTime.setText(timeFormat.format(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void updateSongProgress() {
        final Handler handler = new Handler();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtCurrentTime.setText(timeFormat.format(mediaPlayer.getCurrentPosition()));
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
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

    @Override
    public void onResume() {
        super.onResume();

    }

}
