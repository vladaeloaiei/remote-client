package com.remote.client.remote.wrapper;

import android.os.AsyncTask;
import android.util.Log;

import com.remote.client.statemachine.ApplicationCore;

/**
 * Class used to make async calls to the server
 */
public class AsyncRemoteControl extends AsyncTask<Object, Object, Void>
{
    /* Tag for debug */
    private static final String TAG = "AsyncRemoteControl";

    /**
     * Method used to make the call to the server
     * @param params Parameters for the call
     * @return null
     */
    @Override
    protected Void doInBackground(Object... params)
    {
        if (0 == params.length)
        {
            return null;
        }

        try
        {
            int token = ApplicationCore.getInstance().getToken();

            switch ((Method)params[0])
            {
                case CHANGE_VOLUME:
                    if ((ApplicationCore.getInstance().connected()) && (2 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().changeVolume(token,
                                (Float)params[1]);
                    }

                    break;
                case KEY_PRESS:
                    if ((ApplicationCore.getInstance().connected()) && (2 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().keyPress(token,
                                (Integer)params[1]);
                    }

                    break;
                case KEY_RELEASE:
                    if ((ApplicationCore.getInstance().connected()) && (2 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().keyRelease(token,
                                (Integer)params[1]);
                    }

                    break;
                case MOUSE_PRESS:
                    if ((ApplicationCore.getInstance().connected()) && (2 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().mousePress(token,
                                (Integer)params[1]);
                    }

                    break;
                case MOUSE_RELEASE:
                    if ((ApplicationCore.getInstance().connected()) && (2 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().mouseRelease(token,
                                (Integer)params[1]);
                    }

                    break;
                case MOUSE_MOVE:
                    if ((ApplicationCore.getInstance().connected()) && (4 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().mouseMove(
                                token, (Float)params[1], (Float)params[2], (Boolean)params[3]);
                    }

                    break;
                case MOUSE_SCROLL:
                    if ((ApplicationCore.getInstance().connected()) && (2 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().mouseScroll(token,
                                (Short)params[1]);
                    }

                    break;
                case SHUTDOWN:
                    if ((ApplicationCore.getInstance().connected()) && (1 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().shutdown(token);
                    }

                    break;
                case RESTART:
                    if ((ApplicationCore.getInstance().connected()) && (1 == params.length))
                    {
                        ApplicationCore.getInstance().getControl().restart(token);
                    }

                    break;
                default:
                    Log.d(TAG, "Method not supported: " + params[0]);
                    break;
            }
        }
        catch (Exception ex)
        {
            Log.d(TAG, Log.getStackTraceString(ex));
        }

        return null;
    }
}
