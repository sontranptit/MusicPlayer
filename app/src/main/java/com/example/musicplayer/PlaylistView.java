package com.example.musicplayer;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class PlaylistView extends Fragment {
    View rootView;
    ArrayList<Song> arrayList;
    ListView listView;
    ItemAdapter adapter;
    MediaPlayer mp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        init();
        return rootView;
    }

    private void init() {
        listView = (ListView) rootView.findViewById(R.id.listviewPlaylist);
        arrayList = ((MainActivity)getActivity()).arrayListSong;
        mp = ((MainActivity)getActivity()).mediaPlayer;
        adapter = new ItemAdapter(rootView.getContext(), R.layout.item_view, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).setPos(i);
                boolean check = mp.isPlaying();
                mp.stop();
                mp.reset();
                ((MainActivity)getActivity()).initMediaPlayer(i);
                if (check) {
                    mp.start();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }



}
