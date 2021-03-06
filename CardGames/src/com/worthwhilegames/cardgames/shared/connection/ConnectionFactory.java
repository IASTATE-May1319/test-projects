package com.worthwhilegames.cardgames.shared.connection;

import com.worthwhilegames.cardgames.shared.Constants;
import com.worthwhilegames.cardgames.shared.bluetooth.BluetoothClient;
import com.worthwhilegames.cardgames.shared.bluetooth.BluetoothServer;
import com.worthwhilegames.cardgames.shared.wifi.WifiClient;
import com.worthwhilegames.cardgames.shared.wifi.WifiServer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * A Factory class for getting instances of a connection
 * client or server.
 */
public class ConnectionFactory {

	/**
	 * Get an instance of a Client connection based on the current
	 * connection type.
	 * 
	 * @param ctx
	 * @return the ConnectionClient
	 */
	public static ConnectionClient getClientInstance(Context ctx) {
		ConnectionType type = getConnectionType(ctx);

		if (type == ConnectionType.BLUETOOTH) {
			return BluetoothClient.getInstance(ctx);
		} else if (type == ConnectionType.WIFI) {
			return WifiClient.getInstance(ctx);
		}

		return null;
	}

	/**
	 * Get an instance of a Server connection based on the current
	 * connection type.
	 * 
	 * @param ctx
	 * @return the ConnectionServer
	 */
	public static ConnectionServer getServerInstance(Context ctx) {
		ConnectionType type = getConnectionType(ctx);

		if (type == ConnectionType.BLUETOOTH) {
			return BluetoothServer.getInstance(ctx);
		} else if (type == ConnectionType.WIFI) {
			return WifiServer.getInstance(ctx);
		}

		return null;
	}

	/**
	 * Get the type of connection that is currently in use
	 * 
	 * @return the type of connection in use
	 */
	public static ConnectionType getConnectionType(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences(Constants.PREFERENCES, Context.MODE_WORLD_READABLE);
		String connectionType = prefs.getString(Constants.CONNECTION_TYPE, Constants.WIFI);

		if (Constants.WIFI.equals(connectionType)) {
			return ConnectionType.WIFI;
		} else if (Constants.BLUETOOTH.equals(connectionType)) {
			return ConnectionType.BLUETOOTH;
		}

		return ConnectionType.WIFI;
	}

}
