package ets.genielog.appwatcher_3.profiling.core;

import android.annotation.SuppressLint;
import android.util.SparseArray;

/**
 * This is the Process manager. Through this class you can get the native and
 * the user cpu usage. You also have a list gathering all the {@link Process}
 * 
 * @author alexisdet
 * 
 */
public class Processes {

	/** Singleton instance */
	private static Processes mInstance;

	/** The native cpu usage */
	private float mNativeUsage;
	/** The user cpu usage */
	private float mUserUsage;

	private SparseArray<Process> mProcessList;
	public int key;

	/**
	 * Constructor
	 */
	public Processes() {
		mNativeUsage = 0;
		mUserUsage = 0;
		mProcessList = new SparseArray<Process>();
		key = 0;
	}

	/**
	 * Retrieves the {@link Processes} instance if exist, else creates it. Avoid
	 * creating different object.
	 * 
	 * @return the {@link Processes} instance
	 */
	public static Processes getInstance() {
		if (mInstance == null)
			mInstance = new Processes();
		return mInstance;
	}

	/**
	 * @return the native cpu usage in %
	 */
	public String getNativeUsage() {
		return convertToUsage(mNativeUsage);
	}

	/**
	 * @return the user cpu usage in %
	 */
	public String getUserUsage() {
		return convertToUsage(mUserUsage);
	}

	/**
	 * @return the total cpu usage in %
	 */
	public String getTotalUsage() {
		return convertToUsage(mUserUsage + mNativeUsage);
	}

	/**
	 * @param nativeUsage
	 *            - the native cpu usage to add
	 */
	public void addNativeUsage(float usage) {
		this.mNativeUsage += usage;
	}

	/**
	 * @param userUsage
	 *            - the user cpu usage to add
	 */
	public void addUserUsage(float usage) {
		this.mUserUsage += usage;
	}

	/**
	 * @param process
	 *            - the process to add
	 */
	public void addProcess(Process process) {
		mProcessList.put(key, process);
		key++;
	}

	/**
	 * Convert data as usage
	 * 
	 * @param data
	 *            - the data to convert
	 * @return a string of float value
	 */
	@SuppressLint("DefaultLocale")
	public static String convertToUsage(float data) {
		return String.format("%.1f", data);
		// + " %";
	}

	/**
	 * Clear the data remained by the object
	 */
	public void clearDataSet() {
		mNativeUsage = 0;
		mUserUsage = 0;
		mProcessList.clear();
		key = 0;
	}

	/**
	 * @return the ProcessList
	 */
	public SparseArray<Process> getProcessList() {
		return mProcessList;
	}

}
