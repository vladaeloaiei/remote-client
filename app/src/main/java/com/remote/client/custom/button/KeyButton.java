package com.remote.client.custom.button;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.remote.client.R;
import com.remote.client.remote.wrapper.AsyncRemoteControl;
import com.remote.client.remote.wrapper.Method;
import com.remote.client.utils.keyboard.Keyboard;

/**
 * Class used to extend a button to simulate a keyboard key
 */
public class KeyButton extends AppCompatButton
{
    /* Tag for debug */
    private static final String TAG = "KeyButton";

    public KeyButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setBackgroundResource(R.color.keyColor);
    }

    /**
     * Method called when the Button is pressed
     *
     * @param event The source of the event
     * @return true if the event was consumed, or false otherwise
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Log.d(TAG, "onTouchEvent");
        super.onTouchEvent(event);

        if (MotionEvent.ACTION_DOWN == event.getAction())
        {
            this.setBackgroundResource(R.color.pressedKeyColor);

            new AsyncRemoteControl().execute(Method.KEY_PRESS,
                    Keyboard.getJavaKeyValue(this.getId()));

            return true;
        }
        else if (MotionEvent.ACTION_UP == event.getAction())
        {
            this.setBackgroundResource(R.color.keyColor);

            new AsyncRemoteControl().execute(Method.KEY_RELEASE,
                    Keyboard.getJavaKeyValue(this.getId()));

            performClick();
            return true;
        }

        return false;
    }

    /**
     * Perform click method
     *
     * @return true
     */
    @Override
    public boolean performClick()
    {
        Log.d(TAG, "performClick");
        super.performClick();
        return true;
    }
}