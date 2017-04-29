package com.mobile.countmydrinks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

public class ReactionFragment extends Fragment {

    private static int MILLISECS_IN_SECOND = 1000;
    private static long SEED = 29;
    private TextView timeCountText;
    private TextView promptText;
    private TextView baselineText;

    private boolean gameStarted;
    private boolean countdownActivated;

    private TouchListener mTouchListener;
    private GametimeAsyncTask gametimeAsyncTask;
    private StringBuilder mStringBuilder;
    private Random mRandom;

    private long startTime;
    private long timeElapsed;
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
        mStringBuilder = new StringBuilder();
        mRandom = new Random(SEED);
        return view;
    }

    public void onTouchFired() {

        // end game
        if (gameStarted) {
            promptText.setText(R.string.play_again_prompt);
            gameStarted = false;
            this.getView().setBackgroundColor(Color.WHITE);
            gametimeAsyncTask.cancel(true);
        }
        // start game
        else {
            startTime = 0;
            timeElapsed = 0;
            waitTime = generateRandomWaitTime();
            timeCountText.setText("00:000");
            promptText.setText(R.string.get_ready_prompt);

            gameStarted = true;
            countdownActivated = false;
            gametimeAsyncTask = new GametimeAsyncTask();
            gametimeAsyncTask.execute();
        }

    }

    private void setTimeTextValues(long threadTime) {
        long timeElapsed = threadTime - startTime;
        long seconds = timeElapsed / MILLISECS_IN_SECOND;
        long milliseconds = timeElapsed - (seconds * MILLISECS_IN_SECOND);

        if (seconds < 10) {
            mStringBuilder.append("0");
        }

        mStringBuilder.append(Long.toString(seconds) + ":");

        if (milliseconds < 100) {
            mStringBuilder.append("0");
        }

        if (milliseconds < 10) {
            mStringBuilder.append("0");
        }

        mStringBuilder.append(Long.toString(milliseconds));
        timeCountText.setText(mStringBuilder.toString());
        mStringBuilder.setLength(0);
    }

    private void changeBackgroundGreen() {
        this.getView().setBackgroundColor(Color.GREEN);
    }

    private long generateRandomWaitTime() {
        int minimumWaitTime = 2000; // 2 seconds
        int maximumWaitTime = 5000; // 5 + 2 = 7 seconds
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

            while (gameStarted) {

                if (!countdownActivated) {
                    try {
                        Thread.sleep(waitTime);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                publishProgress();

                if (isCancelled()) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {
            super.onProgressUpdate();
            if (!countdownActivated) {
                startTime = System.currentTimeMillis();
                promptText.setText(R.string.go_prompt);
                changeBackgroundGreen();
                countdownActivated = true;
            }
            setTimeTextValues(System.currentTimeMillis());
        }
    }


}
