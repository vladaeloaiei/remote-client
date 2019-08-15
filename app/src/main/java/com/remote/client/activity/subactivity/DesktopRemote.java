package com.remote.client.activity.subactivity;

import android.util.Log;
import android.widget.TextView;

import com.remote.client.custom.view.DesktopView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class used to handler the share screen on the client side
 */
public class DesktopRemote
{
    /* Tag for debug */
    private static final String TAG = "DesktopRemote";

    public static int fps = 0;

    private Timer       timer             = null;
    private TextView    fpsLabelTextView  = null;
    private DesktopView desktopRemoteView = null;

    public DesktopRemote(DesktopView desktopRemoteView, TextView fpsLabelTextView)
    {
        this.desktopRemoteView = desktopRemoteView;
        this.fpsLabelTextView = fpsLabelTextView;
    }

    /**
     * Starts the share screen
     */
    public void start()
    {
        Log.d(TAG, "start");

        if (! this.desktopRemoteView.isStarted())
        {
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    try
                    {
                        fpsLabelTextView.setText(String.valueOf(fps));
                        fps = 0;
                    }
                    catch (Exception ex)
                    {
                        Log.d(TAG, Log.getStackTraceString(ex));
                    }
                }
            }, 1000, 1000);
            this.desktopRemoteView.start();
        }
    }

    /**
     * Stops the share screen
     */
    public void stop()
    {
        Log.d(TAG, "stop");

        this.desktopRemoteView.stop();

        if (null != this.timer)
        {
            this.timer.cancel();
            this.timer = null;
        }

    }

    /**
     * Destroys the share screen handler and all of it's components
     */
    public void destroy()
    {
        Log.d(TAG, "destroy");

        this.stop();
        this.desktopRemoteView.destroy();
    }
}
