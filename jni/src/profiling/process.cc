/**
 * @file process.cc
 * @brief  Process Class file
 */

#include "process.h"

#include <android/log.h>

#define APPNAME "Appwatcher_3"

namespace ets {
namespace genielog {
namespace appwatcher_3 {
namespace profiling {
namespace core {

void process::set_pid(int pid) {
	this->pid = pid;
}

process::~process() {

	// clean up _PrevProcessList
	this->clearDataSet(
			(std::vector<google::protobuf::Message*>&) this->_PrevProcessList);

	// clean up _CurProcessList
	this->clearDataSet(
			(std::vector<google::protobuf::Message*>&) this->_CurProcessList);
}

bool process::gatherProcesses() {

	// search /proc
	DIR *curDirectory = 0;
	curDirectory = opendir(SYS_PROC_DIR);
	if (curDirectory == 0)
		return (false);

	if (pid == -1) {
		// enter every process directory
		struct dirent *curDirecotryEntry = 0;
		while ((curDirecotryEntry = readdir(curDirectory)) != 0) {
			if (isdigit(curDirecotryEntry->d_name[0]) == false)
				continue;

			int curPID = atoi(curDirecotryEntry->d_name);
			if (curPID == 0)
				continue;

			processInfo* curProcessInfo = new processInfo();
			if (curProcessInfo == 0)
				continue;

			if (this->getProcessInfo(*curProcessInfo, curPID) == true)
				this->_CurProcessList.push_back(curProcessInfo);
			else
				delete curProcessInfo;
		}
	} else {
		// give the data for the given process
		processInfo* curProcessInfo = new processInfo();

		if (this->getProcessInfo(*curProcessInfo, pid) == true)
			this->_CurProcessList.push_back(curProcessInfo);
		else
			delete curProcessInfo;
	}

	closedir(curDirectory);

	// refresh CPU usage
	this->_curCPUInfo.refreshGlobal();

	return (true);
}

bool process::getProcessInfo(processInfo& curProcessInfo, unsigned int pid) {
	// initialize
	curProcessInfo.set_uid(0);
	curProcessInfo.set_pid(0);
	curProcessInfo.set_prioritylevel(0);
	curProcessInfo.set_cpuusage(0);
	curProcessInfo.set_cputime(0);
	curProcessInfo.set_usedsystemtime(0);
	curProcessInfo.set_usedusertime(0);
	curProcessInfo.set_rss(0);

	// save pid
	curProcessInfo.set_pid(pid);

	// get UID
	char statProc[BUFFERSIZE];
	struct stat statInfo;
	// memory allocation for statProc
	memset(statProc, 0, BUFFERSIZE);
	// set the process path with the pid : /proc/[pid]
	// add it into the statProc table
	snprintf(statProc, BUFFERSIZE, SYS_PROC_LOC, pid);

	if (stat(statProc, &statInfo) == -1)
		return (false);

	curProcessInfo.set_uid(statInfo.st_uid);

	// get the user, the system time, and the RSS (memory value)
	snprintf(statProc, BUFFERSIZE, SYS_PROC_STAT, pid);
	FILE* psFile = fopen(statProc, "r");
	unsigned long rss = 0;
	if (psFile != 0) {
		unsigned long usedUserTime = 0;
		unsigned long usedSystemTime = 0;

		fscanf(psFile, SYS_PROC_PATTERN, &usedUserTime, &usedSystemTime, &rss);

		curProcessInfo.set_usedusertime(usedUserTime);
		curProcessInfo.set_usedsystemtime(usedSystemTime);

		if (rss > 0)
			curProcessInfo.set_rss(rss * 4);

		fclose(psFile);
		psFile = 0;
	}

	// get command line (process name)
	snprintf(statProc, BUFFERSIZE, SYS_PROC_CMD, pid);
	psFile = fopen(statProc, "r");
	if (psFile != 0) {
		char cmdLine[BUFFERSIZE];
		int readSize = 0;
		memset(cmdLine, 0, BUFFERSIZE);
		readSize = fread(cmdLine, 1, BUFFERSIZE, psFile);
		fclose(psFile);

		cmdLine[BUFFERSIZE - 1] = '\0';

		if (readSize != 0)
			curProcessInfo.set_name(cmdLine);

		fclose(psFile);
		psFile = 0;
	}

	// if we couldn't get data from cmdline, try to get from stat
	if (curProcessInfo.name().size() == 0) {
		snprintf(statProc, BUFFERSIZE, SYS_PROC_STAT, pid);
		psFile = fopen(statProc, "r");
		if (psFile != 0) {
			char cmdLine[BUFFERSIZE];
			int matchItem = 0;
			memset(cmdLine, 0, BUFFERSIZE);

			// restrict maximum chars is 255, it could prevent security warning
			matchItem = fscanf(psFile, SYS_PROC_BIN, &pid, cmdLine);
			fclose(psFile);

			if (matchItem == 2) {
				cmdLine[BUFFERSIZE - 1] = '\0';

				// remove ')'
				if (cmdLine[strlen(cmdLine) - 1] == ')')
					cmdLine[strlen(cmdLine) - 1] = '\0';

				curProcessInfo.set_name(cmdLine);

			}
			fclose(psFile);
			psFile = 0;
		}
	}

	/*
	 // Then get the PSS
	 snprintf(statProc, BUFFERSIZE, SYS_PROC_SMAPS, pid);
	 psFile = fopen(statProc, "r");
	 if (psFile != 0) {
	 std::ifstream infile(statProc);
	 std::string line;
	 char* search = "Pss";
	 unsigned long pss = 0;
	 while (std::getline(infile, line)) {
	 if (line.find(search) != std::string::npos) {
	 std::locale loc;
	 int year;
	 if (isdigit(line[0], loc)) {
	 std::stringstream(line) >> year;
	 }
	 pss = pss + year;
	 }
	 }
	 __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
	 "process %d - pss  %lu - rss %lu", pid, pss, rss);
	 }
	 fclose(psFile);
	 psFile = 0;
	 */

// get priority
	curProcessInfo.set_prioritylevel(getpriority(PRIO_PROCESS, pid));

// get CPU time
	unsigned long CPUTimeJiffies = (curProcessInfo.usedsystemtime()
			+ curProcessInfo.usedusertime());
	if (CPUTimeJiffies > 0)
		curProcessInfo.set_cputime(CPUTimeJiffies / HZ);

	return (true);
}

std::vector<std::string> process::split(const std::string &s, char delim) {
	std::vector < std::string > elems;
	std::stringstream ss(s);
	std::string item;
	while (std::getline(ss, item, delim)) {
		elems.push_back(item);
	}
	return elems;
}

void process::calcuateCPUUsage() {
// check 2 lists is ready to calculate
	if (this->_CurProcessList.size() == 0 || this->_PrevProcessList.size() == 0)
		return;

// search for match PID and summary all CPUTime (Remove it for reducing CPU consume)
	unsigned long curCPUTime = 0;
	for (int curItem = 0; curItem < this->_CurProcessList.size(); curItem++) {
		for (int prevItem = 0; prevItem < this->_PrevProcessList.size();
				prevItem++) {
			if (this->_CurProcessList[curItem]->pid()
					== this->_PrevProcessList[prevItem]->pid()) {
				curCPUTime += this->_CurProcessList[curItem]->usedsystemtime()
						- this->_PrevProcessList[prevItem]->usedsystemtime();
				curCPUTime += this->_CurProcessList[curItem]->usedusertime()
						- this->_PrevProcessList[prevItem]->usedusertime();

				prevItem = this->_PrevProcessList.size();
			}
		}
	}

	if (curCPUTime < _curCPUInfo.getCPUTime())
		curCPUTime = (float) _curCPUInfo.getCPUTime();
	if (curCPUTime == 0)
		return;

// calculate load for each process
	for (int curItem = 0; curItem < this->_CurProcessList.size(); curItem++) {
		for (int prevItem = 0; prevItem < this->_PrevProcessList.size();
				prevItem++) {
			if (this->_CurProcessList[curItem]->pid()
					== this->_PrevProcessList[prevItem]->pid()) {
				unsigned long procCPUTime = 0;
				procCPUTime += this->_CurProcessList[curItem]->usedsystemtime()
						- this->_PrevProcessList[prevItem]->usedsystemtime();
				procCPUTime += this->_CurProcessList[curItem]->usedusertime()
						- this->_PrevProcessList[prevItem]->usedusertime();

				if (procCPUTime != 0)
					this->_CurProcessList[curItem]->set_cpuusage(
							(float) procCPUTime * 100 / curCPUTime);

				// check upper and bottom limit - if exceed, put the value we had before
				if (this->_CurProcessList[curItem]->cpuusage() > 100
						|| this->_CurProcessList[curItem]->cpuusage() < 0)
					this->_CurProcessList[curItem]->set_cpuusage(
							this->_PrevProcessList[prevItem]->cpuusage());
			}
		}
	}
	return;
}

void process::refresh() {
	//__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
	//		"process refresh pid = %d!\n", this->pid);
// clean up
	this->clearDataSet(
			(std::vector<google::protobuf::Message*>&) this->_PrevProcessList);

// move current to previous
	this->moveDataSet(
			(std::vector<google::protobuf::Message*>&) this->_CurProcessList,
			(std::vector<google::protobuf::Message*>&) this->_PrevProcessList);

// gathering information
	if (this->gatherProcesses() == false)
		return;

// calculate CPU usage
	this->calcuateCPUUsage();

// refresh OS info
	this->_curOSInfo.refresh();

// save data to file (csv)
	this->writeToFile();

	return;
}

void process::writeToFile() {

	mkdir("/mnt/sdcard/AppWatcher_3/", 777);

	FILE * f;

	double cpuTotal = 0;
	char *freeMemory = NULL;
	char *totalMemory = NULL;
	char const *kindOfProcess = NULL; // N Native - A Android

	if (this->pid == -1) {

		f = fopen("/mnt/sdcard/AppWatcher_3/all_processes.csv", "a"); // w ou a

		if (!f)
			__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
					"can't open file!\n");

		// Set the header
		fprintf(f, "Native/Android, PID, CUP, RAM\n");

		for (int curItem = 0; curItem < this->_CurProcessList.size();
				curItem++) {

			// Get the king of process - Native or Android
			if (_CurProcessList[curItem]->uid() == 0
					|| _CurProcessList[curItem]->name().find("/system/")
							!= std::string::npos
					|| _CurProcessList[curItem]->name().find("/sbin/")
							!= std::string::npos) {
				kindOfProcess = "N";
			} else
				kindOfProcess = "A";

			// Write in the file
			fprintf(f, "%s, %d, %.2f, %s", kindOfProcess,
					_CurProcessList[curItem]->pid(),
					_CurProcessList[curItem]->cpuusage(),
					to_human_readable_byte_count(
							_CurProcessList[curItem]->rss() * 1024, true));
			fprintf(f, "\n");

			cpuTotal += _CurProcessList[curItem]->cpuusage();
		}

		std::vector<osInfo*> osDataList = _curOSInfo.getOSData();
		osInfo *osData = osDataList.back();

		freeMemory = to_human_readable_byte_count(
				osData->freememory() + osData->bufferedmemory()
						+ osData->cachedmemory(), true);
		totalMemory = to_human_readable_byte_count(osData->totalmemory(), true);

		fprintf(f, "TOTAL_CPU, FREE_RAM/TOTAL_RAM, NB_PROCESS\n");
		fprintf(f, "%.2f, %s/%s, %d", cpuTotal, freeMemory, totalMemory,
				_CurProcessList.size());
		fprintf(f, "\n");
	} else {

		for (int curItem = 0; curItem < this->_CurProcessList.size();
				curItem++) {

			char path[100];
			sprintf(path, "/mnt/sdcard/AppWatcher_3/process_%s.csv",
					_CurProcessList[curItem]->name().c_str());
			f = fopen(path, "a");

			if (!f)
				__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
						"can't open file!\n");

			fprintf(f, "%.2f %s", _CurProcessList[curItem]->cpuusage(),
					to_human_readable_byte_count(
							_CurProcessList[0]->rss() * 1024, true));
		}
	}
	fprintf(f, "\n");
	fclose(f);
}

char* process::to_human_readable_byte_count(long bytes, bool si) {
// Static lookup table of byte-based SI units
	static const char *suffix[][2] = { { "B", "B" }, { "kiB", "KB" }, { "MiB",
			"MB" }, { "GiB", "GB" }, { "TiB", "TB" }, { "EiB", "EB" }, { "ZiB",
			"ZB" }, { "YiB", "YB" } };
	int unit = si ? 1000 : 1024;
	int exp = 0;
	if (bytes > 0) {
		exp = std::min((int) (log(bytes) / log(unit)),
				(int) (sizeof(suffix) / sizeof(suffix[0]) - 1));
	}
	char* s = new char();
	//sprintf(s, "%.2f%s", bytes / pow(unit, exp), suffix[exp][!!si]);
	sprintf(s, "%.2f", bytes / pow(unit, exp));
	return s;
}

const std::vector<google::protobuf::Message*>& process::getData() {
	return ((const std::vector<google::protobuf::Message*>&) this->_CurProcessList);
}

}
}
}
}
}
