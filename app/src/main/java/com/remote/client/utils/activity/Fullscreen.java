package com.remote.client.utils.activity;

import android.view.View;

/**
 * Class used to enter in the full screen mode
 */
public class Fullscreen
{
    /**
     * This function is used to enter in the full screen mode.
     * It will hide the navigation bar and title bar.
     * <p>Note: Call this function every in resume event.
     *
     * @param view The currently focused view
     */
    public static void hideNavigationBar(View view)
    {
        int hideOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
                          | View.SYSTEM_UI_FLAG_FULLSCREEN
                          | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (null != view)
        {
            view.setSystemUiVisibility(hideOptions);
        }
    }
}
