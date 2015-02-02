package ets.genielog.appwatcher_3.profiling.ipc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.AsyncTask;
import android.util.Log;

import com.google.protobuf.ByteString;

import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcAction;
import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcData;
import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcData.Builder;
import ets.genielog.appwatcher_3.profiling.ipc.IpcMessage.ipcMessage;
import ets.genielog.appwatcher_3.profiling.util.Config;
import ets.genielog.appwatcher_3.profiling.util.PreferencesHelper;
import ets.genielog.appwatcher_3.profiling.util.Util;

/**
 * This is the IPC Service (InterProtocol Connection). It implements a
 * communicate mechanize between process with Unix socket.
 * 
 * @author eolwral
 * @author alexisdet
 */
public class IpcService {

	private static final String TAG = "IpcService - ";

	/** Singleton instance */
	private static IpcService instance = null;
	/** The current context */
	private Context ipcContext = null;

	/** Predefine socket name */
	private final static String socketName = "profiling_v1";
	/** Predefine send buffer size */
	private final static int sendBufferSize = 131072; /* 128K */
	/** Predefine received buffer size */
	private final static int recvBufferSize = 1048576; /* 1M */

	/** Unix client socket */
	private LocalSocket clientSocket = null;
	/** Unix socket address */
	private LocalSocketAddress clientAddress = null;

	/** Comparator for using time stamp */
	private class QueuedComparator implements Comparator<QueuedTask> {
		@Override
		public int compare(QueuedTask lhs, QueuedTask rhs) {
			if (lhs.timestamp > rhs.timestamp)
				return 1;
			else if (lhs.timestamp < rhs.timestamp)
				return -1;
			return 0;
		}
	}

	/** A queue for IPC command */
	private static PriorityQueue<QueuedTask> cmdQueue = null;

	/** Exclusive lock for cmdQueue */
	private final Semaphore cmdQueueLock = new Semaphore(1, true);

	/** This class defines a single task */
	private class QueuedTask {
		public ipcAction[] action = null; // the action to perfom
		public long timestamp = 0; // the timestamp when the request was pulled
		public ipcClientListener listener = null; // the client listenner to
													// which send the callback
		public int pid = -1; // the PID of the process to profile. -1 if no
								// process is required
		public boolean loop = false; // if the librairie should loop on this
										// action
		public ipcMessage result = null; // the request's result

		/**
		 * Constructor
		 * 
		 * @param action
		 *            - the {@link ipcAction}
		 * @param pid
		 *            - a process pid or -1
		 * @param loop
		 *            - whether this action should be repeated
		 * @param timestamp
		 *            - the timestamp at which the request was pulled
		 * @param listener
		 *            - the client listener to which send the callback
		 */
		QueuedTask(ipcAction[] action, int pid, boolean loop, long timestamp,
				ipcClientListener listener) {
			this.action = action;
			this.pid = pid;
			this.loop = loop;
			this.timestamp = timestamp;
			this.listener = listener;
		}

		/**
		 * Replace equals that will only check listener
		 */
		@Override
		public boolean equals(Object object) {
			if (object instanceof QueuedTask) {
				QueuedTask compareObj = (QueuedTask) object;
				if (compareObj.listener == this.listener)
					return true;
			}
			return false;
		}

	}

	/**
	 * Callback interface for ipcClient
	 */
	public interface ipcClientListener {
		/**
		 * This method defines a callback triggered when the data is retrieved
		 * from the core service (define in core.cc, see jni)
		 * 
		 * @param result
		 *            - the {@link ipcMessage} containing the values
		 */
		public void onRecvData(ipcMessage result);
	}

	/**
	 * Background worker for IpcService
	 */
	private static ipcTask worker = null;

	/**
	 * Initialize IpcService
	 * 
	 * @param context
	 */
	public static void Initialize(Context context) {
		if (instance == null) {
			instance = new IpcService(socketName);
			instance.ipcContext = context;
		}
	}

	/**
	 * Get a instance for IpcService [avoid duplicated connections]
	 * 
	 * @return IpcService
	 */
	public static IpcService getInstance() {
		return instance;
	}

	/**
	 * Internal use only for creating object
	 * 
	 * @param serverName
	 *            - the server name
	 */
	private IpcService(String serverName) {
		clientAddress = new LocalSocketAddress(serverName,
				LocalSocketAddress.Namespace.ABSTRACT);

		// prepare a priority queue
		QueuedComparator cmdComparator = new QueuedComparator();
		cmdQueue = new PriorityQueue<QueuedTask>(1, cmdComparator);

		// launch worker
		if (worker == null) {
			worker = new ipcTask();
			worker.execute();
		}
	}

	/**
	 * Destructor for ipcClient
	 */
	protected void finalize() {
		try {
			clientSocket.shutdownInput();
			clientSocket.shutdownOutput();
			clientSocket.close();
			clientSocket = null;
			clientAddress = null;
		} catch (IOException e) {
			Log.d(Config.TAG_APP, TAG + e.getMessage());
		}
	}

