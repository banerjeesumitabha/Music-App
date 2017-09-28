package com.example.android.funmediaplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PlayingActivity extends AppCompatActivity {

    private int count = 0;
    private MediaPlayer song = null;
    private AudioManager audio;
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
                finish();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                song.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 4);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                song.start();
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 9);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume < 3) {
            Toast.makeText(this, "Please Increase Volume", Toast.LENGTH_SHORT).show();
        }

        int result = audio.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            if (song != null) {
                releaseMediaPlayer();
            }
            long songId = getIntent().getLongExtra("SongId", 0);
            String songName = getIntent().getStringExtra("SongName");

            TextView current = (TextView) findViewById(R.id.current);
            current.setText("Currently Playing: " + songName);

            Uri contentUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);

            song = new MediaPlayer();
            song.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                song.setDataSource(getApplicationContext(), contentUri);
                song.prepare();
                song.start();
            } catch (IOException ex) {
                Toast.makeText(PlayingActivity.this, "Song does not exist", Toast.LENGTH_SHORT).show();
            }


            song.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Toast.makeText(PlayingActivity.this, "Song Completed", Toast.LENGTH_SHORT).show();
                    releaseMediaPlayer();
                    finish();
                }
            });
        }
    }

    public void releaseMediaPlayer() {
        if (song != null) {
            song.stop();

            song.release();
            song = null;
        }
        audio.abandonAudioFocus(afChangeListener);
        count = 0;
    }

    public void pause(View view) {
        if (song != null) {
            if (count % 2 == 0) {
                song.pause();
                count++;
                audio.abandonAudioFocus(afChangeListener);
                ImageView pause = (ImageView) findViewById(R.id.pause_button);
                pause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
            } else {
                song.start();
                count++;
                ImageView pause = (ImageView) findViewById(R.id.pause_button);
                pause.setImageResource(R.drawable.ic_pause_white_24dp);
            }
        }
    }

    public void replay(View view) {
        song.seekTo(0);
    }

    public void forward(View view) {
        if (song.getDuration() > song.getCurrentPosition() + 10000)
            song.seekTo(song.getCurrentPosition() + 10000);
        else
            song.seekTo(0);
    }

    public void stop(View view) {
        releaseMediaPlayer();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (song != null) {
            Toast.makeText(this, "Song Stopped", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        }
    }
}
