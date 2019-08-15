package com.remote.client.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.remote.client.activity.subactivity.DesktopRemote;
import com.remote.client.remote.wrapper.AsyncRemoteControl;
import com.remote.client.remote.wrapper.Method;
import com.remote.client.statemachine.ApplicationCore;
import com.remote.client.utils.mouse.Mouse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class used to extend an image view on which the image is shown
 */
public class DesktopView extends AppCompatImageView implements GestureDetector.OnGestureListener
{
    /* Tag for debug */
    private static final String TAG                 = "DesktopView";
    private static final int    NUMBER_OF_THREADS   = 2; /* retrieve image / process image */
    private static final int    TERMINATION_TIMEOUT = 500;

    private final ReentrantLock imageBytesLock      = new ReentrantLock();
    private final Condition     imageBytesAvailable = this.imageBytesLock.newCondition();

    private byte[]                  imageBytes       = null;
    private Paint                   paint            = null;
    private Size                    imageSize        = null;
    private Future                  processImageTask = null;
    private Matrix                  matrix           = null;
    private AtomicBoolean           isRunning        = null;
    private ExecutorService         executor         = null;
    private AtomicReference<Bitmap> screenImage      = null;
    private GestureDetectorCompat   gestureDetector  = null;

    public DesktopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.screenImage = new AtomicReference<>(null);
        this.matrix = new Matrix();
        this.paint = new Paint();
        this.paint.setFilterBitmap(true);
        this.paint.setAntiAlias(true);
        this.isRunning = new AtomicBoolean(false);
        this.executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        this.gestureDetector = new GestureDetectorCompat(context, this);

        this.setOnTouchListener((v, event) ->
        {
            v.performClick();
            return this.gestureDetector.onTouchEvent(event);
        });

