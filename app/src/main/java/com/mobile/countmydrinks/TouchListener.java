package com.mobile.countmydrinks;

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

    public TouchListener(MainActivity mact) {
        this.mainActivity = mact;
        gestureDetectorCompat = new GestureDetectorCompat(this.mainActivity, new MyGestureListener());

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gestureDetectorCompat.onTouchEvent(motionEvent);
        int maskedAction = motionEvent.getActionMasked();
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN: // when the user first touches the screen
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
//
                break;
            default:
                return false;
        }
//        view.invalidate();

        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
//            Log.d(TAG, "Long press called");
//            mainActivity.onLongPress(e.getX(), e.getY());
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            Log.d(TAG, "Double Tap called");
//            mainActivity.onDoubleTap(e.getX(), e.getY());
            return super.onDoubleTap(e);
        }
    }
}
