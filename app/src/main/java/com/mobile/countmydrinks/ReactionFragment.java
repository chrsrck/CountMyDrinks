package com.mobile.countmydrinks;

import android.graphics.Color;
import android.os.AsyncTask;
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
        promptText = (TextView) view.findViewById(R.id.promptText);
        baselineText = (TextView) view.findViewById(R.id.baselineText);

        touchCount = 0;
        gameStarted = false;
        mTouchListener = new TouchListener(mainActivity);
        view.setOnTouchListener(mTouchListener);

        return view;
    }

    public void onTouchFired() {

        // end game game
        if (gameStarted) {
            promptText.setText(R.string.too_much_prompt);
            gameStarted = false;
            this.getView().setBackgroundColor(Color.WHITE);
        }
        // start game game
        else {
            promptText.setText(R.string.go_prompt);
            gameStarted = true;
            this.getView().setBackgroundColor(Color.GREEN);
        }

    }

    private void startGame() {

    }

    private void endGame() {

    }

    private class gametimeAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


}
