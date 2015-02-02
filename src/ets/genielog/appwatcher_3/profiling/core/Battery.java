package ets.genielog.appwatcher_3.profiling.core;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import ets.genielog.appwatcher_3.profiling.util.Config;

/**
 * This is the Battery manager. Through this class you can register and
 * unregister from the battery broadcast receiver. You can also get the current
 * battery level.
 * 
 * @author alexisdet
 * 
 */
public class Battery {

	private static final String TAG = "Battery - ";

	/** Singleton instance */
	private static Battery mInstance;
	/** Battery status */
	private Intent mBatteryStatus;

	/**
	 * Constructor
	 */
	public Battery() {
	}

	/**
	 * Retrieves the {@link Battery} instance if exists, else creates it. Avoid
	 * creating different object.
	 * 
	 * @return the {@link Battery} instance
	 */
	public static Battery getInstance() {
		if (mInstance == null) {
			mInstance = new Battery();
		}
		return mInstance;
	}

	/**
	 * Register the battery receiver
	 * 
	 * @param context
	 *            - the activity context
	 */
	public void startBatteryMonitor(Context context) {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "startBatteryMonitor");
		// The BatteryManager broadcasts all battery and charging details in a
		// sticky Intent that includes the charging status. Because it's a
		// sticky intent, we don't need to register a BroadcastReceiver
		try {
			IntentFilter battFilter = new IntentFilter(
					Intent.ACTION_BATTERY_LOW);
			mBatteryStatus = context.registerReceiver(null, battFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method defines the battery level
	 * 
	 * @return the battery level
	 */
	public int getBatteryLevel() {
		int rawlevel = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
				-1);
		int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int level = -1; // percentage, or -1 for unknown
		if (rawlevel > 0 && scale > 0) {
			level = (rawlevel * 100) / scale;
		} else
			level = 0;
		return level;
	}

	/**
	 * This method defines whether or not the battery is charging
	 * 
	 * @return true == in charge, false == not in charge
	 */
	public boolean isCharging() {
		int status = mBatteryStatus
				.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		return status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
	}
}
