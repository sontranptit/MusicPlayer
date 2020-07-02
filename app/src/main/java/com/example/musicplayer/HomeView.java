package com.example.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    boolean buttonPressed, repeatMode, shuffleMode = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        repeatMode = ((MainActivity)getActivity()).isRepeat;
        shuffleMode = ((MainActivity)getActivity()).isShuffle;
        if (repeatMode){
            btnRep.setImageResource(R.drawable.repeat);
        }else{
            btnRep.setImageResource(R.drawable.no_repeat);
        }
        if (shuffleMode){
            btnShuffle.setImageResource(R.drawable.shuffle);
        }else{
            btnShuffle.setImageResource(R.drawable.no_shuffle);
        }
        pos = ((MainActivity)getActivity()).pos;
        ((MainActivity) getActivity()).initMediaPlayer(pos);
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
                buttonPressed = true;
                boolean check = mp.isPlaying();
                if (shuffleMode){
                    pos = ((MainActivity)getActivity()).shuffleNext();
                    Toast.makeText(rootView.getContext(), "Next id: " + pos, Toast.LENGTH_SHORT).show();
                }else {
                    pos = ((MainActivity)getActivity()).pos;
                    if (pos+1 > arrayList.size() - 1) {
                        pos = 0;
                    }else {
                        pos++;
                    }
                }
                ((MainActivity) getActivity()).setPos(pos);
                mp.stop();
                mp.reset();
                ((MainActivity) getActivity()).initMediaPlayer(pos);
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
                buttonPressed = true;
                boolean check = mp.isPlaying();
                pos = ((MainActivity) getActivity()).pos;
                if (pos-1 < 0) {
                    pos = arrayList.size() - 1;
                }else{
                    pos--;
                }
                ((MainActivity) getActivity()).setPos(pos);
                mp.stop();
                mp.reset();
                ((MainActivity) getActivity()).initMediaPlayer(pos);
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
                if (((MainActivity)getActivity()).isShuffle){
                    Toast.makeText(rootView.getContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.no_shuffle);
                    ((MainActivity)getActivity()).setShuffle(false);
                    shuffleMode = false;
                }else{
                    Toast.makeText(rootView.getContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.shuffle);
                    ((MainActivity)getActivity()).setShuffle(true);
                    shuffleMode = true;
                }
            }
        });

        btnRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatMode = ((MainActivity)getActivity()).isRepeat;
                if (repeatMode){
                    Toast.makeText(rootView.getContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRep.setImageResource(R.drawable.no_repeat);
                    repeatMode = false;
                }else{
                    Toast.makeText(rootView.getContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    btnRep.setImageResource(R.drawable.repeat);
                    repeatMode = true;
                }
                ((MainActivity)getActivity()).setRepeat(repeatMode);
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
        mp = ((MainActivity) getActivity()).mediaPlayer;
        arrayList = ((MainActivity) getActivity()).arrayListSong;
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
                if (repeatMode){

                }else {
                    if (buttonPressed){
                        buttonPressed = false;
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
                        btnPlay.setImageResource(R.drawable.stop);
                    }else{
                        if (mp.getCurrentPosition() == 0) {
                            try {
                                if (shuffleMode){
                                    // just get the current pos
                                }else {
                                    if (pos + 1 > arrayList.size() - 1) {
                                        pos = 0;
                                    } else {
                                        pos++;
                                    }
                                }
                                txtName.setText(arrayList.get(pos).getTitle());
                                Bitmap tmpBitmap = null;
                                tmpBitmap = arrayList.get(pos).getBitmap();
                                if (tmpBitmap != null) {
                                    imgCover.setImageBitmap(tmpBitmap);
                                } else {
                                    imgCover.setImageDrawable(getResources().getDrawable(R.drawable.music));
                                }
                                setTotalTimer();
                                btnPlay.setImageResource(R.drawable.stop);
                            }catch (Exception e){

                            }
                        }
                    }
                }


                handler.postDelayed(this, 100);
            }
        }, 100);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mp.isPlaying()) {
            pos = ((MainActivity) getActivity()).pos;
//            Toast.makeText(rootView.getContext(), "You called me! Current pos = " + pos, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPause() {
        super.onPause();
    }
}
