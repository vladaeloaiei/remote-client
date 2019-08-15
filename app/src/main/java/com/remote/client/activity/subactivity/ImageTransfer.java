package com.remote.client.activity.subactivity;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.remote.client.R;
import com.remote.client.utils.transfer.FileTransferHandler;

import java.util.List;

/**
 * Class which handle the image transfer of client side
 */
public class ImageTransfer

{
    /* Tag for debug */
    private static final String TAG               = "ImageTransfer";
    private static final String FILE_PROVIDER     = "android.support.v4.content" +
                                                    ".FileProvider";
    private static final int    NUMBER_OF_COLUMNS = 4;
    private static final int    MAX_PROGRESS      = 100 * 100;

    private FragmentActivity fragmentActivity    = null;
    private TextView         fileNameTextView    = null;
    private ProgressBar      transferProgressBar = null;

    public ImageTransfer(FragmentActivity activity, ProgressBar progressBar, TextView textView)
    {
        this.fragmentActivity = activity;
        this.transferProgressBar = progressBar;
        this.fileNameTextView = textView;
        this.transferProgressBar.setMax(MAX_PROGRESS);
    }

    /**
     * Select the images that will be send
     */
    public void selectImages()
    {
        Log.d(TAG, "selectImages");
        BSImagePicker multiPicker = null;

        multiPicker = new BSImagePicker.Builder(FILE_PROVIDER)
                              .isMultiSelect()
                              .setMultiSelectBarBgColor(android.R.color.background_dark)
                              .setMultiSelectTextColor(R.color.primary_text)
                              .setMultiSelectDoneTextColor(android.R.color.white)
                              .setSpanCount(NUMBER_OF_COLUMNS)
                              .build();

        multiPicker.show(this.fragmentActivity.getSupportFragmentManager(), "Image picker");
    }

    /**
     * Send the selected images
     * @param imagesPath Paths of the images
     */
    public void transferImages(List<Uri> imagesPath)
    {
        Log.d(TAG, "transferImages");

        FileTransferHandler transferHandler = new FileTransferHandler(
                imagesPath.toArray(new Uri[0]), (float)1 / imagesPath.size(),
                this::onSuccessImageTransfer, this::onFailImageTransfer);

        switchToTransferLayout();
        transferHandler.execute(this.transferProgressBar, this.fileNameTextView);
    }

    /**
     * Callback for a successful transfer
     */
    private void onSuccessImageTransfer()
    {
        Log.d(TAG, "onSuccessImageTransfer: finished.");
        this.fragmentActivity.runOnUiThread(() -> Toast.makeText(this.fragmentActivity,
                "Image transfer succeeded", Toast.LENGTH_SHORT).show());

        switchToPickLayout();
    }

    /**
     * Callback for a failed transfer
     */
    private void onFailImageTransfer()
    {
        Log.d(TAG, "onFailImageTransfer: finished.");
        this.fragmentActivity.runOnUiThread(() -> Toast.makeText(this.fragmentActivity,
                "Image transfer failed", Toast.LENGTH_SHORT).show());

        switchToPickLayout();
    }

    /**
     * Switch to pick layout
     */
    private void switchToPickLayout()
    {
        this.fragmentActivity.findViewById(R.id.transferImagesLayout).setVisibility(View.GONE);
        this.fragmentActivity.findViewById(R.id.pickImagesLayout).setVisibility(View.VISIBLE);
    }

    /**
     * Switch to transfer layout
     */
    private void switchToTransferLayout()
    {
        this.fragmentActivity.findViewById(R.id.pickImagesLayout).setVisibility(View.GONE);
        this.fragmentActivity.findViewById(R.id.transferImagesLayout).setVisibility(View.VISIBLE);
    }
}
