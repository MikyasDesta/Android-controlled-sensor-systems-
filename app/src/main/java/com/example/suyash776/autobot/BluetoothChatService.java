package com.example.suyash776.autobot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class   BluetoothChatService {
	// Debugging
	private static final String TAG = "BluetoothChatService";
	private static final boolean D = true;

	// Member fields
	private final BluetoothAdapter mAdapter;
	private static Handler mHandler;
	private static int mState;
	private ConnectThread mConnectThread;
	private static ConnectedThread mConnectedThread;
	private AcceptThread mSecureAcceptThread;

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothChatSecure";

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0; // We are now doing nothing
	public static final int STATE_LISTEN = 1; // Now Listening for incoming
	// connections
	public static final int STATE_CONNECTING = 2; // Now initiating an outgoing
	// connection
	public static final int STATE_CONNECTED = 3; // Now connected to a remote
	// device
	public static final int STATE_CONNECTIONLOST = 4;

	/*
	 * Addition. Singleton instance of BluetoothChatServer
	 */
	private static BluetoothChatService sChatService;

	private BluetoothChatService(Context context, Handler handler){
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mHandler = handler;
	}

	// Addition
	public static Handler getHandler() {
		return mHandler;
	}

	public static synchronized BluetoothChatService getBluetoothChatServiceObject(Context pcontext, Handler phandler){
		if(sChatService == null) {
			sChatService = new BluetoothChatService(pcontext, phandler);
		}
		return sChatService;
	}

	/*
	 * Set the current state of the chat connection
	 * 
	 * @param state: An integer defining the current connection state
	 */
	private synchronized static void setState(int state) {
		if (D)
			Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		Log.d(TAG, "Sending message to main activity "+MainActivity.MESSAGE_STATE_CHANGE);
		mHandler.obtainMessage(MainActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	/* Set the current connection state */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		if (D)
			Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(STATE_LISTEN);

		// Start the thread to listen on a BluetoothServerSocket
		if (mSecureAcceptThread == null) {
			mSecureAcceptThread = new AcceptThread(true);
			mSecureAcceptThread.start();
		}
	}

	/*
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * 
	 * @param device: the BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device, boolean secure) {
		if (D)
			Log.d(TAG, "Connect to " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect to a given device
		mConnectThread = new ConnectThread(device, secure);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device, final String socketType) {
		if (D)
			Log.d(TAG, "connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket, socketType);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(MainActivity.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private String mSocketType;

		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = "Secure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a successful connection or an exception
				Log.d(TAG, "Before Connect()");
				mmSocket.connect();			//THIS IS GIVING PROBLEM
				Log.d(TAG, "After Connect()");
			} catch (IOException e) {
				// Close the socket
				try {
					Log.d(TAG, "connect() failed. Closing socket!!!");
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() " + mSocketType
							+ " socket during connection failure", e2);
				}

				if (D) Log.d(TAG, "Connection Failed. Inside catch() block");
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothChatService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice, mSocketType);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect " + mSocketType
						+ " socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private static class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private  static InputStream mmInStream = null;
		private  static OutputStream mmOutStream = null;

		public ConnectedThread(BluetoothSocket socket, String socketType) {
			Log.d(TAG, "create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");			
			int bytes;
			char get_char;
			// Keep listening to the InputStream while connected
			String packet="";
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read();
					get_char=(char)bytes;
					//Log.d("get_char", String.valueOf(get_char));
					if(get_char!=66) {  // Until 'B' received						
						packet+=get_char;
					}
					else {
						packet+=get_char;
						mHandler.obtainMessage(Joystick.MESSAGE_READ,  packet).sendToTarget();
						//mHandler.obtainMessage(Accelerometer.MESSAGE_READ,  packet).sendToTarget();
						Log.d("COMPLETE_PACKET", packet);
						packet = "";
					}										
				} 
				catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * 
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {
		if (D) Log.d(TAG, "stop");

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		//setState(STATE_NONE);  // Commented this line out because it was causing DeviceListActivity Activity to be re-launched
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 * @param out The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public static void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (sChatService) {
			if (mState != STATE_CONNECTED) return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		// Send a failure message back to the Activity
		Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(MainActivity.TOAST, "Unable to connect to device. Retry");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		BluetoothChatService.this.start();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private static void connectionLost() {
		// Send a failure message back to the Activity
		setState(STATE_CONNECTIONLOST);
		Log.i(TAG, "Connection Lost");
		Message msg = mHandler.obtainMessage(MainActivity.CONNECTION_LOST);
		Bundle bundle = new Bundle();
		bundle.putString(MainActivity.CONN_LOST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// Start the service over to restart listening mode
		// BluetoothChatService.this.start();
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted
	 * (or until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;
		private String mSocketType;

		public AcceptThread(boolean secure) {
			BluetoothServerSocket tmp = null;
			mSocketType = "Secure";

			// Create a new listening server socket
			try {
				if (secure) {
					tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
				}
			} catch (IOException e) {
				Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			if (D) Log.d(TAG, "Socket Type: " + mSocketType +
					" BEGIN mAcceptThread" + this);
			setName("AcceptThread" + mSocketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					if (D) Log.d(TAG, "Before socket = mmServerSocket.accept()");
					socket = mmServerSocket.accept();			
					if (D) Log.d(TAG, "After socket = mmServerSocket.accept()");			//Problem here
				} catch (IOException e) {
					Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BluetoothChatService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice(),
									mSocketType);
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate new socket.
							try {
								socket.close();
							} catch (IOException e) {
								Log.e(TAG, "Could not close unwanted socket", e);
							}
							break;
						}
					}
				}
			}
			if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

		}

		public void cancel() {
			if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
			}
		}
	}

	public static void setHandler(Handler jHandler) {
		mHandler = jHandler;
	}        
}
