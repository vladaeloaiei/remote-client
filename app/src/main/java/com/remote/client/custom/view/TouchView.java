package com.remote.client.custom.view;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.remote.client.R;
import com.remote.client.remote.wrapper.AsyncRemoteControl;
import com.remote.client.remote.wrapper.Method;
import com.remote.client.utils.mouse.Mouse;

/**
 * Class used to simulate touch area of a touchpad
 */
public class TouchView extends AppCompatImageView
        implements GestureDetector.OnGestureListener
{
    /* Tag for debug */
    private static final String TAG = "TouchView";

    private static final int SCROLL_CALIBRATION = 4;

    /* This is used to ignore some scroll events to make mouse scroll smoother */
    private float                 scrollChecker   = TouchView.SCROLL_CALIBRATION;
    private GestureDetectorCompat gestureDetector = null;

    public TouchView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.setBackgroundResource(R.color.touchpadColor);
        this.gestureDetector = new GestureDetectorCompat(context, this);

        this.setOnTouchListener((v, event) ->
        {
            v.performClick();
            return this.gestureDetector.onTouchEvent(event);
        });
    }

    /**
     * Perform click method
     *
     * @return true
     */
    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent event)
    {
        Log.d(TAG, "onDown");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY)
    {
        Log.d(TAG, "onFling");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event)
    {
        Log.d(TAG, "onLongPress");
    }

    /**
     * Method called when the user scrolled
     * @param event1 The first event called
     * @param event2 The last event called
     * @param distanceX The distance on OX
     * @param distanceY The distance on OY
     * @return true
     */
    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2,
                            float distanceX, float distanceY)
    {
        if (2 == event2.getPointerCount())
        {
            if (TouchView.SCROLL_CALIBRATION <= this.scrollChecker)
            {
                this.scrollChecker = 0;
                /* This is mouse scroll upd/down */

                if ((- distanceY) > 0)
                {
                    /* Scroll up */
                    new AsyncRemoteControl().execute(Method.MOUSE_SCROLL, (short)1);
                }
                else
                {
                    /* Scroll down */
                    new AsyncRemoteControl().execute(Method.MOUSE_SCROLL, (short)- 1);
                }
            }
            else
            {
                /* Ignore this event, increment scroll checker
                 * and maybe next event will be luckier */
                this.scrollChecker += Math.sqrt(Math.abs(distanceY)) - 1;
            }
        }
        else
        {
            /* Next mouse scroll event will be valid */
            this.scrollChecker = TouchView.SCROLL_CALIBRATION;
            /* Normal mouse move. Mark as relative position. Also negate the value */
            new AsyncRemoteControl().execute(Method.MOUSE_MOVE, - distanceX, - distanceY, false);
        }

        return true;

    }

    @Override
    public void onShowPress(MotionEvent event)
    {
        Log.d(TAG, "onShowPress");
    }

    /**
     * Method called when the user single taps the view
     * @param event The generated event
     * @return true
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event)
    {
        Log.d(TAG, "onSingleTapUp");
        new AsyncRemoteControl().execute(Method.MOUSE_PRESS, Mouse.BUTTON1_MASK);
        new AsyncRemoteControl().execute(Method.MOUSE_RELEASE, Mouse.BUTTON1_MASK);
        return true;
    }
}