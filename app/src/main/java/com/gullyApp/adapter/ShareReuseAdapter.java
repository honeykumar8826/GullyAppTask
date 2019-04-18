package com.gullyApp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gullyApp.R;
import com.gullyApp.modal.ShareReuseModal;

import java.util.List;

public class ShareReuseAdapter extends RecyclerView.Adapter<ShareReuseAdapter.ShareReuseViewHolder> {
    private Context context;
    private List<ShareReuseModal> shareReuseModalList;

    public ShareReuseAdapter(Context context, List<ShareReuseModal> shareReuseModalList) {
        this.context = context;
        this.shareReuseModalList = shareReuseModalList;
    }

    @NonNull
    @Override
    public ShareReuseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_reuse_list_layout, viewGroup, false);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        view.setLayoutParams(new RecyclerView.LayoutParams(height, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ShareReuseAdapter.ShareReuseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareReuseViewHolder shareReuseViewHolder, int i) {
        final ShareReuseModal shareReuseModal = shareReuseModalList.get(i);
        final String imgUrl = shareReuseModal.getImgUrl();
        shareReuseViewHolder.userName.setText(shareReuseModal.getUserName());
        shareReuseViewHolder.name.setText(shareReuseModal.getName());


        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(shareReuseViewHolder.userProfile);
    }

    @Override
    public int getItemCount() {
        return shareReuseModalList.size();
    }

    class ShareReuseViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfile;
        TextView userName, name;

        public ShareReuseViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.profile_img);
            userName = itemView.findViewById(R.id.tv_userName);
            name = itemView.findViewById(R.id.tv_name);
        }
    }
}

