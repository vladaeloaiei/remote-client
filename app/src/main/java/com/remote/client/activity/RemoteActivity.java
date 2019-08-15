package com.remote.client.activity;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.remote.client.R;
import com.remote.client.activity.subactivity.DesktopRemote;
import com.remote.client.activity.subactivity.ImageTransfer;
import com.remote.client.activity.subactivity.VolumeControl;
import com.remote.client.remote.wrapper.AsyncRemoteControl;
import com.remote.client.remote.wrapper.Method;
import com.remote.client.statemachine.ApplicationCore;
import com.remote.client.statemachine.ApplicationState;
import com.remote.client.utils.activity.Fullscreen;

import java.io.File;
import java.util.List;

/**
 * The class for the remote activity
 */
public class RemoteActivity extends AppCompatActivity
        implements BSImagePicker.OnMultiImageSelectedListener,
                   BSImagePicker.ImageLoaderDelegate

{
    /* Tag for debug */
    private static final String TAG = "RemoteActivity";

    private Thread        heartbeatThread = null;
    private DesktopRemote desktopRemote   = null;
    private VolumeControl volumeControl   = null;
    private ImageTransfer imageTransfer   = null;


    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_remote);

        this.imageTransfer = new ImageTransfer(this, findViewById(R.id.progressBar),
                findViewById(R.id.transferringImageNameTextView));
        this.volumeControl = new VolumeControl();
        this.desktopRemote = new DesktopRemote(findViewById(R.id.desktopView),
                findViewById(R.id.fpsLabel));
        this.heartbeatThread = new Thread(this::heartBeat);
        this.heartbeatThread.start();
        this.updateApplicationState();
    }

    /**
     * Callback used to call hideNavigationBar every
     * time the application is resumed.
     */
    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume" + ApplicationCore.getInstance().getState());
        super.onResume();

        /* Set the fullscreen landscape theme */
        Fullscreen.hideNavigationBar(this.getWindow().getDecorView());
        /* Check the application actual state and set the correct activity */
        ApplicationCore.getInstance().checkState(this, ApplicationState.CONNECTED);
        /* Set the volume control to change the remote volume */
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        /* Restart fps timer and DesktopView pipeline if is needed */
        if (ApplicationState.REMOTE_DESKTOP == ApplicationCore.getInstance().getState())
        {
            this.desktopRemote.start();
        }
    }

    /**
     * Method called when the activity is stopped
     */
    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop");
        super.onStop();

        /* Stop the fps timer and the DesktopView pipeline if is needed */
        if (ApplicationState.REMOTE_DESKTOP == ApplicationCore.getInstance().getState())
        {
            this.desktopRemote.stop();
        }
    }

    /**
     * Method called when the activity is destroyed
     */
    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        /* Destroy the DesktopView pipeline */
        this.desktopRemote.destroy();

        /* Stop the heartBeat thread */
        if (this.heartbeatThread.isAlive())
        {
            this.heartbeatThread.interrupt();
        }
    }

    /**
     * Method called when the activity is not focused
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
        {
            Log.d(TAG, "Hide navigation bar");
            Fullscreen.hideNavigationBar(this.getWindow().getDecorView());
        }
    }

    /**
     * Method called when a smartphone key is pressed
     *
     * @param keyCode The code of the key
     * @param event   The event received
     * @return true
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d(TAG, "onKeyDown");

        switch (keyCode)
        {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (KeyEvent.ACTION_DOWN == event.getAction())
                {
                    this.volumeControl.volumeUp();
                }

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (KeyEvent.ACTION_DOWN == event.getAction())
                {
                    this.volumeControl.volumeDown();
                }

                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Method called when a image needs to be loaded
     *
     * @param imageFile The image
     * @param ivImage   The view
     */
    @Override
    public void loadImage(File imageFile, ImageView ivImage)
    {
        Glide.with(this).load(imageFile).into(ivImage);
    }

    /**
     * Method called when multiple images are selected
     *
     * @param uriList List of uri of the images
     * @param tag     unused
     */
    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag)
    {
        Log.d(TAG, "onMultiImageSelected: " + uriList.size() + " images selected");
        this.imageTransfer.transferImages(uriList);
    }

    /**
     * Method used to change the layout
     *
     * @param view The new layout
     */
    public void onSwitchLayout(View view)
    {
        ApplicationState nextState         = null;
        ApplicationState currentState      = ApplicationCore.getInstance().getState();
        ViewFlipper      mainRemoteLayout  = findViewById(R.id.mainRemoteLayout);
        int              currentChildIndex = mainRemoteLayout.getDisplayedChild();
        int              wantedChildIndex  = 0;

        Log.d(TAG, "onSwitchLayout: Button id = " + view.getId());

        switch (view.getId())
        {
            case R.id.touchpadButton:
                wantedChildIndex = mainRemoteLayout.indexOfChild(findViewById(R.id.touchpadLayout));
                nextState = ApplicationState.TOUCHPAD;
                break;
            case R.id.desktopButton:
                wantedChildIndex = mainRemoteLayout.indexOfChild(findViewById(R.id.desktopLayout));
                nextState = ApplicationState.REMOTE_DESKTOP;
                break;
            case R.id.keyboardButton:
                wantedChildIndex = mainRemoteLayout.indexOfChild(findViewById(R.id.keyboardLayout));
                nextState = ApplicationState.KEYBOARD;
                break;
            case R.id.imageTransferButton:
                wantedChildIndex =
                        mainRemoteLayout.indexOfChild(findViewById(R.id.imageTransferLayout));
                nextState = ApplicationState.IMAGE_TRANSFER;
                break;
            case R.id.functionalKeysButton:
                wantedChildIndex =
                        mainRemoteLayout.indexOfChild(findViewById(R.id.functionalKeysLayout));
                nextState = ApplicationState.FUNCTIONAL_KEYS;
                break;
            default:
                Log.d(TAG, "Unknown button: " + view.getId());
                break;
        }

        /* Check if is needed to change the layout and ask for permission */
        if ((currentChildIndex != wantedChildIndex) &&
            (ApplicationCore.getInstance().setState(nextState)))
        {
            mainRemoteLayout.setDisplayedChild(wantedChildIndex);

            if (ApplicationState.REMOTE_DESKTOP == nextState)
            {
                /* Start remote desktop */
                this.desktopRemote.start();
            }
            else if (ApplicationState.REMOTE_DESKTOP == currentState)
            {
                /* Stop remote desktop */
                this.desktopRemote.stop();
            }
        }
    }

    /**
     * Exists the remote activity
     *
     * @param view The calling view
     */
    public void onExitButton(View view)
    {
        Log.d(TAG, "onKeyDown");
        ApplicationCore.getInstance().disconnect(this, "Disconnected");
    }

    /**
     * Method used to open image picker
     *
     * @param view the calling view
     */
    public void onPickImage(View view)
    {
        Log.d(TAG, "onPickImage");
        this.imageTransfer.selectImages();
    }

    /**
     * Update the application state
     */
    private void updateApplicationState()
    {
        ViewFlipper mainRemoteLayout = findViewById(R.id.mainRemoteLayout);

        Log.d(TAG, "updateApplicationState");

        switch (mainRemoteLayout.getCurrentView().getId())
        {
            case R.id.touchpadLayout:
                ApplicationCore.getInstance().setState(ApplicationState.TOUCHPAD);
                break;
            case R.id.desktopLayout:
                ApplicationCore.getInstance().setState(ApplicationState.REMOTE_DESKTOP);
                break;
            case R.id.keyboardLayout:
                ApplicationCore.getInstance().setState(ApplicationState.KEYBOARD);
                break;
            case R.id.imageTransferLayout:
                ApplicationCore.getInstance().setState(ApplicationState.IMAGE_TRANSFER);
                break;
            case R.id.functionalKeysLayout:
                ApplicationCore.getInstance().setState(ApplicationState.FUNCTIONAL_KEYS);
                break;
            default:
                Log.d(TAG, "Unknown view: " + mainRemoteLayout.getCurrentView().getId());
        }
    }

    /**
     * This method is used to check the connection with the server.
     * If the connection is down, the activity will be closed.
     */
    private void heartBeat()
    {
        Log.d(TAG, "heartBeat: started");
        int token   = ApplicationCore.getInstance().getToken();
        int counter = 0;

        try
        {
            while (null != ApplicationCore.getInstance().getControl())
            {
                if (! Boolean.TRUE.equals(ApplicationCore.getInstance().getControl().ping(token)))
                {
                    if (++ counter > 10)
                    {
                        Log.d(TAG, "heartBeat: Connection lost");
                        Thread.sleep(200);
                        break;
                    }
                }
                else
                {
                    counter = 0;
                    Thread.sleep(1000);
                }
            }
        }
        catch (InterruptedException ex)
        {
            /* This occurs only when the running thread was interrupted
             * due to the destruction of this activity. Return so no popup will be shown. */
            Log.d(TAG, "heartBeat: interrupted");
            return;
        }
        catch (Exception ex)
        {
            Log.d(TAG, Log.getStackTraceString(ex));
        }

        ApplicationCore.getInstance().disconnect(this, "Connection lost");
        Log.d(TAG, "heartBeat: finished");
    }

    /**
     * Method called when a functional key is pressed
     *
     * @param view The calling view
     */
    public void onFunctionalKey(View view)
    {
        Log.d(TAG, "onFunctionalKey: Button id = " + view.getId());

        switch (view.getId())
        {
            case R.id.shutdownRemoteButton:
                new AsyncRemoteControl().execute(Method.SHUTDOWN);
                break;
            case R.id.restartRemoteButton:
                new AsyncRemoteControl().execute(Method.RESTART);
                break;
            default:
                Log.d(TAG, "Unknown button: " + view.getId());
        }
    }
}
