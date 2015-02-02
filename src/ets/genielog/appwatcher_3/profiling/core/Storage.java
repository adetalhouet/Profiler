package ets.genielog.appwatcher_3.profiling.core;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import ets.genielog.appwatcher_3.profiling.util.Config;

/**
 * This is the Storage manager. Through this class you can get the available
 * internal memory.
 * 
 * @author alexisdet
 * 
 */
public class Storage {

	private static final String TAG = "Storage - ";

	/**
	 * @return the available internal memory size
	 */
	public static String getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return formatSize(availableBlocks * blockSize);
	}

	/**
	 * Convert data as storage
	 * 
	 * @param size
	 *            - the current blocks size
	 * @return a string with correct format
	 */
	public static String formatSize(long size) {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "formatSize");
		String suffix = null;
		// define the size of the data
		if (size >= 1024) {
			suffix = " kB";
			size /= 1024;
			if (size >= 1024) {
				suffix = " MB";
				size /= 1024;
				if (size >= 1024) {
					suffix = " GB";
				}
			}
		}
		// parse the number
		StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
		// place the comma
		int commaOffset = resultBuffer.length() - 3;
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',');
			commaOffset -= 3;
		}
		// add the suffix
		if (suffix != null)
			resultBuffer.append(suffix);
		return resultBuffer.toString();
	}
}
