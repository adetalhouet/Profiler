/**
 * @file process.h
 * @brief Process Class header file
 */

#ifndef PROCESS_H_
#define PROCESS_H_

#include <stdio.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/resource.h>

#include <math.h>
#include <algorithm>
#include <vector>
#include <locale>

#include <iostream>
#include <fstream>
#include <sstream>
#include <string>

#include "base.h"
#include "cpu.h"
#include "os.h"
#include "processInfo.pb.h"

#include <jni.h>

#define HZ 100
#define SYS_PROC_DIR "/proc"
#define SYS_PROC_LOC "/proc/%d"
#define SYS_PROC_CMD "/proc/%d/cmdline"
#define SYS_PROC_STAT "/proc/%d/stat"
#define SYS_PROC_SMAPS "/proc/%d/smaps"

#define SYS_PROC_BIN "%d (%255s)"
#define SYS_PROC_PATTERN "%*d %*s %*c %*d %*d %*d %*d %*d %*d %*d %*d %*d %*d %lu %lu %*d %*d %*d %*d %*d %*u %*u %*u %lu"
#define SYS_PROC_SMAPS_PATTERN "%*d %lu %lu %*d %*d %*d %*d %*d %*d %*d %*d %*d %*d"

namespace ets {
namespace genielog {
namespace appwatcher_3 {
namespace profiling {
namespace core {

/**
 * @class process
 * @brief offer process information functions
 */
class process: ets::genielog::appwatcher_3::profiling::core::base {
private:

	/** buffer size */
	const static unsigned int BUFFERSIZE = 256;

	/** process id if provided */
	unsigned int pid;

	/** internal previous process list  */
	std::vector<processInfo*> _PrevProcessList;
	/** internal current process list */
	std::vector<processInfo*> _CurProcessList;

	/** internal CPU usage */
	cpu _curCPUInfo;
	/** internal OS usage */
	os _curOSInfo;

	/**
	 * gather process information for /proc directory
	 * @return successes or fail
	 */
	bool gatherProcesses();

	/**
	 * get specific process information by PID
	 * @param curProcessInfo process information object
	 * @param pid target process
	 * @return true == success, false == fail
	 */
	bool getProcessInfo(processInfo& curProcessInfo, unsigned int pid);

	/**
	 * calculate CPU usage for each process
	 */
	void calcuateCPUUsage();

	char* to_human_readable_byte_count(long bytes, bool si);

	void writeToFile();

	std::vector<std::string> split(const std::string &s, char delim);

public:

	/**
	 * setter for pid
	 */
	void set_pid(int pid);

	/**
	 * destructor for Process
	 */
	~process();

	/**
	 * get process list
	 * @return a ProcessInfo list for running process
	 */
	const std::vector<google::protobuf::Message*>& getData();

	/**
	 * refresh process information
	 * @return true == data is ready to be read, false == not ready
	 */
	void refresh();
};

}
}
}
}
}

#endif /* PROCESS_H_ */
