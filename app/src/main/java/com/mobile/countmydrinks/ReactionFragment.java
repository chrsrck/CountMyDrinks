package com.mobile.countmydrinks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class ReactionFragment extends Fragment {
    private static long SEED = 29;
    private TextView timeCountText;
    private TextView promptText;
    private TextView baselineText;

    private boolean gameStarted;
    private boolean countdownActivated;

    private TouchListener mTouchListener;
    private GametimeAsyncTask gametimeAsyncTask;
    private Random mRandom;

    private long startTime;
    private long waitTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reaction, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        timeCountText = (TextView) view.findViewById(R.id.timeCountText);
        promptText = (TextView) view.findViewById(R.id.promptText);
        baselineText = (TextView) view.findViewById(R.id.baselineText);

        gameStarted = false;
        countdownActivated = false;

        mTouchListener = new TouchListener(mainActivity);
        view.setOnTouchListener(mTouchListener);
        mRandom = new Random(SEED);
        gametimeAsyncTask = new GametimeAsyncTask();
        return view;
    }

    public void onTouchFired() {
        // end game
        if (gameStarted && countdownActivated) {
            promptText.setText(R.string.play_again_prompt);
            gameStarted = false;
            this.getView().setBackgroundColor(Color.WHITE);
            gametimeAsyncTask.cancel(true);
            setTimeTextValues(System.currentTimeMillis());
            countdownActivated = false;
        }
        else if (gameStarted) { // tapped too soon
            gametimeAsyncTask.cancel(true);
            gameStarted = false;
            countdownActivated = false;
            promptText.setText(R.string.too_soon_prompt);
        }
        // start game
        else if (!gameStarted){
            this.getView().setBackgroundColor(Color.WHITE);
            startTime = 0;
            waitTime = generateRandomWaitTime();
            timeCountText.setText("0 ms");
            promptText.setText(R.string.get_ready_prompt);
            gameStarted = true;
            countdownActivated = false;
            gametimeAsyncTask = new GametimeAsyncTask();
            gametimeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void setTimeTextValues(long threadTime) {
        long timeElapsed = threadTime - startTime;
        timeCountText.setText("" + timeElapsed + " ms");
    }

    private void changeBackgroundGreen() {
        this.getView().setBackgroundColor(Color.parseColor("#00E676"));
    }

    private long generateRandomWaitTime() {
        int minimumWaitTime = 2000; // 2 seconds
        int maximumWaitTime = 3000; // 3 + 2 = 5 seconds
        return (long) (mRandom.nextInt(maximumWaitTime) + minimumWaitTime) ;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!gametimeAsyncTask.isCancelled()) {
            gametimeAsyncTask.cancel(true);
        }
    }

    private class GametimeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!countdownActivated) {
                try {
                    Thread.sleep(waitTime);
                    publishProgress();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            super.onProgressUpdate();
            startTime = System.currentTimeMillis();
            promptText.setText(R.string.go_prompt);
            changeBackgroundGreen();
            countdownActivated = true;
        }
    }
}
