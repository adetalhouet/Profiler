package ets.genielog.appwatcher_3.profiling.core;

import java.io.File;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcAction;
import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcData;
import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcMessage;
import ets.genielog.appwatcher_3.profiling.ipc.IpcService;
import ets.genielog.appwatcher_3.profiling.ipc.IpcService.ipcClientListener;
import ets.genielog.appwatcher_3.profiling.model.NetworkInfo.networkInfo;
import ets.genielog.appwatcher_3.profiling.model.OsInfo.osInfo;
import ets.genielog.appwatcher_3.profiling.model.ProcessInfo.processInfo;
import ets.genielog.appwatcher_3.profiling.util.Config;

/**
 * This is the Profiler manager. Through this class you communicate with the
 * core engine define in cc (see jni). It implements the
 * {@link ipcClientListener}. The {@link IpcService} class let us add request
 * and manage them in order to send them. This class contains all the profiling
 * data dealing with {@link Battery}, {@link Processes}, {@link Memory},
 * {@link Network} and {@link Storage}.
 * 
 * @author alexisdet
 * 
 */
public class State implements ipcClientListener {

	private static final String TAG = "State - ";

	/** The current context */
	// private Context mContext;
	/** Singleton instance */
	private static State mInstance;

	/** The battery manager */
	private Battery mBatteryManager;
	/** The connection manager */
	private Network mNetworkManager;
	/** The memory manager */
	private Memory mMemoryManager;
	/** The cpu manager */
	private Processes mProcessManager;

	public static int PID;

	/** Set all the profiling action */
	public static final ipcAction allAction[] = new ipcAction[] {
			ipcAction.PROCESS, ipcAction.OS, ipcAction.NETWORK, ipcAction.CPU };

	/** Set the process profiling action */
	public static final ipcAction processAction[] = new ipcAction[] {
			ipcAction.PROCESS, ipcAction.OS, ipcAction.CPU };

	/**
	 * Constructor
	 */
	public State() {
	}

	/**
	 * Retrieves the {@link State} instance if exists, else creates it. Avoid
	 * creating different object.
	 * 
	 * @return the {@link State} instance
	 */
	public static State getInstance() {
		if (mInstance == null) {
			mInstance = new State();
		}
		return mInstance;
	}

	/**
	 * Initialize the ipc client and the data
	 */
	public void initProfiler(final Context context) {
		Log.d(Config.TAG_APP, TAG + "initProfiler");

		// IPC service
		IpcService.Initialize(context);
		// Battery
		this.mBatteryManager = Battery.getInstance();
		// Network
		this.mNetworkManager = Network.getInstance();
		// RAM
		this.mMemoryManager = Memory.getInstance();
		// CPU
		this.mProcessManager = Processes.getInstance();

		State.PID = -1;

		// Create folder
		new File(Config.APP_PATH).mkdir();

		new CountDownTimer(2000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				requestProfileState(State.allAction, false);
			}

			@Override
			public void onFinish() {
				requestProfileStateForProcess(State.processAction, 2635, true);
			}
		}.start();

	}

	/**
	 * This method stop the profiler process
	 */
	public void stopProfiler() {
		Log.d(TAG, "stopProfiler");

		// exit the library
		IpcService.getInstance().exit();
		// disconnect the ipc client socket
		IpcService.getInstance().disconnect();
	}

	/**
	 * Add a request to the ipc service command queue
	 * 
	 * @param cmd
	 *            - array of ipcAction to perform
	 * @param loop
	 *            - whether this action should be repeated
	 */
	public boolean requestProfileState(ipcAction cmd[], boolean loop) {
		return IpcService.getInstance().addRequest(cmd, -1, 0, loop, this);
	}

	/**
	 * Add a request to the ipc service command queue
	 * 
	 * @param cmd
	 *            - array of ipcAction to perform
	 * @param pid
	 *            - a process id
	 * @param loop
	 *            - whether this action should be repeated
	 */
	public boolean requestProfileStateForProcess(ipcAction cmd[], int pid,
			boolean loop) {
		Log.d(Config.TAG_APP, TAG + "profile pid");
		return IpcService.getInstance().addRequest(cmd, pid, 0, loop, this);
	}

	@Override
	public void onRecvData(ipcMessage result) {
		Log.d(Config.TAG_APP, TAG + " onRecvData");
		// if attempt fail, add request again
		if (result == null) {
			// do something
			return;
		}

		// clearDataSet();

		for (int index = 0; index < result.getDataCount(); index++) {
			try {
				// get the data
				ipcData rawData = result.getData(index);

				// process the OS data
				if (rawData.getAction() == ipcAction.OS) {
					mMemoryManager.setOsData(osInfo.parseFrom(rawData
							.getPayload(0)));
				}
				// process the Network data
				if (rawData.getAction() == ipcAction.NETWORK) {
					for (int count = 0; count < rawData.getPayloadCount(); count++) {
						networkInfo nwInfo = networkInfo.parseFrom(rawData
								.getPayload(count));

						String tp = mNetworkManager.getInterfaceStatus(nwInfo
								.getFlags());

						if (!tp.contains("lo"))
							if (tp.contains("up"))
								if (tp.contains("running"))
									mNetworkManager.add(nwInfo);
					}
					if (mNetworkManager.getNetworkData().isEmpty())
						networkDisconnected();
				}

				// process the Process data
				if (rawData.getAction() == ipcAction.PROCESS) {
					for (int count = 0; count < rawData.getPayloadCount(); count++) {

						processInfo psInfo = processInfo.parseFrom(rawData
								.getPayload(count));

						// check whether or not it is a native process
						if (psInfo.getUid() == 0
								|| psInfo.getName().contains("/system/")
								|| psInfo.getName().contains("/sbin/")) {
							mProcessManager
									.addNativeUsage(psInfo.getCpuUsage());
						} else {
							mProcessManager.addUserUsage(psInfo.getCpuUsage());

							mProcessManager.addProcess(new Process(psInfo
									.getName(), psInfo.getPid(), Memory
									.convertToSize((psInfo.getRss() * 1024),
											true), Processes
									.convertToUsage(psInfo.getCpuUsage())));
						}
					}
				}
			} catch (Exception e) {
				Log.d(Config.TAG_APP, TAG + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void networkDisconnected() {
		Log.d(Config.TAG_APP, TAG + "network disconnected");
	}

	public String send() {

		return null;
	}

	/**
	 * Clear the data remained by the objects
	 */
	private void clearDataSet() {
		this.mNetworkManager.clearDataSet();
		this.mProcessManager.clearDataSet();
		this.mMemoryManager.clearDataSet();
	}

	/**
	 * @return the mBatteryManager
	 */
	public Battery getBatteryManager() {
		return mBatteryManager;
	}

	/**
	 * @return the mNetworkManager
	 */
	public Network getNetworkManager() {
		return mNetworkManager;
	}

	/**
	 * @return the mMemoryManager
	 */
	public Memory getMemoryManager() {
		return mMemoryManager;
	}

	/**
	 * @return the mProcessManager
	 */
	public Processes getProcessManager() {
		return mProcessManager;
	}

}