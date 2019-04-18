package com.gullyApp.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gullyApp.R;
import com.gullyApp.adapter.CommentAdapter;
import com.gullyApp.animation.OnSwipeTouchListener;
import com.gullyApp.modal.CommentModal;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends DialogFragment {
    private RecyclerView recyclerView;
    private LinearLayout swipeLinearLayout;
    private Animation animShow, animHide;
    private View view;
    private ImageView downList;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        getAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
    }
/*    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }*/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //initialize the list
        List<CommentModal> commentModalList = new ArrayList<>();
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_comment, container, false);
        // initialize the id
        setItId();
        swipeLinearLayout.setOnTouchListener(new OnSwipeTouchListener(getContext())
        {
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                recyclerView.startAnimation(animHide);
                getDialog().dismiss();
//                swipeLinearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
               /* recyclerView.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(animShow);*/
            }
        });
        downList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        for (int i = 0; i < 50; i++) {
            CommentModal commentModal = new CommentModal("https://tineye.com/images/widgets/mona.jpg", "honey kumar", "harry");
            commentModalList.add(commentModal);
        }
        CommentAdapter commentAdapter = new CommentAdapter(this.getActivity(), commentModalList);
        recyclerView.setAdapter(commentAdapter);

        commentAdapter.notifyDataSetChanged();
        return view;
    }

    private void setItId() {
        recyclerView = view.findViewById(R.id.recycles_comment);
        swipeLinearLayout = view.findViewById(R.id.linear_layout_swipe);
        downList = view.findViewById(R.id.close_comment);
    }

    private void getAnimation() {
        animShow = AnimationUtils.loadAnimation(getActivity(),R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(getActivity(),R.anim.view_hide);
    }
}
