package com.videoMaker.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.videoMaker.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowRecordedVideoActivity extends AppCompatActivity {
    private static final String TAG = "RecordedVideoActivity";
    @BindView(R.id.video_play)
    VideoView mVideo;
    @BindView(R.id.mPlayVideo)
    ImageView playVideo;
    private String videoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recorded_video);
        ButterKnife.bind(this);
        // get the value from the intent
        Intent intent = getIntent();
        videoData = intent.getStringExtra("videoUri");
        Log.i(TAG, "onCreate intent value" + videoData);
//         video in  a player
        try {
            setVideoInBackground();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setVideoInBackground() throws IOException {
        // setMediaForRecordVideo();
        Uri uri = Uri.parse(videoData);
        mVideo.setVideoURI(uri);
        mVideo.setOnCompletionListener(mp -> playVideo.setVisibility(View.VISIBLE));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ShowRecordedVideoActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.mPlayVideo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mPlayVideo:
                playVideo.setVisibility(View.GONE);
                mVideo.start();
                break;
            default:
                break;
        }
    }
}
