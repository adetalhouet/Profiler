package ets.genielog.appwatcher_3.profiling.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ets.genielog.appwatcher_3.profiling.util.Config;

/**
 * This class defines a {@link BroadcastReceiver} acting as a callback triggered
 * when a {@code Intent.ACTION_BATTERY_LOW} or
 * {@code Intent.ACTION_BATTERY_OKAY} is send by the system.
 * 
 * @author alexisdet
 * 
 */
public class BatteryReceiver extends BroadcastReceiver {

	private static final String TAG = "BatteryReceiver - ";

	@Override
	public void onReceive(Context context, Intent intent) {

		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "called");

		if (intent.getAction() == Intent.ACTION_BATTERY_LOW) {
			// TODO stop scan process
			if (Config.DEBUG_PROFILING)
				Log.d(Config.TAG_APP, TAG + "battery low");
		}

		if (intent.getAction() == Intent.ACTION_BATTERY_OKAY) {
			// TODO start scan process
			if (Config.DEBUG_PROFILING)
				Log.d(Config.TAG_APP, TAG + "battery okay");
		}
	}
}