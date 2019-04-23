package com.videoMaker.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.videoMaker.R;
import com.videoMaker.activity.ShowRecordedVideoActivity;
import com.videoMaker.prefManager.SharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/*
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends CameraVideoFragment {

    /**
     * 0 forback camera
     * 1 for front camera
     * Initlity default camera is front camera
     */
   /* public static final String CAMERA_FRONT = "1";
    private static final String TAG = "CameraFragment";
    private static final String VIDEO_DIRECTORY_NAME = "HTask";*/
    public static TextView remainSecond;
    @BindView(R.id.mTextureView)
    AutoFitTextureView mTextureView;
    @BindView(R.id.mRecordVideo1)
    ImageView mRecordVideo;
    @BindView(R.id.mVideoView)
    VideoView mVideoView;
    @BindView(R.id.mPlayVideo)
    ImageView mPlayVideo;
    Unbinder unbinder;
    ImageView moveNextActivity /*flashLight,*//*switchCamera,*//* *//*openGallery, moveNextActivity*/;
    View view;
    private String mOutputFilePath;


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

/*    @Override
    public int getTextureResource() {
        return R.id.mTextureView;
    }*/

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
                        playVideoOnNextActivity();
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
                Intent moveIntent = new Intent(getActivity(), ShowRecordedVideoActivity.class);
                moveIntent.putExtra("videoUri", mOutputFilePath);
                startActivity(moveIntent);
                break;
            default:
                break;

        }
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