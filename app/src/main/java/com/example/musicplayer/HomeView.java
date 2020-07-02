package com.example.musicplayer;

import android.content.Context;
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
import android.widget.Toast;
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
    ImageView imgCover;
    SeekBar seekBar;
    //
    MediaPlayer mp;
    ArrayList<Song> arrayList;
    int pos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init();


        ((MainActivity)getActivity()).initMediaPlayer(pos);
        //
        txtName.setText(arrayList.get(pos).getTitle());
        Bitmap tmpBitmap = null;
        tmpBitmap = arrayList.get(pos).getBitmap();
        if (tmpBitmap != null) {
            imgCover.setImageBitmap(tmpBitmap);
        } else {
            imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
        }
        setTotalTimer();
        //
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btnPlay.setImageResource(R.drawable.play);
                } else {
                    mp.start();
                    btnPlay.setImageResource(R.drawable.stop);
                }
                updateSongProgress();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos++;
                boolean check = mp.isPlaying();
                if (pos > arrayList.size() - 1) {
                    pos = 0;
                }
                ((MainActivity)getActivity()).setPos(pos);
                mp.stop();
                mp.reset();
                ((MainActivity)getActivity()).initMediaPlayer(pos);
                //
                txtName.setText(arrayList.get(pos).getTitle());
                Bitmap tmpBitmap = null;
                tmpBitmap = arrayList.get(pos).getBitmap();
                if (tmpBitmap != null) {
                    imgCover.setImageBitmap(tmpBitmap);
                } else {
                    imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
                }
                setTotalTimer();
                //
                if (check) {
                    mp.start();
                    btnPlay.setImageResource(R.drawable.stop);
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos--;
                boolean check = mp.isPlaying();
                if (pos < 0) {
                    pos = arrayList.size() - 1;
                }
                ((MainActivity)getActivity()).setPos(pos);
                mp.stop();
                mp.reset();
                ((MainActivity)getActivity()).initMediaPlayer(pos);
                //
                txtName.setText(arrayList.get(pos).getTitle());
                Bitmap tmpBitmap = null;
                tmpBitmap = arrayList.get(pos).getBitmap();
                if (tmpBitmap != null) {
                    imgCover.setImageBitmap(tmpBitmap);
                } else {
                    imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
                }
                setTotalTimer();
                //
                if (check) {
                    mp.start();
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
                mp.seekTo(seekBar.getProgress());
            }
        });
        return rootView;
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
        mp = ((MainActivity)getActivity()).mediaPlayer;
        pos = ((MainActivity)getActivity()).pos;
        arrayList = ((MainActivity)getActivity()).arrayListSong;
    }

    private void setTotalTimer() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        txtTotalTime.setText(timeFormat.format(mp.getDuration()));
        seekBar.setMax(mp.getDuration());
    }

    private void updateSongProgress() {
        final Handler handler = new Handler();
        final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtCurrentTime.setText(timeFormat.format(mp.getCurrentPosition()));
                seekBar.setProgress(mp.getCurrentPosition());
                // check if finish
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        pos++;
                        boolean check = mp.isPlaying();
                        if (pos > arrayList.size() - 1) {
                            pos = 0;
                        }
                        mp.stop();
                        mp.reset();
                        ((MainActivity)getActivity()).initMediaPlayer(pos);
                        //
                        txtName.setText(arrayList.get(pos).getTitle());
                        Bitmap tmpBitmap = null;
                        tmpBitmap = arrayList.get(pos).getBitmap();
                        if (tmpBitmap != null) {
                            imgCover.setImageBitmap(tmpBitmap);
                        } else {
                            imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
                        }
                        setTotalTimer();
                        //
                        if (check) {
                            mp.start();
                            btnPlay.setImageResource(R.drawable.stop);
                        }
                        mp.start();
                    }
                });
                handler.postDelayed(this, 100);
            }
        }, 100);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mp.isPlaying()){
            pos = ((MainActivity)getActivity()).getPos();
            Toast.makeText(rootView.getContext(), "You called me! Current pos = " + pos, Toast.LENGTH_SHORT).show();
            txtName.setText(arrayList.get(pos).getTitle());
            Bitmap tmpBitmap = null;
            tmpBitmap = arrayList.get(pos).getBitmap();
            if (tmpBitmap != null) {
                imgCover.setImageBitmap(tmpBitmap);
            } else {
                imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
            }
            btnPlay.setImageResource(R.drawable.stop);
            setTotalTimer();
            updateSongProgress();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}
