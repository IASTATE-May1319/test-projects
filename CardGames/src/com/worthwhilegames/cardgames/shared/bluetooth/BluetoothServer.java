package com.worthwhilegames.cardgames.shared.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.worthwhilegames.cardgames.shared.Util;
import com.worthwhilegames.cardgames.shared.connection.ConnectionConstants;
import com.worthwhilegames.cardgames.shared.connection.ConnectionServer;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This Singleton class acts as a Server for a Bluetooth connection.
 * 
 * It connects to up to 4 other Bluetooth devices (running the client), and then
 * communicates with any number of them (by sending messages and receiving messages).
 */
public class BluetoothServer extends BluetoothCommon implements ConnectionServer {
	/**
	 * The Logcat Debug tag
	 */
	private static final String TAG = BluetoothServer.class.getName();

	/**
	 * The Singleton instance of this class
	 */
	private static BluetoothServer instance = null;

	/**
	 * A map of MAC addresses to their corresponding BluetoothConnectionServices
	 */
	private HashMap<String, BluetoothConnectionService> services;

	/**
	 * The BluetoothAdapter used to query Bluetooth information
	 */
	private BluetoothAdapter mBluetoothAdapter;

	/**
	 * The Thread that runs while listening for connections
	 */
	private AcceptThread mAcceptThread;

	/**
	 * The context of this thread
	 */
	private Context mContext;

	/**
	 * The Handler to handle all messages coming from the BluetoothConnectionService
	 * 
	 * This should pretty much just pass the message on to the UI/GameLayer
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (Util.isDebugBuild()) {
				Log.d(TAG, "handleMessage: " + msg.what);
			}

			Bundle data = msg.getData();
			switch (msg.what) {
			case BluetoothConstants.STATE_MESSAGE:
				// When the state of the Bluetooth connection has changed
				// let all the listeners know that this has happened
				Intent i = new Intent(ConnectionConstants.STATE_CHANGE_INTENT);
				i.putExtra(ConnectionConstants.KEY_STATE_MESSAGE, data.getInt(ConnectionConstants.KEY_STATE_MESSAGE));
				i.putExtra(ConnectionConstants.KEY_DEVICE_ID, data.getString(ConnectionConstants.KEY_DEVICE_ID));
				mContext.sendBroadcast(i);
				break;
			case BluetoothConstants.READ_MESSAGE:
				Intent i1 = new Intent(ConnectionConstants.MESSAGE_RX_INTENT);
				i1.putExtra(ConnectionConstants.KEY_MESSAGE_TYPE, data.getInt(ConnectionConstants.KEY_MESSAGE_TYPE));
				i1.putExtra(ConnectionConstants.KEY_MESSAGE_RX, data.getString(ConnectionConstants.KEY_MESSAGE_RX));
				i1.putExtra(ConnectionConstants.KEY_DEVICE_ID, data.getString(ConnectionConstants.KEY_DEVICE_ID));
				mContext.sendBroadcast(i1);
				break;
			}
		}
	};

	/**
	 * Create a new BluetoothServer
	 * 
	 * @param ctx
	 */
	private BluetoothServer(Context ctx) {
		mContext = ctx;
		services = new HashMap<String, BluetoothConnectionService>();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mAcceptThread = new AcceptThread(mContext, mBluetoothAdapter, mHandler, services, 4);
	}

	/**
	 * Gets the instance of the BluetoothServer
	 * 
	 * @param ctx the Context of this BluetoothServer
	 * @return the BluetoothServer
	 */
	public static BluetoothServer getInstance(Context ctx) {
		if (instance == null) {
			instance = new BluetoothServer(ctx);
		}

		return instance;
	}

	/* (non-Javadoc)
	 * @see cs309.a1.shared.connection.ConnectionServer#startListening()
	 */
	@Override
	public void startListening(int maxConnections) {
		// Start the AcceptThread which listens for incoming connection requests
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread(mContext, mBluetoothAdapter, mHandler, services, maxConnections);
		}

		mAcceptThread.start();
	}

	/* (non-Javadoc)
	 * @see cs309.a1.shared.connection.ConnectionServer#stopListening()
	 */
	@Override
	public void stopListening() {
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
	}

	/* (non-Javadoc)
	 * @see cs309.a1.shared.connection.ConnectionServer#disconnect()
	 */
	@Override
	public void disconnect() {
		// Disconnect connections
		for (BluetoothConnectionService serv : services.values()) {
			serv.stop();
		}

		// Clear out the list of BluetoothConnectionServices
		services.clear();
	}

	/* (non-Javadoc)
	 * @see cs309.a1.shared.connection.ConnectionServer#getConnectedDeviceCount()
	 */
	@Override
	public int getConnectedDeviceCount() {
		return getConnectedDevices().size();
	}

	/* (non-Javadoc)
	 * @see cs309.a1.shared.connection.ConnectionServer#getConnectedDevices()
	 */
	@Override
	public List<String> getConnectedDevices() {
		if (services.size() == 0) {
			return new ArrayList<String>();
		}

		HashSet<String> toRet = new HashSet<String>();

		for (String s : services.keySet()) {
			BluetoothConnectionService service = services.get(s);

			if (service.getState() == ConnectionConstants.STATE_CONNECTED) {
				toRet.add(s);
			}
		}

		return new ArrayList<String>(toRet);
	}

	/* (non-Javadoc)
	 * 
	 * If address is null, we send the message to all connected devices.
	 * 
	 * @see cs309.a1.shared.bluetooth.BluetoothCommon#write(java.lang.Object, java.lang.String[])
	 */
	@Override
	public boolean write(int messageType, Object obj, String ... address) {
		boolean retVal = true;

		// If the caller didn't provide any addresses, send the message to all
		// connected devices
		if (address.length == 0) {
			for (BluetoothConnectionService service : services.values()) {
				if (!performWrite(service, messageType, obj)) {
					retVal = false;
				}
			}
		} else {
			// Otherwise send it out to the devices specified in address
			for (String addr : address) {
				BluetoothConnectionService service = services.get(addr);

				if (service != null) {
					if (!performWrite(service, messageType, obj)) {
						retVal = false;
					}
				}
			}
		}

		return retVal;
	}
}

