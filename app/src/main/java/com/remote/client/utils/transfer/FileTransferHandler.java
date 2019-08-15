package com.remote.client.utils.transfer;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.remote.client.statemachine.ApplicationCore;
import com.remote.client.statemachine.ApplicationState;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * Class used to handle the file transfer in an async way
 */
public class FileTransferHandler extends AsyncTask<Object, Object, Runnable>
{
    /* Tag for debug */
    private static final String TAG            = "FileTransferHandler";
    private static final int    MAX_CHUNK_SIZE = 100 * 1024; /* 100KB */

    private float    progressPercent = 0;
    private Uri[]    uriList         = null;
    private Runnable notifySuccess   = null;
    private Runnable notifyFailure   = null;

    public FileTransferHandler(Uri[] uriList, float percent, Runnable success, Runnable failure)
    {
        this.uriList = uriList;
        this.progressPercent = percent;
        this.notifyFailure = failure;
        this.notifySuccess = success;
    }

    /**
     * Send the files to the server
     *
     * @param objects Info
     * @return runnable which will be called after the transfer
     */
    @Override
    protected Runnable doInBackground(Object... objects)
    {
        try
        {
            this.transfer((ProgressBar)objects[0], (TextView)objects[1]);
            resetProgress((ProgressBar)objects[0], (TextView)objects[1]);
            return notifySuccess;
        }
        catch (Exception ex)
        {
            Log.d(TAG, Log.getStackTraceString(ex));
            resetProgress((ProgressBar)objects[0], (TextView)objects[1]);
            return notifyFailure;
        }
    }

    /**
     * Method which will be called for every update
     *
     * @param objects Info
     */
    @Override
    protected void onProgressUpdate(Object... objects)
    {
        this.updateProgress(((ProgressBar)objects[0]), (float)objects[1],
                (TextView)objects[2], (String)objects[3]);
    }

    /**
     * Method which will be called after the transfer
     *
     * @param notify The notify which will be sent
     */
    @Override
    protected void onPostExecute(Runnable notify)
    {
        notify.run();
    }

    /**
     * Transfer the files
     *
     * @param progressBar       The progress bar that will be updated
     * @param imageNameTextView The text view that will be updated
     * @throws Exception If any error occurs
     */
    private void transfer(ProgressBar progressBar, TextView imageNameTextView)
            throws Exception
    {
        File file = null;

        for (Uri uri : this.uriList)
        {
            file = new File(uri.getPath());
            transferFile(progressBar, imageNameTextView, file);
        }
    }

    /**
     * Transfer a file
     *
     * @param progressBar       The progress bar that will be updated
     * @param imageNameTextView The text view that will be updated
     * @param file              The file that will be sent
     * @throws Exception If eny error occurs
     */
    private void transferFile(ProgressBar progressBar, TextView imageNameTextView, File file)
            throws Exception
    {
        FileInputStream inputStream = null;

        try
        {
            int    token         = ApplicationCore.getInstance().getToken();
            int    bytesRead     = 0;
            float  deltaProgress = 0;
            byte[] chunk         = new byte[MAX_CHUNK_SIZE];

            Log.d(TAG, "transferFile: " + file.getName());
            inputStream = new FileInputStream(file);
            deltaProgress = ((float)MAX_CHUNK_SIZE / inputStream.available());
            deltaProgress *= progressBar.getMax();

            while (- 1 != (bytesRead = inputStream.read(chunk)))
            {
                if (ApplicationCore.getInstance().getState() == ApplicationState.DISCONNECTED)
                {
                    throw new Exception("The client is disconnected from server");
                }

                if (! ApplicationCore.getInstance().getControl().sendFile(token, file.getName(),
                        Arrays.copyOf(chunk, bytesRead)))
                {
                    throw new Exception("Failed to send file chunk to server: " + file.getName());
                }

                onProgressUpdate(progressBar, deltaProgress, imageNameTextView, file.getName());
            }

            ApplicationCore.getInstance().getControl().sendFile(token, file.getName(), null);
            inputStream.close();

            Log.d(TAG, "File " + file.getName() + " has been successfully sent.");
        }
        finally
        {
            if (null != inputStream)
            {
                inputStream.close();
            }
        }
    }

    /**
     * Method used to update the progress
     *
     * @param progressBar       The progress bar
     * @param deltaProgress     The delta progress
     * @param imageNameTextView The view for the image name
     * @param imageName         The image name
     */
    private void updateProgress(ProgressBar progressBar, float deltaProgress,
                                TextView imageNameTextView, String imageName)
    {
        float globalDeltaProgress = deltaProgress * this.progressPercent;

        Log.d(TAG, "updateProgressBar: add " + globalDeltaProgress + " to current progress.");
        progressBar.incrementProgressBy((int)globalDeltaProgress);
        /* Make a request to set the new value of text view.
         * The thread with created this view will be the one that will actually update the text */
        imageNameTextView.post(() -> imageNameTextView.setText(imageName));
    }

    /**
     * Method used to reset the progress
     *
     * @param progressBar       The progress bar
     * @param imageNameTextView The view for the image name
     */
    private void resetProgress(ProgressBar progressBar, TextView imageNameTextView)
    {
        Log.d(TAG, "resetProgress");
        /* Make a request to set the new value of text view.
         * The thread with created this view will be the one that will actually update the text */
        progressBar.post(() -> progressBar.setProgress(0));
        imageNameTextView.post(() -> imageNameTextView.setText(""));
    }
}
