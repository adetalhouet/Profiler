package ets.genielog.appwatcher_3.profiling.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import ets.genielog.appwatcher_3.profiling.ipc.IpcService;

public class Util {

	private static final String TAG = "Util - ";

	/**
	 * kill a process
	 * 
	 * @param pid
	 */
	public static void killProcess(int pid) {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "killProcess");
		IpcService.getInstance().killProcess(pid);
	}

	/**
	 * is ARMv7 base ?
	 * 
	 * @return true == yes , false == no
	 */
	@SuppressLint("DefaultLocale")
	public static boolean isARMv7() {
		return (android.os.Build.CPU_ABI.toLowerCase().contains("armeabi-v7"));
	}

	/**
	 * is ARM base ?
	 * 
	 * @return true == yes, false == no
	 */
	@SuppressLint("DefaultLocale")
	public static boolean isARM() {
		return (android.os.Build.CPU_ABI.toLowerCase().contains("armeabi"));
	}

	/**
	 * is MIPS base ?
	 * 
	 * @return true == yes, false == no
	 */
	@SuppressLint("DefaultLocale")
	public static boolean isMIPS() {
		return (android.os.Build.CPU_ABI.toLowerCase().contains("mips"));
	}

	/**
	 * is X86 base ?
	 * 
	 * @return true == yes, false == no
	 */
	@SuppressLint("DefaultLocale")
	public static boolean isX86() {
		return (android.os.Build.CPU_ABI.toLowerCase().contains("x86"));
	}

	/**
	 * This method checks a file status
	 * 
	 * @param file
	 *            - path
	 * @return true == exist, false == not exist
	 */
	@SuppressWarnings("unused")
	private static boolean fileExist(String localPath) {
		File targetFile = new File(localPath);
		return targetFile.exists();
	}

	/**
	 * This method copies a binary from asset directory to working directory.
	 * 
	 * @param assetPath
	 * @param localPath
	 * @param context
	 * @return true == copied, false == text busy
	 */
	private static boolean copyFile(String assetPath, String localPath,
			Context context) {
		try {
			// detect architecture
			if (isARM())
				assetPath += "_arm";
			else if (isX86())
				assetPath += "_x86";
			else if (isMIPS())
				assetPath += "_mips";
			else
				assetPath += "_arm";

			InputStream binary = context.getAssets().open(assetPath);
			FileOutputStream execute = new FileOutputStream(localPath);

			int read = 0;
			byte[] buffer = new byte[4096];

			while ((read = binary.read(buffer)) > 0) {
				execute.write(buffer, 0, read);
			}

			execute.close();
			binary.close();

			execute = null;
			binary = null;

		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method writes a security token file
	 * 
	 * @param tokenFilePath
	 *            - path of the security token
	 * @param token
	 *            - security token
	 * @return true == succeed, false == failed
	 */
	private static boolean writeTokenFile(String tokenFilePath, String token) {
		try {
			FileWriter file = new FileWriter(tokenFilePath);
			file.write(token);
			file.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method executes the profiling library as a binary
	 * 
	 * @param context
	 *            - the activity context
	 * @throws InterruptedException
	 */
	public static boolean execCore(Context context) {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "execCore");
		if (context == null)
			return false;

		String binary = context.getFilesDir().getAbsolutePath() + "/"
				+ Config.LIB_PROFILING_NAME;

		// copy file
		if (!copyFile(Config.LIB_PROFILING_NAME, binary, context))
			return false;

		// write token file
		writeTokenFile(binary + ".token", PreferencesHelper
				.getInstance(context).getToken());

		// lock file
		File file = new File(binary + ".lock");
		FileChannel channel = null;
		FileLock lock = null;
		try {
			channel = new RandomAccessFile(file, "rw").getChannel();
			lock = channel.tryLock();
		} catch (Exception e) {
			Log.d(TAG, "lock failed " + e.getLocalizedMessage());
			return false;
		}

		// execute profiling
		try {
			Runtime.getRuntime().exec("chmod 755 " + binary).waitFor();
			// if (!settings.isRoot())
			Runtime.getRuntime()
					.exec(new String[] { "sh", "-c",
							binary + " " + binary + ".token &" }).waitFor();
			// else
			// Runtime.getRuntime()
			// .exec(new String[] { "su", "-c ",
			// binary + " " + binary + ".token &" }).waitFor();

		} catch (Exception e) {
			return false;
		}

		// release the lock
		try {
			lock.release();
			channel.close();
		} catch (Exception e) {
		}

		return true;
	}
}
