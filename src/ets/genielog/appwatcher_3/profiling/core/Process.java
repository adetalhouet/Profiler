package ets.genielog.appwatcher_3.profiling.core;

/**
 * This class gathers a process' data
 * 
 * @author alexisdet
 * 
 */
public class Process {

	/** The process name */
	private String name;
	/** The process id */
	private int pid;
	/** The process' memory usage */
	private String memory;
	/** The process' cpu usage */
	private String cpu;

	/**
	 * @param name
	 *            - process' name
	 * @param pid
	 *            - process' id
	 * @param memory
	 *            - process' memory usage
	 * @param cpu
	 *            - process' cpu usage
	 */
	public Process(String name, int pid, String memory, String cpu) {
		this.name = name;
		this.pid = pid;
		this.memory = memory;
		this.cpu = cpu;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            - the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * @param pid
	 *            - the pid to set
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @return the memory
	 */
	public String getMemory() {
		return memory;
	}

	/**
	 * @param memory
	 *            - the memory to set
	 */
	public void setMemory(String memory) {
		this.memory = memory;
	}

	/**
	 * @return the cpu
	 */
	public String getCpu() {
		return cpu;
	}

	/**
	 * @param cpu
	 *            - the cpu to set
	 */
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String toString() {
		return "" + pid + ";" + memory + ";" + cpu;
	}

}
