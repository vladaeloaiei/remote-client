package com.remote.client.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.remote.client.R;
import com.remote.client.statemachine.ApplicationCore;
import com.remote.client.statemachine.ApplicationState;
import com.remote.client.utils.activity.Fullscreen;

import byteremote.common.socket.ProtocolType;

/**
 * This class is the activity for the connection
 */
public class ConnectionActivity extends AppCompatActivity
{
    /* Tag for debug */
    private static final String TAG         = "ConnectionActivity";
    private static final String PREFERENCES = "REMOTE_PREFERENCES";

    private SharedPreferences sharedPreferences = null;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Log.d(TAG, "onCreate");
        sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        String ip = sharedPreferences.getString("user_ip", null);

        if (ip != null)
        {
            ((TextView)findViewById(R.id.ipValue)).setText(ip);
        }

        String password = sharedPreferences.getString("user_password", null);

        if (password != null)
        {
            ((TextView)findViewById(R.id.passwordValue)).setText(password);
        }
    }

    /**
     * Callback used to call hideNavigationBar every
     * time the application is resumed.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        /* Set the fullscreen landscape theme */
        Fullscreen.hideNavigationBar(this.getWindow().getDecorView());

        /* Check the application actual state and set the correct activity */
        ApplicationCore.getInstance().checkState(this, ApplicationState.DISCONNECTED);

        Log.d(TAG, "onResume" + ApplicationCore.getInstance().getState());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        Fullscreen.hideNavigationBar(this.getWindow().getDecorView());
    }

    /**
     * Event used to hide the keyboard
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event,
     * false if you haven't. The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        View view = this.getCurrentFocus();

        if (null != view)
        {
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            if (null != inputMethodManager)
            {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * Callback for the connect button pressed
     *
     * @param view The source element that send the event
     */
    public void onConnectButton(View view)
    {
        try
        {
            String ip = ((EditText)findViewById(R.id.ipValue)).getText().toString();
            Short password =
                    Short.decode(((EditText)findViewById(R.id.passwordValue)).getText().toString());

            /* First of all, store the ip and password */
            sharedPreferences.edit().putString("user_ip", ip).apply();
            sharedPreferences.edit().putString("user_password", Short.toString(password)).apply();

            /* Now, check the protocol used */
            ProtocolType protocolType = ProtocolType.TCP;
            int selectedRadioButtonID =
                    ((RadioGroup)findViewById(R.id.switchProtocol)).getCheckedRadioButtonId();

            if (- 1 != selectedRadioButtonID)
            {
                if ("UDP".equals(((RadioButton)findViewById(selectedRadioButtonID)).getText().toString()))
                {
                    protocolType = ProtocolType.UDP;
                }
            }

            /* Send to manager connect request */
            ApplicationCore.getInstance().connect(this, ip, password, protocolType);
        }
        catch (Exception ex)
        {
            Toast message = Toast.makeText(getApplicationContext(), "Invalid password",
                    Toast.LENGTH_SHORT);

            message.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            message.show();
        }
    }
}
