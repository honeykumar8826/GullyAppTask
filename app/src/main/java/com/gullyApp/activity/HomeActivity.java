package com.gullyApp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gullyApp.R;
import com.gullyApp.adapter.ImageLoadAdapter;
import com.gullyApp.adapter.VideoRecyclerViewAdapter;
import com.gullyApp.camera.CameraFragment;
import com.gullyApp.camera.VideoRecordActivity;
import com.gullyApp.fragment.CommentFragment;
import com.gullyApp.fragment.SharePostFragment;
import com.gullyApp.modal.ImageModal;
import com.gullyApp.modal.VideoInfo;
import com.gullyApp.network.NetworkClient;
import com.gullyApp.playVideo.ui.ExoPlayerRecyclerView;
import com.gullyApp.utils.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.LinearLayout.VERTICAL;

public class HomeActivity extends AppCompatActivity {
    public static final String API_KEY = "9e5ef71432c64196a16273c85cfb94c1";
    private static final String TAG = "HomeActivity";
    private final String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE};
    ExoPlayerRecyclerView recyclerViewFeed;
    @BindView(R.id.camera_open)
    ImageView cameraOpen;
    @BindView(R.id.like_active)
    ImageView likeActive;
    @BindView(R.id.feed_reuse)
    ImageView feedReuse;
    @BindView(R.id.feed_option)
    ImageView feedOption;
    @BindView(R.id.tv_friend)
    TextView friend;
    @BindView(R.id.tv_popular)
    TextView popular;
    @BindView(R.id.tv_collab)
    TextView collab;
    @BindView(R.id.background_image)
    ImageView backgroundImg;
    private BottomNavigationView bottomNavigationView;
    private List<ImageModal> imageModalList;
    private RecyclerView recyclerViewNews;
    private Context context;
    private FragmentManager fragmentManager;
    private Animation animShow, animHide;
    private Boolean isFriend = true;
    private Boolean isPopular = false;
    private Boolean isCollab = false;
    private List<VideoInfo> videoInfoList = new ArrayList<>();
    private VideoRecyclerViewAdapter mAdapter;
    private boolean firstTime = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setInItId();
        ButterKnife.bind(this);
        // permission code for external storage
        externalStorgaePermission();
        // initialize the fragment manager
        fragmentManager = getSupportFragmentManager();
        // get the all video file data
        showVideoByApiData();
        //list initialization
        imageModalList = new ArrayList<>();
        //intialize the context
        context = HomeActivity.this;
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, true));
        // get the controller of the animation
        getAnimation();
        if (isNetworkConnected()) {// for News Api Result
            callNewsApi();
            // Toast.makeText(context, "internet connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "internet disconnected", Toast.LENGTH_SHORT).show();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_search:
                        Toast.makeText(HomeActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_add:
                        Toast.makeText(HomeActivity.this, "Add", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_notification:
                        Toast.makeText(HomeActivity.this, "Navigation", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_NA:
                        Toast.makeText(HomeActivity.this, "List", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        Toast.makeText(HomeActivity.this, "wrong selection", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

    }

    private void externalStorgaePermission() {
        // permission to check the required permission is given or not
//        int count = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(HomeActivity.this, permissionList)) {
//            Log.i(TAG, "checkPermission: " + count++);
                ActivityCompat.requestPermissions(HomeActivity.this, permissionList, 10);
            } else {
                Toast.makeText(this, " Permission  granted ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "permission automatically granted", Toast.LENGTH_SHORT).show();
        }


    }

    // give true if the permission is granted
    private boolean hasPermissions(Context context, String... permissions) {
        int count = 0;
        if (context != null && permissions != null) {
            Log.i(TAG, "hasPermissions: " + permissions.length);
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    count++;
                    Log.i(TAG, "hasPermissions: " + count);
                    return false;
                }
            }
        }
        return true;
    }
    // get the callback of the permission is come in the below method

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions);
            if (grantResults[0] == -1) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Log.i(TAG, "shouldShowRequestPermissionRationale:");
                    showMessageOkCancel("Storage permission is required to Video location",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    // Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Storage Permission not granted", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, " Permissions  granted ", Toast.LENGTH_SHORT).show();

            }
        }
    }
    // if permission is deny then this will open the popup to send into the setting of permission

    private void showMessageOkCancel(String permissionDetail, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(this).setMessage(permissionDetail)
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void playVideoInBackground() {
        recyclerViewFeed.setVideoInfoList(videoInfoList);
        mAdapter = new VideoRecyclerViewAdapter(videoInfoList);
        recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
        recyclerViewFeed.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        recyclerViewFeed.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFeed.setAdapter(mAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerViewFeed);

        if (firstTime) {
            new Handler(Looper.getMainLooper()).post(() -> recyclerViewFeed.playVideo());
            firstTime = false;
        }
        recyclerViewFeed.scrollToPosition(0);
    }

    private void showVideoByApiData() {
        //
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/AndroidWave");
        Log.i(TAG, "showVideoByApiData:1 " + folder);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            addVideoInList(listOfFiles);
//            Log.i(TAG, "showVideoByApiData: inside if part");
//            Log.i(TAG, "setVideoInBackground: "+listOfFiles);
//            for (int i = 0; i < listOfFiles.length; i++) {
//                VideoInfo videoInfo = new VideoInfo();
//                if (listOfFiles[i].isFile()) {
//
//                    videoInfo.setUrl(listOfFiles[i].getPath());
//                    videoInfo.setCoverUrl("https://www.gstatic.com/webp/gallery/4.webp");
//                    videoInfoList.add(videoInfo);
//                    //Log.i(TAG, "setVideoInBackground:" + listOfFiles[i].getPath());
//                    // System.out.println("File " + listOfFiles[i].getName());
//                } else if (listOfFiles[i].isDirectory()) {
//                    Log.i(TAG, "setVideoInBackground:else if " + listOfFiles[i].getName());
//                    // System.out.println("Directory " + listOfFiles[i].getName());
//                }
//            }
            playVideoInBackground();
        } else {
            backgroundImg.setVisibility(View.VISIBLE);
            // videoInfoList.clear();
            // playVideoInBackground();
            Log.i(TAG, "showVideoByApiData: inside else part");
            // Toast.makeText(context, "Make Video ", Toast.LENGTH_SHORT).show();
        }

  /*      //
        Retrofit retrofit = new Retrofit.Builder().baseUrl(NetworkClient.BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkClient api = retrofit.create(NetworkClient.class);

        Call<ResponseBody> call = api.getVideoData();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "onResponse: " + response.body());
                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("videoData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonVideoData = jsonArray.getJSONObject(i);
                        String imageUrl = jsonVideoData.getString("setCoverUrl");
                        String videoUrl = jsonVideoData.getString("setUrl");

                        VideoInfo videoInfo = new VideoInfo();
                        videoInfo.setCoverUrl(imageUrl);
                        videoInfo.setUrl(videoUrl);
                        videoInfoList.add(videoInfo);
                    }
                    // for playing video in the background
                    playVideoInBackground();

                    Log.i(TAG, "onResponse:1 " + jsonObject);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    // Toast.makeText(MainActivity.this, "Internet Issue", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Some Big Issue", Toast.LENGTH_SHORT).show();
                }

                // swipeRefreshLayout.setRefreshing(false);
            }
        });*/
    }


    private void getAnimation() {
        animShow = AnimationUtils.loadAnimation(this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(this, R.anim.view_hide);
    }

    private void addLikeRecordFragment() {
        CommentFragment commentFragment = new CommentFragment();
        // overridePendingTransition(R.anim.bottom_to_top,0);
//        commentFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        commentFragment.show(fragmentManager, "Load data");

    }

    private void setInItId() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerViewNews = findViewById(R.id.recycles_profile);
        recyclerViewFeed = findViewById(R.id.recyclerViewFeed);
    }

    // return the true if network is active
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void callNewsApi() {
        //swipeRefreshLayout.setRefreshing(true);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(NetworkClient.BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkClient api = retrofit.create(NetworkClient.class);
        Call<ResponseBody> call = api.getNews("in", API_KEY);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Log.i(TAG, "onResponse: " + response.body());
                try {
                    String result = response.body().string();
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    int totalItem = jsonObject.getInt("totalResults");
                    if (status.equals("ok") && totalItem > 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("articles");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonArticle = jsonArray.getJSONObject(i);
                                String imgUrl = jsonArticle.getString("urlToImage");
                                ImageModal imageModal = new ImageModal(imgUrl);
//                                Log.i(TAG, "values inside the for loop: " + authorName + "title" + title + "imgUrl" + imgUrl);
                                imageModalList.add(imageModal);
                            }
                            ImageLoadAdapter imageLoadAdapter = new ImageLoadAdapter(context, imageModalList);
                            recyclerViewNews.setAdapter(imageLoadAdapter);
                            // recyclerViewNews.startAnimation(animShow);
                            imageLoadAdapter.notifyDataSetChanged();
                            // recyclerViewNews.scheduleLayoutAnimation();
                            // swipeRefreshLayout.setRefreshing(false);

                        } else {
                            Log.i(TAG, "JsonArray item is zero ");
                        }
                        Log.i(TAG, "jsonArray: " + jsonArray.length());
                    } else {
                        Log.i(TAG, "else part: ");
                    }

                    // Log.i(TAG, "onResponse:1 " + status + "" + totalItem);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof IOException) {
                    // Toast.makeText(MainActivity.this, "Internet Issue", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Some Big Issue", Toast.LENGTH_SHORT).show();
                }

                // swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause:on popup open ");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: in side on pause");
                recyclerViewFeed.onPausePlayer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoInfoList.clear();
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/AndroidWave");
        Log.i(TAG, "showVideoByApiData:1 " + folder);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            addVideoInList(listOfFiles);
            mAdapter.notifyDataSetChanged();
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: inside onresume");
                recyclerViewFeed.onRestartPlayer();
            }
        });
    }

    private void addVideoInList(File[] listOfFiles) {
        /*File folder = new File(Environment.getExternalStorageDirectory().toString() + "/AndroidWave");
        Log.i(TAG, "showVideoByApiData:1 "+folder);
        File[] listOfFiles = folder.listFiles();*/
               /* Log.i(TAG, "showVideoByApiData: inside if part");
                Log.i(TAG, "setVideoInBackground: "+listOfFiles);*/
        for (int i = 0; i < listOfFiles.length; i++) {
            VideoInfo videoInfo = new VideoInfo();
            if (listOfFiles[i].isFile()) {

                videoInfo.setUrl(listOfFiles[i].getPath());
                videoInfo.setCoverUrl("https://www.gstatic.com/webp/gallery/4.webp");
                videoInfoList.add(videoInfo);
                //Log.i(TAG, "setVideoInBackground:" + listOfFiles[i].getPath());
                // System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                Log.i(TAG, "setVideoInBackground:else if " + listOfFiles[i].getName());
                // System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (recyclerViewFeed != null)
            recyclerViewFeed.onRelease();
        super.onDestroy();
    }

    private void addReuseRecordFragment() {
        SharePostFragment sharePostFragment = new SharePostFragment();
        // overridePendingTransition(R.anim.bottom_to_top,0);
//        commentFragment.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        sharePostFragment.show(fragmentManager, "Load data");
    }

    @OnClick({R.id.camera_open, R.id.like_active, R.id.feed_option, R.id.feed_reuse, R.id.tv_collab
            , R.id.tv_popular, R.id.tv_friend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_friend:
                if (isFriend) {
                    friend.setTextColor(Color.WHITE);
                    popular.setTextColor(Color.GRAY);
                    collab.setTextColor(Color.GRAY);
                    Toast.makeText(context, "friend Button is click", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_popular:
                isPopular = true;
                if (isPopular) {
                    popular.setTextColor(Color.WHITE);
                    friend.setTextColor(Color.GRAY);
                    collab.setTextColor(Color.GRAY);
                    Toast.makeText(context, "popular Button is click", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_collab:
                isCollab = true;
                if (isCollab) {
                    collab.setTextColor(Color.WHITE);
                    popular.setTextColor(Color.GRAY);
                    friend.setTextColor(Color.GRAY);
                    Toast.makeText(context, "collab Button is click", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.camera_open:
                Intent openVideoPage = new Intent(HomeActivity.this, VideoRecordActivity.class);
                startActivity(openVideoPage);
                break;
            case R.id.like_active:
                // add fragement
                addLikeRecordFragment();
                break;
            case R.id.feed_reuse:
                // add fragement
                addReuseRecordFragment();
                break;
            case R.id.feed_option:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                startActivity(sharingIntent);
                break;
            default:
                Toast.makeText(HomeActivity.this, "wrong selection", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}




