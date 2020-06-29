package com.example.musicplayer;

import android.graphics.Bitmap;

public class Song {
    private String title;
    private String file;
    private String lyric;
    private Bitmap bitmap;

    public Song(String title, String file, Bitmap bitmap) {
        this.title = title;
        this.file = file;
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public String getFile() {
        return file;
    }

    public String getLyric() {
        return lyric;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
