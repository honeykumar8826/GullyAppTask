package com.gullyApp.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
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
import com.gullyApp.R;
import com.gullyApp.activity.HomeActivity;
import com.gullyApp.activity.ShowRecordedVideoActivity;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends CameraVideoFragment {

    private static final String TAG = "CameraFragment";
    private static final String VIDEO_DIRECTORY_NAME = "HTask";
    @BindView(R.id.mTextureView)
    AutoFitTextureView mTextureView;
    @BindView(R.id.mRecordVideo1)
    ImageView mRecordVideo;
    @BindView(R.id.mVideoView)
    VideoView mVideoView;
    @BindView(R.id.mPlayVideo)
    ImageView mPlayVideo;
    Unbinder unbinder;
    private String mOutputFilePath;
    ImageView flashLight,switchCamera,openGallery,moveNextActivity;
    View view;
    public static TextView remainSecond;
    private  static boolean isVideoSave= false;
    /** 0 forback camera
     * 1 for front camera
     * Initlity default camera is front camera */
    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";
    private String cameraId = CAMERA_FRONT;


    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */


    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        // set the id of imageviews
        setInItId();
        unbinder = ButterKnife.bind(this, view);
        // listener to open flash light
        flashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
                {

                    Camera cam = Camera.open();
                    Camera.Parameters p = cam.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    cam.setParameters(p);
                    cam.startPreview();
                }
            }
        });
        // listener to open flash light
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Switch Camera", Toast.LENGTH_SHORT).show();
            }
        });
        // listener to open gallery
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivity(intent);
            }
        });
        // listener to move on a next activity
        moveNextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveIntent = new Intent(getActivity(), ShowRecordedVideoActivity.class);
                moveIntent.putExtra("videoUri",mOutputFilePath);
                startActivity(moveIntent);
            }
        });
        return view;
    }

    private void setInItId() {
        flashLight = view.findViewById(R.id.flash_light);
        switchCamera = view.findViewById(R.id.cam_switch);
        openGallery = view.findViewById(R.id.open_gallery);
        remainSecond = view.findViewById(R.id.remain_seconds);
        moveNextActivity = view.findViewById(R.id.play_on_next_activity);

    }

    @Override
    public int getTextureResource() {
        return R.id.mTextureView;
    }

    @Override
    protected void setUp(View view) {

    }

    @OnClick({R.id.mRecordVideo1, R.id.mPlayVideo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mRecordVideo1:
                /**
                 * If media is not recoding then start recording else stop recording
                 */
                if (mIsRecordingVideo) {
                    try {

                        stopRecordingVideo();
                        if(waitTimer !=null)
                        {
                            waitTimer.cancel();
                            waitTimer =null;
                            remainSecond.setVisibility(View.GONE);
                            moveNextActivity.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(getActivity(), "Video Recorded Successfully", Toast.LENGTH_SHORT).show();
                       // prepareViews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    startRecordingVideo();
                    isVideoSave = true;
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
        }
    }

    private void prepareViews() {
        if (mVideoView.getVisibility() == View.GONE) {
            mVideoView.setVisibility(View.VISIBLE);
            mPlayVideo.setVisibility(View.VISIBLE);
            mTextureView.setVisibility(View.GONE);
            try {
                setMediaForRecordVideo();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMediaForRecordVideo() throws IOException {
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
            mPlayVideo.setVisibility(View.GONE);
            mRecordVideo.setImageResource(R.drawable.ic_record);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
            Log.d(TAG, "Finished correcting raw video");
            return filePath;
        }
        return mFilePath;
    }

    /**
     * Create directory and return file
     * returning video file
     */
    private File getOutputMediaFile() {
        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                VIDEO_DIRECTORY_NAME);
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + VIDEO_DIRECTORY_NAME + " directory");
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
    public static Boolean isVideoRecorded()
    {
        return isVideoSave;
    }
}