package com.example.musicplayer;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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
    ArrayList<Song> arrayListSong = new ArrayList<>();
    String[] supportedExtensions = {".mp3", ".m4a"};
    ListView listView;
    ItemAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        init();
        return rootView;
    }

    private void init() {
        listView = (ListView) rootView.findViewById(R.id.listviewPlaylist);
        arrayListSong = readSongs(Environment.getExternalStorageDirectory());
        adapter = new ItemAdapter(rootView.getContext(), R.layout.item_view, arrayListSong);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(rootView.getContext(), "Picked item number: " + ((int)i + 1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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


}
