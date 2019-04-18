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
import com.gullyApp.modal.CommentModal;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context context;
    private List<CommentModal> commentModalList;

    public CommentAdapter(Context context, List<CommentModal> commentModalList) {
        this.context = context;
        this.commentModalList = commentModalList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_layout, viewGroup, false);
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));
        view.setLayoutParams(new RecyclerView.LayoutParams(height, RecyclerView.LayoutParams.WRAP_CONTENT));
        // view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        final CommentModal commentModal = commentModalList.get(i);
        final String imgUrl = commentModal.getImgUrl();
        commentViewHolder.userName.setText(commentModal.getUserName());
        commentViewHolder.name.setText(commentModal.getName());


        Glide.with(context)
                .asBitmap()
                .load(imgUrl)
                .placeholder(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .into(commentViewHolder.userProfile);
    }

    @Override
    public int getItemCount() {
        return commentModalList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfile;
        TextView userName,name;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.profile_img);
            userName = itemView.findViewById(R.id.tv_userName);
            name = itemView.findViewById(R.id.tv_name);
        }
    }
}
