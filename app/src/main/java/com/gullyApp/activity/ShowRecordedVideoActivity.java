package com.gullyApp.activity;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.gullyApp.R;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShowRecordedVideoActivity extends AppCompatActivity {
    private static final String TAG = "ShowRecordedVideoActivi";
    private VideoView mVideo;
    private String  videoData;
    private ImageView playVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recorded_video);
//        set the id for layout
        setInItId();
        // get the value from the intent
        Intent intent = getIntent();
        videoData = intent.getStringExtra("videoUri");
        Log.i(TAG, "onCreate intnet value" + videoData);
//         video in  a player

        try {
            setVideoInBackground();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo.setVisibility(View.GONE);
                mVideo.start();
            }
        });

    }

    private void setInItId() {
        mVideo = findViewById(R.id.video_play);
        playVideo = findViewById(R.id.mPlayVideo);
    }

    private void setVideoInBackground() throws IOException {
        // setMediaForRecordVideo();
        Uri uri = Uri.parse(videoData);
        mVideo.setVideoURI(uri);
        mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playVideo.setVisibility(View.VISIBLE);
            }
        });

    }

}
