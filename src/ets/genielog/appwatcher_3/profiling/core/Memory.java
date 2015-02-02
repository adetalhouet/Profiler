package ets.genielog.appwatcher_3.profiling.core;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.os.Debug.MemoryInfo;
import ets.genielog.appwatcher_3.profiling.model.OsInfo.osInfo;

/**
 * This is the Memory manager. Through this class you can get the total memory,
 * the current free memory and the current used memory of the device.
 * 
 * @author alexisdet
 * 
 */
public class Memory {

	/** Singleton instance */
	private static Memory mInstance;
	/** The {@link osInfo} containing all the memory data */
	private osInfo mMemoryData;

	/**
	 * Constructor
	 */
	public Memory() {
		mMemoryData = null;
	}

	/**
	 * Retrieves the {@link Memory} instance if exist, else creates it. Avoid
	 * creating different object
	 * 
	 * @return the {@link Memory} instance
	 */
	public static Memory getInstance() {
		if (mInstance == null)
			mInstance = new Memory();
		return mInstance;
	}

	/**
	 * @param osData
	 *            - the current data
	 */
	public void setOsData(osInfo osData) {
		this.mMemoryData = osData;
	}

	/**
	 * @return - the total memory
	 */
	public String getTotalMemory() {
		return convertToSize(mMemoryData.getTotalMemory(), true);
	}

	/**
	 * @return - the free memory
	 */
	public String getFreeMemory() {
		return convertToSize(
				mMemoryData.getFreeMemory() + mMemoryData.getBufferedMemory()
						+ mMemoryData.getCachedMemory(), true);
	}

	/**
	 * get private memory
	 * 
	 * @param activityMgr
	 *            - the activity manager
	 * @param pid
	 *            - the process id of process
	 * @return private memory value
	 */
	public static MemoryInfo getMemoryInfoPerPID(ActivityManager activityMgr,
			int pid) {
		int processPID[] = new int[1];
		processPID[0] = pid;
		MemoryInfo[] memInfo = activityMgr.getProcessMemoryInfo(processPID);
		return memInfo[0];
	}

	/**
	 * @return the memory used by my process
	 */
	public static MemoryInfo getMyProcessMemoryInfo(ActivityManager activityMgr) {
		int processPID[] = new int[1];
		processPID[0] = android.os.Process.myPid();
		MemoryInfo[] memInfo = activityMgr.getProcessMemoryInfo(processPID);
		return memInfo[0];
	}

	/**
	 * Convert data as memory
	 * 
	 * @param data
	 *            - the data to convert
	 * @return a string with correct format
	 * 
	 *         Reference:
	 *         http://stackoverflow.com/questions/3758606/how-to-convert
	 *         -byte-size-into-human-readable-format-in-java
	 */
	@SuppressLint("DefaultLocale")
	public static String convertToSize(long data, boolean si) {
		int unit = si ? 1000 : 1024;
		if (data < unit)
			return data + " B";
		int exp = (int) (Math.log(data) / Math.log(unit));
		// String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
		// + (si ? "" : "i");
		return String.format("%.1f", data / Math.pow(unit, exp));
		// return String.format("%.1f %sB", data / Math.pow(unit, exp), pre);
	}

	/**
	 * Clear the data remained by the object
	 */
	public void clearDataSet() {
		mMemoryData = null;
	}
}
