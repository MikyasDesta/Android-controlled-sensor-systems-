package com.example.suyash776.autobot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_QUERY_CONTROLLER_TYPE = 3;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int CONNECTION_LOST = 6;
    final static int REQUEST_START_JOYSTICK_ACTIVITY = 99;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    public static final String CONN_LOST = "Connection Lost";
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    public static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public BluetoothChatService mChatService = null;

    // Views involved in the home screen
    public TextView statusUpdate = null;
    public Button connect = null;
    public Button exit_Button = null;
    public Button video_Button = null;
    public Button joystick_Button = null;
    private static final boolean D = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(D)
            Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
        // If the device does not support BlueTooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this,"This device does not support bluetooth!!!",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (D) Log.i(TAG, "+++onStart+++");

        // Loading Font Face


        statusUpdate = (TextView) findViewById(R.id.Conncect_Text);
       // video_Button=(Button)findViewById(R.id.VideoStreamBtn);
        connect = (Button) findViewById(R.id.ConnectBtn);
        exit_Button = (Button) findViewById(R.id.ExitBtn);
        //joystick_Button = (Button) findViewById(R.id.button_joystick);
       setupUI();
    }

    private void setupUI() {
        connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }

            }
        });
        exit_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* joystick_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joystickIntent = new Intent(MainActivity.this,Joystick.class);
                startActivity(joystickIntent);
            }
        });
        video_Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(MainActivity.this,VideoStream.class);
                startActivity(videoIntent);
            }
        });
*/
        if (mChatService == null) setupChat();
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");
        mChatService = BluetoothChatService.getBluetoothChatServiceObject(this,mHandler);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.onDestroy();
        if (D)
            Log.i(TAG, "+++onDestroy+++");
        if (mChatService != null)
            mChatService.stop();
        mBluetoothAdapter.disable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      BluetoothChatService.setHandler(mHandler);
        switch (requestCode){
            case REQUEST_START_JOYSTICK_ACTIVITY:		// Return from joystick activity
                restart_controller_selection();
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) { // If user hits "Okay" for
                    // BT enable prompt
                    connect.setVisibility(View.GONE);
                    exit_Button.setVisibility(View.GONE);
                    // Bluetooth is now enabled, so set up a chat session

                    String address = mBluetoothAdapter.getAddress();
                    String name = mBluetoothAdapter.getName();

                    // Applying font
                    //statusUpdate.setTypeface(tf);
                    statusUpdate.setText(name + "\n" + address);

                    Intent displaylistintent = null;
                    displaylistintent = new Intent(MainActivity.this,
                            DeviceListActivity.class); // Starts the DeviceListActivity class
                    startActivityForResult(displaylistintent,
                            REQUEST_CONNECT_DEVICE);
                    setupChat();
                } else {
                    // User did not hit the "okay" button or an error occured
                    Log.d(TAG, "Bluetooth not enabled");
                    Toast.makeText(MainActivity.this, "Bluetooth not enabled",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    mBluetoothAdapter.disable();
                    finish();
                }
                break;
            case REQUEST_QUERY_CONTROLLER_TYPE:
                // Returns here after returning from ControllerType Activity
                if(resultCode == Activity.RESULT_OK) {
                    String controllerType = data.getExtras().getString(ControllerType.CONTROLLER_TYPE);
                    Log.d(TAG, ""+controllerType);
                    if(controllerType.contains("Sensor Values")) {
                        Intent startJoystickActivity = null;
                        Log.d(TAG, "Starting Joystick Class");
                        startJoystickActivity = new Intent(MainActivity.this,
                                Joystick.class);
                        startActivityForResult(startJoystickActivity, REQUEST_START_JOYSTICK_ACTIVITY);
                    }
                }
                else {
                    Log.d(TAG, "Controller Type Selection Aborted");
                    if (mChatService != null)
                        mChatService.stop();
                    mBluetoothAdapter.disable();
                    finish();
                }
                break;
        }
    }
    private void restart_controller_selection() {
        Intent startControllerselectionActivity = null;
        startControllerselectionActivity = new Intent(MainActivity.this, ControllerType.class);
        startActivityForResult(startControllerselectionActivity, REQUEST_QUERY_CONTROLLER_TYPE);
    }

    private void connectDevice(Intent data, boolean b) {
        String address = data.getExtras().getString(
                DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        Toast.makeText(MainActivity.this, "Trying to pair with " + device,
                Toast.LENGTH_SHORT).show();
        // Attempt to connect to the device
        mChatService.connect(device, b);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Toast.makeText(MainActivity.this, "State: Connected", Toast.LENGTH_SHORT).show();
                            Intent startControllerselectionActivity = null;
                            startControllerselectionActivity = new Intent(MainActivity.this, ControllerType.class);
                            startActivityForResult(startControllerselectionActivity, REQUEST_QUERY_CONTROLLER_TYPE);
                            break;

                        case BluetoothChatService.STATE_CONNECTING:
                            Toast.makeText(MainActivity.this, "State: Connecting", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.d(TAG, "Reached here by accident");
                            Toast.makeText(MainActivity.this,
                                    "Unable to connect. Retry", Toast.LENGTH_SHORT)
                                    .show();
                            Intent displaylistintent = null;
                            displaylistintent = new Intent(MainActivity.this,
                                    DeviceListActivity.class);
                            startActivityForResult(displaylistintent,
                                    REQUEST_CONNECT_DEVICE);
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);

                    Toast.makeText(getApplicationContext(),
                            "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

}
