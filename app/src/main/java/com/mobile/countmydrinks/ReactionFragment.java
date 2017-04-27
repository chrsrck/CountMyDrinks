package com.mobile.countmydrinks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReactionFragment extends Fragment {

    private TouchListener mTouchListener;
    private int touchCount;
    private boolean gameStarted;

    private TextView timeCountText;
    private TextView promptText;
    private TextView baselineText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reaction, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        timeCountText = (TextView) view.findViewById(R.id.timeCountText);
        promptText = (TextView) view.findViewById(R.id.timeCountText);
        baselineText = (TextView) view.findViewById(R.id.baselineText);

        touchCount = 0;
        gameStarted = false;
        mTouchListener = new TouchListener(mainActivity);
        view.setOnTouchListener(mTouchListener);

        return view;
    }

    public void onTouchFired() {

        promptText.setText("Tap gesture detected!");
//        if (gameStarted && touchCount == 0) {
//
//        }
//        else if (!gameStarted && touchCount == 1) {
//
//        }
//        else if (gameStarted && touchCount == 2) {
//
//        }
    }

    private void startGame() {

    }

    private void endGame() {

    }



}
