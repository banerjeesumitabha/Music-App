package com.example.android.funmediaplayer;


public class Song {

    private long mSongResourceId;
    private String mSongName;
    private String mArtistName;


    public Song(long songResourceId, String songName, String artistName) {
        mSongResourceId = songResourceId;
        mSongName = songName;
        mArtistName = artistName;
    }

    public long getSongResourceId() {
        return mSongResourceId;
    }

    public String getSongName() {
        return mSongName;
    }

    public String getArtistName() {
        return mArtistName;
    }

}

