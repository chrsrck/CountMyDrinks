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

public class ReactionFragment extends Fragment {

    private static int MILLISECS_IN_SECOND = 1000;
    private static int MILLISECS_IN_DECISECS = 100;
    private static int MILLISECS_IN_CENTISECS = 10;

    private static int CENTISECONDS_IN_SECOND = 100;

    private TextView timeCountText;
    private TextView promptText;
    private TextView baselineText;

    private TouchListener mTouchListener;
    private boolean gameStarted;
    private int milliseconds;
    private GametimeAsyncTask gametimeAsyncTask;
    private StringBuilder mStringBuilder;

    private long startTime;
    private long timeElapsed;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reaction, container, false);
        final MainActivity mainActivity = (MainActivity) getActivity();
        timeCountText = (TextView) view.findViewById(R.id.timeCountText);
        promptText = (TextView) view.findViewById(R.id.promptText);
        baselineText = (TextView) view.findViewById(R.id.baselineText);

        gameStarted = false;
        mTouchListener = new TouchListener(mainActivity);
        view.setOnTouchListener(mTouchListener);
        mStringBuilder = new StringBuilder();
        return view;
    }

    public void onTouchFired() {

        // end game
        if (gameStarted) {
            promptText.setText(R.string.too_much_prompt);
            gameStarted = false;
            this.getView().setBackgroundColor(Color.WHITE);
            gametimeAsyncTask.cancel(true);
        }
        // start game
        else {
            startTime = 0;
            timeElapsed = 0;
            timeCountText.setText("00:000");
            promptText.setText(R.string.go_prompt);
            gameStarted = true;
            this.getView().setBackgroundColor(Color.GREEN);
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

//        if (centisecs < 1) {
//            mStringBuilder.append("0");
//        }
        mStringBuilder.append(Long.toString(milliseconds));
        timeCountText.setText(mStringBuilder.toString());
        mStringBuilder.setLength(0);
    }

    private void startGame() {

    }

    private void endGame() {

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
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            while (gameStarted) {
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
            setTimeTextValues(System.currentTimeMillis());
        }
    }


}
