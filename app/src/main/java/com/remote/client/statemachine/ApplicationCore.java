package com.remote.client.statemachine;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.remote.client.activity.ConnectionActivity;
import com.remote.client.activity.RemoteActivity;

import java.util.concurrent.atomic.AtomicReference;

import byteremote.client.ByteClient;
import byteremote.client.tcp.ByteTCPClient;
import byteremote.client.udp.ByteUDPClient;
import byteremote.common.socket.ProtocolType;
import remotecontrol.RemoteControl;

/**
 * This public class is the main core of the application which
 * contains information about the current state.
 */
public class ApplicationCore
{
    /* Tag for debug */
    private static final String TAG              = "ApplicationCore";
    private static final int[]  SERVER_PORT_LIST = {40000,
                                                    40001,
                                                    40002,
                                                    40003};

    public static final int INVALID_TOKEN = - 1;

    /* Singleton instance */
    private static final ApplicationCore instance = new ApplicationCore();

    private int        token  = - 1;
    private ByteClient broker = null;

    private AtomicReference<RemoteControl>    remoteControl = null;
    private AtomicReference<ApplicationState> state         = null;


    public static ApplicationCore getInstance()
    {
        return ApplicationCore.instance;
    }

    /**
     * Singleton private constructor
     */
    private ApplicationCore()
    {
        this.remoteControl = new AtomicReference<>(null);
        this.state = new AtomicReference<>(ApplicationState.DISCONNECTED);
    }

    public RemoteControl getControl()
    {
        return this.remoteControl.get();
    }

    /**
     * Return the application current state
     *
     * @return current state
     */
    public ApplicationState getState()
    {
        return this.state.get();
    }

    /**
     * Set the new application state
     *
     * @param newState the new application state
     * @return true if is a valid state or false otherwise
     */
    public boolean setState(ApplicationState newState)
    {
        if (ApplicationState.DISCONNECTED == this.state.get())
        {
            /* The application is not connected to server
             * changing state is forbidden */
            return false;
        }
        if (ApplicationState.CONNECTED == newState ||
            ApplicationState.DISCONNECTED == newState)
        {
            /* It is not allowed to set CONNECTED/DISCONNECTED as a new state */
            return false;
        }

        /* The next state is valid */
        this.state.set(newState);
        return true;
    }

    /**
     * Get the connection state.
     *
     * @return true if the connection with the server is established or false otherwise
     */
    public boolean connected()
    {
        return (this.state.get() != ApplicationState.DISCONNECTED);
    }

    public void checkState(Activity activity, ApplicationState knownState)
    {
        if ((ApplicationState.DISCONNECTED == knownState) &&
            (ApplicationState.DISCONNECTED != this.state.get()))
        {
            /* If the known state is disconnected, but the actual state is disconnected */
            this.startRemoteActivity(activity);
        }
        else if ((ApplicationState.DISCONNECTED != knownState) &&
                 (ApplicationState.DISCONNECTED == this.state.get()))
        {
            /* If the known state is connected, but the actual state is disconnected */
            this.stopRemoteActivity(activity);
        }
    }

