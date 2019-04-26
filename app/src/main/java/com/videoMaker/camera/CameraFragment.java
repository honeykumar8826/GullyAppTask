package com.videoMaker.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import com.videoMaker.R;
import com.videoMaker.activity.ShowRecordedVideoActivity;
import com.videoMaker.prefManager.SharedPreference;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class CameraFragment extends CameraVideoFragment {

    public static TextView remainSecond;
    @BindView(R.id.mTextureView)
    AutoFitTextureView mTextureView;
    @BindView(R.id.mRecordVideo1)
    ImageView mRecordVideo;
    @BindView(R.id.mVideoView)
    VideoView mVideoView;
    @BindView(R.id.mPlayVideo)
    ImageView mPlayVideo;
    @BindView(R.id.relative_bottom)
    RelativeLayout relativeLayoutBottom;
    Unbinder unbinder;
    ImageView moveNextActivity,openGallery,switchCamera /*flashLight,*//*switchCamera,*//* *//*openGallery, moveNextActivity*/;
    View view;
    private String mOutputFilePath;
    private static final String VIDEO_DIRECTORY_NAME = "RealmeVideo";


    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, view);
        // set the id of imageviews
        setInItId();
        unbinder = ButterKnife.bind(this, view);
   /*     // listener to open flash light
        flashLight.setOnClickListener(v -> {
            if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

                *//*Camera cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();*//*
                Toast.makeText(getActivity(), "Flash button", Toast.LENGTH_SHORT).show();
            }
        });*/
        return view;
    }

    private void setInItId() {
        remainSecond = view.findViewById(R.id.remain_seconds);
        moveNextActivity = view.findViewById(R.id.play_on_next_activity);
    }
    @Override
    protected void setUp(View view) {

    }

    @OnClick({R.id.mRecordVideo1, R.id.mPlayVideo, R.id.cam_switch, R.id.open_gallery,
            R.id.remain_seconds, R.id.play_on_next_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mRecordVideo1:
                /*
                 * If media is not recoding then start recording else stop recording
                 */
                if (mIsRecordingVideo) {
                    try {

                        stopRecordingVideo();
                        if (waitTimer != null) {
                            waitTimer.cancel();
                            waitTimer = null;
                            remainSecond.setVisibility(View.GONE);
                            moveNextActivity.setVisibility(View.VISIBLE);
                        }
                        StoreDataInSharedPref(mOutputFilePath);
                        prepareViews();
                      //  playVideoOnNextActivity();
                        Toast.makeText(getActivity(), "Video Recorded Successfully", Toast.LENGTH_SHORT).show();
                        // prepareViews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    startRecordingVideo();
                    /*boolean isVideoSave = true;*/
                    // i have changed the image
                    mRecordVideo.setImageResource(R.drawable.ic_camera_push_to_record);
                    //Receive out put file here
                    mOutputFilePath = getCurrentFile().getAbsolutePath();
                }
                break;
            case R.id.mPlayVideo:
                mVideoView.start();
                mPlayVideo.setVisibility(View.GONE);
                break;
            case R.id.cam_switch:
                Toast.makeText(getActivity(), "Switch Camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.open_gallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivity(intent);
                break;
            case R.id.play_on_next_activity:
                /*Intent moveIntent = new Intent(getActivity(), ShowRecordedVideoActivity.class);
                moveIntent.putExtra("videoUri", mOutputFilePath);
                startActivity(moveIntent);*/
                break;
            default:
                break;

        }
    }
    private void prepareViews() {
        if (mVideoView.getVisibility() == View.GONE) {
            mVideoView.setVisibility(View.VISIBLE);
           // mPlayVideo.setVisibility(View.VISIBLE);
            mTextureView.setVisibility(View.GONE);
            try {
                setMediaForRecordVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMediaForRecordVideo() throws IOException {
        relativeLayoutBottom.setVisibility(View.GONE);
        mOutputFilePath = parseVideo(mOutputFilePath);
        // Set media controller
        mVideoView.setMediaController(new MediaController(getActivity()));
        mVideoView.requestFocus();
        mVideoView.setVideoPath(mOutputFilePath);
        mVideoView.seekTo(100);
        mVideoView.setOnCompletionListener(mp -> {
            // Reset player
            mVideoView.setVisibility(View.GONE);
            mTextureView.setVisibility(View.VISIBLE);
            mPlayVideo.setVisibility(View.VISIBLE);
            mPlayVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayVideo.setVisibility(View.GONE);
                    prepareViews();
                        Toast.makeText(getActivity(), "play video", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String parseVideo(String mFilePath) throws IOException {
        DataSource channel = new FileDataSourceImpl(mFilePath);
        IsoFile isoFile = new IsoFile(channel);
        List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);
        boolean isError = false;
        for (TrackBox trackBox : trackBoxes) {
            TimeToSampleBox.Entry firstEntry = trackBox.getMediaBox().getMediaInformationBox().getSampleTableBox().getTimeToSampleBox().getEntries().get(0);
            // Detect if first sample is a problem and fix it in isoFile
            // This is a hack. The audio deltas are 1024 for my files, and video deltas about 3000
            // 10000 seems sufficient since for 30 fps the normal delta is about 3000
            if (firstEntry.getDelta() > 10000) {
                isError = true;
                firstEntry.setDelta(3000);
            }
        }
        File file = getOutputMediaFile();
        String filePath = file.getAbsolutePath();
        if (isError) {
            Movie movie = new Movie();
            for (TrackBox trackBox : trackBoxes) {
                movie.addTrack(new Mp4TrackImpl(channel.toString() + "[" + trackBox.getTrackHeaderBox().getTrackId() + "]", trackBox));
            }
            movie.setMatrix(isoFile.getMovieBox().getMovieHeaderBox().getMatrix());
            Container out = new DefaultMp4Builder().build(movie);

            //delete file first!
            FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
         //   Log.d(TAG, "Finished correcting raw video");
            return filePath;
        }
        return mFilePath;
    }
    private File getOutputMediaFile() {
        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                VIDEO_DIRECTORY_NAME);
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                /*Log.d(TAG, "Oops! Failed create "
                        + VIDEO_DIRECTORY_NAME + " directory");*/
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }

    private void playVideoOnNextActivity() {

        Intent moveIntent = new Intent(getActivity(), ShowRecordedVideoActivity.class);
        moveIntent.putExtra("videoUri", mOutputFilePath);
        startActivity(moveIntent);
    }

    private void StoreDataInSharedPref(String videoFilePath) {
        SharedPreference sharedPreference = SharedPreference.getInstance();
        sharedPreference.insertVideo(videoFilePath);
    }

    @Override
    public void onPause() {
        super.onPause();
        //StoreDataInSharedPref(mOutputFilePath);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}