	/**
	 * Connection to libprofoling
	 * 
	 * @return true == success, false == fail
	 */
	private boolean connect() {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "connect");
		try {
			clientSocket = new LocalSocket();
			clientSocket.connect(clientAddress);
			clientSocket.setSendBufferSize(sendBufferSize);
			clientSocket.setReceiveBufferSize(recvBufferSize);

			// Notice: the value is milliseconds
			clientSocket.setSoTimeout(2 * 1000);

			// send token
			OutputStream outData = clientSocket.getOutputStream();
			byte[] outToken = PreferencesHelper.getInstance(ipcContext)
					.getToken().getBytes();
			outData.write(outToken);

		} catch (IOException e) {
			Log.e(Config.TAG_APP, TAG + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Check daemon status
	 * 
	 * @return true == alive, false == dead
	 */
	private boolean checkStatus() {
		if (clientSocket == null)
			return false;
		return clientSocket.isConnected();
	}

	/**
	 * Restart the daemon
	 * 
	 * @param true == success, false == failed
	 */
	private boolean restartDaemon() {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "restartDaemon");
		return Util.execCore(ipcContext);
	}

	/**
	 * Start to connect
	 */
	public boolean forceConnect() {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "forceConnect");
		if (restartDaemon()) {
			waitTime(1);
			return connect();
		}
		return false;
	}

	/**
	 * Send a exit command
	 */
	public void exit() {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "exit");
		if (!checkStatus())
			return;

		ipcMessage.Builder exitCommand = ipcMessage.newBuilder();
		exitCommand.setType(ipcMessage.ipcType.EXIT);

		// send
		try {
			OutputStream outData = clientSocket.getOutputStream();
			exitCommand.build().writeTo(outData);
		} catch (IOException e) {
		}

