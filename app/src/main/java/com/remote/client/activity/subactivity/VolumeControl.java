package com.remote.client.activity.subactivity;

import android.util.Log;

import com.remote.client.remote.wrapper.AsyncRemoteControl;
import com.remote.client.remote.wrapper.Method;

/**
 * Class used to handle the audio volume control
 */
public class VolumeControl
{
    /* Tag for debug */
    private static final String TAG = "VolumeControl";

    /**
     * Increment the volume with 10%
     */
    public void volumeUp()
    {
        Log.d(TAG, "volumeUp");
        new AsyncRemoteControl().execute(Method.CHANGE_VOLUME, (float)0.1);
    }

    /**
     * Decrement the volume with 10%
     */
    public void volumeDown()
    {
        Log.d(TAG, "volumeDown");
        new AsyncRemoteControl().execute(Method.CHANGE_VOLUME, (float)- 0.1);
    }
}