    /**
     * Establish the connection and start the window activity if possible
     * Note: This call will create a new thread that will handle the process
     *
     * @param activity The activity that calls this function (Instance of ConnectionActivity)
     * @param ip       The ip of the server
     * @param password The requested password
     */
    public void connect(Activity activity, String ip, short password, ProtocolType type)
    {
        if (ApplicationState.DISCONNECTED == this.state.get())
        {
            new Thread(() ->
            {
                try
                {
                    /* Establish connection with the server */
                    this.token = createClientAndConnectToServer(ip, type, password);

                    if (0 < this.token)
                    {
                        Log.d(TAG, "Successfully connected to remote");
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Connected",
                                Toast.LENGTH_SHORT).show());
                        this.state.set(ApplicationState.CONNECTED);
                        this.startRemoteActivity(activity);
                    }
                    else
                    {
                        Log.d(TAG, "Connection to remote refused");

                        activity.runOnUiThread(() -> Toast.makeText(activity, "Connection refused",
                                Toast.LENGTH_SHORT).show());

                        /* Connection refused. Close the socket */
                        this.remoteControl.set(null);
                        this.token = - 1;
                    }
                }
                catch (Exception ex)
                {
                    Log.d(TAG, "Socket creation failed");
                    Log.d(TAG, Log.getStackTraceString(ex));

                    activity.runOnUiThread(() -> Toast.makeText(activity, "Connection failed",
                            Toast.LENGTH_SHORT).show());
                }
            }).start();
        }
    }

    /**
     * Close the connection
     * Note: This call will create a new thread that will handle the process.
     *
     * @param activity The activity that calls this function (Instance of
     *                 ConnectionActivity)
     */
    public void disconnect(Activity activity, String message)
    {
        if (ApplicationState.DISCONNECTED != this.state.get())
        {
            new Thread(() ->
            {
                this.state.set(ApplicationState.DISCONNECTED);

                try
                {
                    /* Close the server connection */
                    if (this.remoteControl.get().disconnect(this.token))
                    {
                        Log.d(TAG, "disconnect: Success");
                    }
                    else
                    {
                        /* Is this possible? */
                        Log.d(TAG, "disconnect: Failed");
                    }

                    /* Close the socket */
                    this.token = - 1;
                    this.broker.disconnect();
                }
                catch (Exception ex)
                {
                    Log.d(TAG, Log.getStackTraceString(ex));
                }

                this.broker = null;
                this.remoteControl.set(null);
                activity.runOnUiThread(() -> Toast.makeText(activity, message,
                        Toast.LENGTH_SHORT).show());
                this.stopRemoteActivity(activity);
            }).start();
        }
    }

    /**
     * Create a new client and connect it to the server
     *
     * @param ip       Server host name
     * @param type     Protocol type
     * @param password Inserted password
     * @return a valid token for a successful connection
     */
    private int createClientAndConnectToServer(String ip, ProtocolType type, short password)
    {
        int token = INVALID_TOKEN;

        for (int port : SERVER_PORT_LIST)
        {
            try
            {
                /* Open socket to the exposed object */
                Log.d(TAG, "createClientAndConnectToServer: port: " + port);

                if (ProtocolType.TCP == type)
                {
                    this.broker = new ByteTCPClient(ip, port);
                }
                else
                {
                    this.broker = new ByteUDPClient(ip, port, port);
                    /* Set timeout to 200 ms */
                    ((ByteUDPClient)this.broker).setDataTimeOut(200);
                }

                this.remoteControl.set(new RemoteControl(this.broker));

                /* Establish connection with the server */
                token = this.remoteControl.get().connect(password);

                if (INVALID_TOKEN != token)
                {
                    return token;
                }
                else
                {
                    this.broker.disconnect();
                    this.broker = null;
                }
            }
            catch (Exception ex)
            {
                Log.d(TAG, Log.getStackTraceString(ex));
            }
        }

        return INVALID_TOKEN;
    }

    /**
     * Get the token
     *
     * @return The token
     */
    public int getToken()
    {
        return token;
    }

    /**
     * Start the remote activity
     *
     * @param currentActivity The current activity
     */
    private void startRemoteActivity(Activity currentActivity)
    {
        /* Create a new intent */
        Intent intent = new Intent(currentActivity, RemoteActivity.class);
        /* Start the window activity */
        currentActivity.startActivity(intent);
        /* Finish the old activity */
        currentActivity.finish();
    }

    /**
     * Stop the remote activity
     *
     * @param currentActivity The current activity
     */
    private void stopRemoteActivity(Activity currentActivity)
    {
        /* Create a new intent */
        Intent intent = new Intent(currentActivity, ConnectionActivity.class);
        /* Start the window activity */
        currentActivity.startActivity(intent);
        /* Finish the old activity */
        currentActivity.finish();
    }
}