        Log.d(TAG, "Constructor");
    }

    /**
     * Checks if the image handler is started
     *
     * @return true if the image handler is started, or false otherwise
     */
    public boolean isStarted()
    {
        return this.isRunning.get();
    }

    /**
     * Starts the image handler
     */
    public void start()
    {
        if (! this.isRunning.get())
        {
            this.isRunning.set(true);
            this.executor.submit(this::retrieveImage);
            this.processImageTask = this.executor.submit(this::processImage);
            Log.d(TAG, ": started");
        }
    }

    /**
     * Stops the image handler
     */
    public void stop()
    {
        if (this.isRunning.get())
        {
            this.isRunning.set(false);

            if (null != this.processImageTask)
            {
                this.processImageTask.cancel(true);
            }
        }

        Log.d(TAG, ": stopped");
    }

    /**
     * Destroy the image handler
     */
    public void destroy()
    {
        this.isRunning.set(false);
        this.executor.shutdown();

        try
        {
            /* Wait a while for existing tasks to terminate */
            if (! this.executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS))
            {
                this.executor.shutdownNow(); /* Cancel currently executing tasks */
            }
        }
        catch (InterruptedException ex)
        {
            /* (Re-)Cancel if current thread also interrupted */
            this.executor.shutdownNow();
            /* Preserve interrupt status */
            Thread.currentThread().interrupt();
        }

        Log.d(TAG, "destroyed");
    }

    /**
     * Method called to update the image view
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        Bitmap newScreenImage = this.screenImage.get();

        if (null != newScreenImage)
        {
            canvas.drawBitmap(newScreenImage, matrix, this.paint);
            ++ DesktopRemote.fps;
        }
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
    public boolean onDown(MotionEvent e)
    {
        Log.d(TAG, "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {
        Log.d(TAG, "onShowPress");
    }

    /**
     * A method called when a tap up occurs
     *
     * @param e The generated event
     * @return true
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        Log.d(TAG, "onSingleTapUp");

        float x        = e.getX();
        float y        = e.getY();
        float oxOffset = (float)(getMeasuredWidth() - this.imageSize.getWidth()) / 2;
        float oyOffset = (float)(getMeasuredHeight() - this.imageSize.getHeight()) / 2;

        /* Check if the tap was int the wanted area */
        if ((x > oxOffset) && (x < getMeasuredWidth() - oxOffset) &&
            (y > oyOffset) && (y < getMeasuredHeight() - oyOffset))
        {
            /* Get the position on the image screen */
            float mappedX = x - oxOffset;
            float mappedY = y - oyOffset;
            /* Get the absolute position to the image screen */
            float absoluteX = mappedX / this.imageSize.getWidth();
            float absoluteY = mappedY / this.imageSize.getHeight();

            /* Move mouse */
            new AsyncRemoteControl().execute(Method.MOUSE_MOVE, absoluteX, absoluteY, true);
            /* Perform left click */
            new AsyncRemoteControl().execute(Method.MOUSE_PRESS, Mouse.BUTTON1_MASK);
            new AsyncRemoteControl().execute(Method.MOUSE_RELEASE, Mouse.BUTTON1_MASK);
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        Log.d(TAG, "onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {
        Log.d(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        Log.d(TAG, "onFling");
        return true;
    }

    /**
     * Set the retrieved image bytes
     *
     * @param imageBytes The image bytes
     */
    private void setImageBytes(byte[] imageBytes)
    {
        /* lock access to imageBytes */
        this.imageBytesLock.lock();
        /* save image bytes */
        this.imageBytes = imageBytes;
        /* signal process thread */
        this.imageBytesAvailable.signal();
        /* unlock access to imageBytes */
        this.imageBytesLock.unlock();
    }

    /**
     * Get the last image bytes retrieved
     *
     * @return the image bytes
     * @throws InterruptedException If the calling thread is interruped
     */
    private byte[] getImageBytes()
            throws InterruptedException
    {
        byte[] imageBytes = null;

        try
        {
            /* lock access to screenShot */
            this.imageBytesLock.lock();

            /* wait until an image is available */
            while (null == this.imageBytes)
            {
                this.imageBytesAvailable.await();
            }

            imageBytes = this.imageBytes;
            this.imageBytes = null;
        }
        finally
        {
            /* unlock access to screenShot */
            this.imageBytesLock.unlock();
        }

        return imageBytes;
    }

    /**
     * Task for retrieving the image from the server
     */
    private void retrieveImage()
    {
        byte[] receivedImageBytes = null;
        int    token              = ApplicationCore.getInstance().getToken();

        Log.d(TAG, "retrieveImage: started");

        while (this.isRunning.get())
        {
            try
            {
                if (null == ApplicationCore.getInstance().getControl())
                {
                    Log.d(TAG, "The client is disconnected from server");
                    this.isRunning.set(false);
                }

                receivedImageBytes =
                        ApplicationCore.getInstance().getControl().getScreenShot(token);
                this.setImageBytes(receivedImageBytes);

            }
            catch (Exception ex)
            {
                Log.d(TAG, Log.getStackTraceString(ex));
            }
        }

        Log.d(TAG, "retrieveImage: finished");
    }

    /**
     * Task for processing the image
     */
    private void processImage()
    {
        float  scale        = 1;
        int    screenWidth  = getMeasuredWidth();
        int    screenHeight = getMeasuredHeight();
        byte[] imageBytes   = null;
        Bitmap screenImage  = null;

        Log.d(TAG, "processImage: started");

        try
        {
            while (this.isRunning.get())
            {
                imageBytes = this.getImageBytes();
                screenImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                scale = Math.min((float)screenWidth / screenImage.getWidth(),
                        (float)screenHeight / screenImage.getHeight());

                /* Set the size of image that will be on the screen */
                this.imageSize = new Size((int)(scale * screenImage.getWidth()),
                        (int)(scale * screenImage.getHeight()));

                this.matrix.setRectToRect(
                        new RectF(0, 0, screenImage.getWidth(), screenImage.getHeight()),
                        new RectF(0, 0, screenWidth, screenHeight),
                        Matrix.ScaleToFit.CENTER);

                /* Set the new screen image */
                this.screenImage.set(screenImage);
                /* Call invalidate to draw the new image */
                super.invalidate();
            }
        }
        catch (InterruptedException ex)
        {
            /* Preserve interrupt status */
            Thread.currentThread().interrupt();
        }

        Log.d(TAG, "processImage: finished");
    }
}