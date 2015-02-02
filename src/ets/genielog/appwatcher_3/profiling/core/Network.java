package ets.genielog.appwatcher_3.profiling.core;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo;
import ets.genielog.appwatcher_3.profiling.util.Config;

/**
 * This is the Connection manager. Through this class you can get the current
 * connected interface, and their uplink and downlink bandwidth.
 * 
 * @author alexisdet
 * 
 */
public class Network {

	private static final String TAG = "Network - ";

	/** Singleton instance */
	private static Network mInstance;
	/** The current connected interface */
	private ArrayList<networkInfo> mNetworkData;

	/**
	 * Constructor
	 */
	public Network() {
		mNetworkData = new ArrayList<networkInfo>();
	}

	/**
	 * Retrieves the {@link Network} instance if exist, else creates it. Avoid
	 * creating different object
	 * 
	 * @param context
	 *            - the activity context
	 * @return the {@link Network} instance
	 */
	public static Network getInstance() {
		if (mInstance == null)
			mInstance = new Network();
		return mInstance;
	}

	/**
	 * This method retrieves the interface connectivity status from its
	 * associated flag
	 * 
	 * @param flag
	 *            - the interface flag
	 * @return the interface status
	 */
	public String getInterfaceStatus(int flag) {

		StringBuilder tp = new StringBuilder();

		// IFF_UP = 0x1, /* Interface is up. */
		if ((flag & 0x1) != 0)
			tp.append("up ");
		else
			tp.append("down ");

		// IFF_BROADCAST = 0x2, /* Broadcast address valid. */
		if ((flag & 0x2) != 0)
			tp.append("broadcast ");

		// IFF_DEBUG = 0x4, /* Turn on debugging. */
		// IFF_LOOPBACK = 0x8, /* Is a loopback net. */
		if ((flag & 0x8) != 0)
			tp.append("loopback ");

		// IFF_POINTOPOINT = 0x10, /* Interface is point-to-point link. */
		if ((flag & 0x10) != 0)
			tp.append("p2p ");

		// IFF_NOTRAILERS = 0x20, /* Avoid use of trailers. */
		// IFF_RUNNING = 0x40, /* Resources allocated. */
		if ((flag & 0x40) != 0)
			tp.append("running ");

		// IFF_NOARP = 0x80, /* No address resolution protocol. */
		// IFF_PROMISC = 0x100, /* Receive all packets. */
		if ((flag & 0x100) != 0)
			tp.append("promisc ");

		// IFF_ALLMULTI = 0x200, /* Receive all multicast packets. */
		// IFF_MASTER = 0x400, /* Master of a load balancer. */
		// IFF_SLAVE = 0x800, /* Slave of a load balancer. */

		// IFF_MULTICAST = 0x1000, /* Supports multicast. */
		if ((flag & 0x1000) != 0)
			tp.append("multicast ");

		// IFF_PORTSEL = 0x2000, /* Can set media type. */
		// IFF_AUTOMEDIA = 0x4000, /* Auto media select active. */
		// IFF_DYNAMIC = 0x8000 /* Dialup device with changing addresses. */

		return tp.toString();
	}

	/**
	 * add a connected interface to the list
	 * 
	 * @param nwInfo
	 *            - the interface to add
	 */
	public void add(networkInfo nwInfo) {
		mNetworkData.add(nwInfo);
	}

	/**
	 * Clear the data remained by the object
	 */
	public void clearDataSet() {
		mNetworkData.clear();
	}

	/**
	 * @return the mNetworkData
	 */
	public ArrayList<networkInfo> getNetworkData() {
		return mNetworkData;
	}

	/****************************************************************************************************/

	/**
	 * This method gets the network info
	 * 
	 * @param context
	 *            - the activity context
	 * @return the network info
	 */
	public static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/**
	 * This method checks if there is any connectivity
	 * 
	 * @param context
	 *            - the activity context
	 * @return true == connected, false == not
	 */
	public static boolean isConnected(Context context) {
		NetworkInfo info = Network.getNetworkInfo(context);
		return (info != null && info.isConnected());
	}

	/**
	 * This method checks if there is any connectivity to a WiFi network
	 * 
	 * @param context
	 *            - the activity context
	 * @return true == connected to WiFi, false == not
	 */
	public static boolean isConnectedWifi(Context context) {
		NetworkInfo info = Network.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * This method checks if there is any connectivity to a mobile network
	 * 
	 * @param context
	 *            - the activity context
	 * @return true == connected to mobile, false == not
	 */
	public static boolean isConnectedMobile(Context context) {
		NetworkInfo info = Network.getNetworkInfo(context);
		return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * This method checks if there is fast connectivity
	 * 
	 * @param context
	 *            - the activity context
	 * @return true == fast, false == not fast
	 */
	public static boolean isConnectedFast(Context context) {
		NetworkInfo info = Network.getNetworkInfo(context);
		return (info != null && info.isConnected() && Network.isConnectionFast(
				info.getType(), info.getSubtype()));
	}

	/**
	 * This method checks if the connection is fast according to mobile
	 * specifications
	 * 
	 * @param type
	 *            - either {@code ConnectivityManager.TYPE_WIFI} or
	 *            {@code ConnectivityManager.TYPE_MOBILE}
	 * @param subType
	 *            - the network technology if
	 *            {@code ConnectivityManager.TYPE_MOBILE}
	 * @return true == fast, false == not fast
	 */
	public static boolean isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return true; // ~ 2-14 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return true; // ~ 700-1700 kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return true; // ~ 1-23 Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
				/*
				 * Above API level 7, make sure to set android:targetSdkVersion
				 * to appropriate level to use these
				 */
			case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
				return true; // ~ 1-2 Mbps
			case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
				return true; // ~ 5 Mbps
			case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
				return true; // ~ 10-20 Mbps
			case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
				return false; // ~25 kbps
			case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
				return true; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * This method gets the connection speed for WiFi interface
	 * 
	 * @param context
	 *            - the activity context
	 * @return the connection speed ({@code WifiInfo.LINK_SPEED_UNITS})
	 */
	public Integer getWifiConnectionSpeed(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			Integer linkSpeed = wifiInfo.getLinkSpeed(); // measured using
			if (Config.DEBUG_PROFILING)
				Log.d(Config.TAG_APP, TAG + "link speed : " + linkSpeed); // WifiInfo.LINK_SPEED_UNITS
			return linkSpeed;
		}
		return null;
	}
}
