package es.molestudio.temazos.swipelistview;

import android.view.MotionEvent;
import android.view.View;


public class SwipeDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        None // Ninguna accion
    }

    private static final int MIN_DISTANCE = 100;
    private float downX, upX;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                mSwipeDetected = Action.None;
                return false; // permite que otros eventos como "click" se procesen
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();

                float deltaX = downX - upX;

                // Detección horizontal de movimiento
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }

                return true;
            }
        }
        return false;
    }
}
