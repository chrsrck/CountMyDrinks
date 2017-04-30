package com.mobile.countmydrinks;

import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by chrsrck on 4/13/17.
 */

public class TouchListener implements View.OnTouchListener {
    private static final String TAG = "touclistner";

    MainActivity mainActivity;
    GestureDetectorCompat gestureDetectorCompat;
    ReactionFragment mReactionFragment;

    public TouchListener(MainActivity mact) {
        mainActivity = mact;
        gestureDetectorCompat = new GestureDetectorCompat(this.mainActivity, new MyGestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        Log.d("oontouch", "On touch detected touch listener");
        gestureDetectorCompat.onTouchEvent(motionEvent);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mainActivity.onTouchFired();
            return super.onSingleTapUp(e);
        }
    }
}
