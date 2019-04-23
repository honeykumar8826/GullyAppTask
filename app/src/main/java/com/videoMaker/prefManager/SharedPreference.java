package com.videoMaker.prefManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SharedPreference {
    private static final String TAG = "SharedPreference";
    private static SharedPreference instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("VideosName", Context.MODE_PRIVATE);
    }

    public static void initShared(Context context) {
        if (instance == null) {
            instance = new SharedPreference(context);
        }
    }

    public static SharedPreference getInstance() {
        return instance;
    }

    public void insertVideo(String videoPath) {
        editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>();
        List<String> existElementList = getAllVideoList();
        Log.i(TAG, "insertVideo:already video list " + existElementList);
        if (existElementList != null) {
            Log.i(TAG, "insertVideo: list " + existElementList.size());
            if (existElementList.size() >= 1) {
                set.add(videoPath);
                set.addAll(existElementList);
                editor.putStringSet("VideoPath", set);
                editor.apply();
            }
        } else {
            Log.i(TAG, " video first time in else part");
            if (videoPath != null) {
                set.add(videoPath);
                editor.putStringSet("VideoPath", set);
                editor.apply();
            }
        }
    }

    public List<String> getAllVideoList() {
        Set<String> set = sharedPreferences.getStringSet("VideoPath", null);
        if (set != null) {
            Log.i(TAG, "getAllVideoList: " + set.size());
            List<String> mainList = new ArrayList<>();
            mainList.addAll(set);
            return mainList;
        }
        return null;
    }
}