		return;
	}

	/**
	 * Kill processes
	 * 
	 * @param pid
	 */
	public void killProcess(int pid) {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "killProcess");
		if (!checkStatus())
			return;

		ipcMessage.Builder setCommand = ipcMessage.newBuilder();
		setCommand.setType(ipcMessage.ipcType.COMMAND);

		Builder data = setCommand.addDataBuilder();
		data.setAction(ipcAction.KILLPROCESS);
		String pidData = "" + pid;
		data.addPayload(ByteString.copyFrom(pidData.getBytes()));

		// send
		try {
			OutputStream outData = clientSocket.getOutputStream();
			setCommand.build().writeTo(outData);
		} catch (IOException e) {
		}

		return;
	}

	/**
	 * Add a request to queue
	 * 
	 * @param action
	 *            request actions which is a array
	 * @param pid
	 *            process to profile, -1 if no process needed
	 * @param sec
	 *            how long before execute the request
	 * @param loop
	 *            whether this action should be repeated
	 * @param obj
	 *            callback when request done
	 * @return true == success, false == fail
	 */
	public boolean addRequest(ipcAction[] action, int pid, int sec,
			boolean loop, ipcClientListener obj) {
		// check worker
		if (worker == null) {
			worker = new ipcTask();
			worker.execute();
		}

		// add a task into queue
		sec = (1000 * sec);
		long timestamp = System.currentTimeMillis() + sec;
		try {
			cmdQueueLock.acquire();

			QueuedTask newTask = new QueuedTask(action, pid, loop, timestamp,
					obj);
			if (!cmdQueue.contains(newTask))
				cmdQueue.add(newTask);

			cmdQueueLock.release();
		} catch (InterruptedException e) {
			Log.d(Config.TAG_APP, TAG + e.getMessage());
		}
		return true;
	}

	/**
	 * Search specific listener and remove its requests from queue
	 * 
	 * @param listener
	 */
	public void removeRequest(ipcClientListener listener) {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "removeRequest");
		try {
			QueuedTask checkObj = new QueuedTask(null, -1, false, 0, listener);
			cmdQueueLock.acquire();
			cmdQueue.remove(checkObj);
			cmdQueueLock.release();
		} catch (InterruptedException e) {
			Log.d(Config.TAG_APP, TAG + e.getMessage());
		}
	}

	/**
	 * Remove all requests from queue
	 */
	public void removeAllRequest() {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "removeAllRequest");
		try {
			cmdQueueLock.acquire();
			cmdQueue.clear();
			cmdQueueLock.release();
		} catch (InterruptedException e) {
			Log.d(Config.TAG_APP, TAG + e.getMessage());
		}
	}

	/**
	 * Disconnect with daemon
	 */
	public void disconnect() {
		if (Config.DEBUG_PROFILING)
			Log.d(Config.TAG_APP, TAG + "disconnect");
		if (clientSocket == null)
			return;

		removeAllRequest();

		try {
			clientSocket.shutdownOutput();
			clientSocket.shutdownInput();
			clientSocket.close();
			clientSocket = null;
		} catch (Exception e) {
			Log.d(Config.TAG_APP, TAG + e.getMessage());
		}
		return;
	}

	/**
	 * Wait for specific seconds
	 * 
	 * @param seconds
	 */
	private void waitTime(int sec) {
		try {
			// sleep
			Thread.sleep(1000 * sec);
		} catch (InterruptedException e) {
		}
	}

	/** AsyncTask managing the request */
	private class ipcTask extends AsyncTask<Void, QueuedTask, Void> {

		/** Internal receive buffer */
		private byte[] buffer = new byte[recvBufferSize];
		private int curBufferSize = recvBufferSize;

		/** prepare the ipc service */
		private boolean prepareIpc() {
			// check connection's status
			if (!checkStatus()) {
				restartDaemon();
				if (!connect())
					return false;
			}
			return true;
		}

		/**
		 * Background worker for IPC communication
		 */
		@Override
		protected Void doInBackground(Void... params) {
			QueuedTask job = null;

			while (true) {

				// check connection's status
				if (!prepareIpc()) {
					waitTime(1);
					continue;
				}

				// search jobs
				try {
					cmdQueueLock.acquire();
					job = cmdQueue.peek();
					cmdQueueLock.release();
				} catch (InterruptedException e) {
					Log.d(Config.TAG_APP, TAG + e.getMessage());
				}

				// if no jobs, just wait
				if (job == null) {
					waitTime(1);
					continue;
				}

				// if job isn't ready to go , just wait
				if (job.timestamp > System.currentTimeMillis()) {
					waitTime(1);
					continue;
				}

				// process job
				try {
					cmdQueueLock.acquire();
					job = cmdQueue.poll();
					cmdQueueLock.release();
				} catch (InterruptedException e) {
					Log.d(Config.TAG_APP, TAG + e.getMessage());
				}

				// if no jobs, just wait
				if (job == null) {
					waitTime(1);
					continue;
				}

				try {
					job.result = sendMessage(job);
				} catch (Exception e) {
					Log.d(Config.TAG_APP, TAG + e.getMessage());
				}

				// if result is empty, just disconnect
				if (job.result == null)
					disconnect();

				publishProgress(job);

				job = null;
			}
		}

		/**
		 * Send result to the requester
		 * 
		 * @param job
		 *            the job has been finished
		 */
		protected void onProgressUpdate(QueuedTask... job) {

			if (job.length == 0)
				return;

			QueuedTask procJob = job[job.length - 1];

			procJob.listener.onRecvData(procJob.result);
			procJob.listener = null;
			procJob.action = null;
			procJob.result = null;
		}

		/**
		 * send request to libprofiling and get data when data is ready
		 * 
		 * @param job
		 *            - the new job
		 * @return result the new data
		 * @throws Exception
		 */
		private ipcMessage sendMessage(QueuedTask job) throws Exception {
			ipcMessage result = null;
			OutputStream outData = null;
			InputStream inData = null;

			// prepare ipcMessage
			ipcMessage.Builder ipcmsg = ipcMessage.newBuilder();
			ipcmsg.setType(ipcMessage.ipcType.ACTION);
			for (int index = 0; index < job.action.length; index++) {
				ipcData.Builder data = ipcData.newBuilder();
				data.setAction(job.action[index]);

				String pidData = "" + job.pid;
				String loopData;
				if (job.loop)
					loopData = "1";
				else
					loopData = "0";
				data.addPayload(ByteString.copyFrom(pidData.getBytes()));
				data.addPayload(ByteString.copyFrom(loopData.getBytes()));

				ipcmsg.addData(data);
			}

			// send message and wait result
			// send
			try {
				outData = clientSocket.getOutputStream();
			} catch (IOException e) {
				Log.d(Config.TAG_APP, TAG + e.getMessage());
			}

			try {
				ipcMessage msg = ipcmsg.build();
				msg.writeTo(outData);
			} catch (IOException e) {
				Log.d(Config.TAG_APP, TAG + e.getMessage());
			}

			// receive (blocking mode)
			try {
				inData = clientSocket.getInputStream();
			} catch (IOException e1) {
				Log.d(Config.TAG_APP, TAG + e1.getMessage());
			}

			int totalSize = 0;

			// read data size
			if (inData.read(buffer, 0, 4) == 0)
				throw new IOException("Unable to get transfer size");

			// convert byte to int
			totalSize = (int) buffer[0] & 0xFF;
			totalSize |= (int) (buffer[1] & 0xFF) << 8;
			totalSize |= (int) (buffer[2] & 0xFF) << 16;
			totalSize |= (int) (buffer[3] & 0xFF) << 24;

			// check limit (10M)
			if (totalSize > recvBufferSize * 10)
				throw new Exception("Excced memory limit");

			// prepare enough buffer size
			if (curBufferSize < totalSize) {
				buffer = new byte[totalSize];
				curBufferSize = totalSize;
			}

			// receive data
			int transferSize = 0;
			while (transferSize != totalSize)
				transferSize += inData.read(buffer, transferSize, totalSize
						- transferSize);

			// convert to ipcMessage
			ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer,
					0, totalSize + 0);

			try {
				result = ipcMessage.parseFrom(byteStream);
			} catch (IOException e) {
				Log.d(Config.TAG_APP, TAG + e.getMessage());
				result = null;
			}

			byteStream.close();
			byteStream = null;

			return result;
		}
	}

}
