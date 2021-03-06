/**
 * @file core.cc
 * @brief Core library for Profiling
 *
 *  Main core program
 */

// Linux
#include <string.h>
#include <unistd.h>

#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/resource.h>

// stl
#include <vector>

// Android
#include <android/log.h>
#define APPNAME "Appwatcher_3"

// Profiling library
#include <os.h>
#include <process.h>
#include <network.h>

// ProfilingIPC library
#include <ipcserver.h>

#define BufferSize 256

// using name space
using namespace ets::genielog::appwatcher_3::profiling;

// global variables
static ipc::ipcserver server;
static ipc::ipcMessage command;

// system object
static std::vector<core::base *> adapter;

// for cache mechanize
struct cachedData {
	int id;
	int time;
	ipc::ipcData data;
};

static std::vector<cachedData *> storage;

// buffer
bool endLoop = false;
bool loop = true;
static char* buffer = NULL;
static int bufferSize = 0;

bool prepareIPC() {
	// initialize
	if (!server.init(SOCKETNAME)) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"core - can't initialize socket!\n");
		return (false);
	}

	// prepare socket
	if (!server.bind()) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"core - can't bind socket!\n");
		return (false);
	}

	return (true);
}

bool prepareBuffer(int requireSize) {
	// check size
	if (requireSize > bufferSize) {
		// reallocate buffer
		if (buffer != NULL)
			delete buffer;
		buffer = new char[requireSize];

		// check buffer
		if (buffer == NULL)
			bufferSize = 0;
		else
			bufferSize = requireSize;
	}

	// valid buffer
	if (bufferSize == 0)
		return (false);

	// clean up
	memset(buffer, 0, bufferSize);
	return (true);
}

bool checkToken() {
	// check token
	if (server.isVerified())
		return (true);

	server.checkToken();
	return (false);
}

bool receiveCMD() {
	if (!prepareBuffer(1024))
		return (false);

	// clean up
	command.Clear();

	int recvSize = 0;
	if (!server.receieve(buffer, bufferSize, recvSize)) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"Core - can't receive data!\n");
		return (false);
	}

	if (!command.ParseFromArray(buffer, recvSize)) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"Core - can't parse data!\n");
		return (false);
	}
	return (true);
}

bool sendData(ipc::ipcMessage& result) {
	if (result.has_type() == false) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"Core - action type is empty!\n");
		return (false);
	}

	if (!prepareBuffer(result.ByteSize())) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"Core - can't prepare send buffer!\n");
		return (false);
	}

	if (!result.SerializeToArray(buffer, result.GetCachedSize())) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"Core - can't serialize!\n");
		return (false);
	}

	if (!server.send(buffer, result.GetCachedSize())) {
		__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
				"Core - can't send data!\n");
		return (false);
	}
	return (true);
}

bool fillPayload(const std::vector<google::protobuf::Message *>& objList,
		ipc::ipcData& result) {
	// put data into payload
	for (int ptr = 0; ptr < objList.size(); ptr++)
		result.add_payload(objList[ptr]->SerializeAsString());

	return (true);
}

void initAdapter() {
	// initial the array
	for (int i = 0; i < ipc::ipcAction_MAX + 1; i++) {
		adapter.push_back(NULL);
		storage.push_back(NULL);
	}
}

bool prepareAdapter(ipc::ipcAction action, unsigned int pid) {
	// set the pid if process object already exist
	if (action == ipc::PROCESS && adapter[action] != NULL) {
		((core::process *) adapter[action])->set_pid(pid);
	}
	// check
	if (adapter[action] != NULL)
		return (true);

	// prepare
	switch (action) {
	case ipc::OS:
		adapter[ipc::OS] = (core::base *) new core::os();
		break;
	case ipc::PROCESS:
		adapter[ipc::PROCESS] = (core::base *) new core::process();
		((core::process *) adapter[action])->set_pid(pid);
		break;
	case ipc::CPU:
		adapter[ipc::CPU] = (core::base *) new core::cpu();
		break;
	case ipc::NETWORK:
		adapter[ipc::NETWORK] = (core::base *) new core::network();
		break;
	default:
		return (false);
	}
	return (true);
}

void cleanUp() {
	// close all clients
	server.clean();

	// remove and empty all data
	for (int index = 0; index < ipc::ipcAction_MAX + 1; index++) {
		if (adapter[index] != NULL)
			delete adapter[index];
		if (storage[index] != NULL)
			delete storage[index];
	}
	return;
}

bool processActionMsg() {

	// current result
	ipc::ipcMessage result;
	bool flag = true;
	int pid = -1;

	//prepare ipcMessage
	result.Clear();

	// result
	result.set_type(ipc::ipcMessage::RESULT);

	// process ACTION message
	for (int index = 0; index < command.data_size(); index++) {

		// get data
		ipc::ipcData data = command.data(index);

		// get the pid for the ipcAction Process in
		// order to retrieve the expected data
		if (data.action() == ipc::PROCESS) {
			pid = atoi(data.payload(0).c_str());
		}
		// get if we have to loop on this action or not
		loop = atoi(data.payload(1).c_str());

		// check cache status and use cached data
		if (storage[data.action()] != NULL) {
			if (storage[data.action()]->id != server.getClientId()
					&& storage[data.action()]->time > (time(NULL) - 3)) {
				ipc::ipcData* newData = result.add_data();
				newData->set_action(data.action());
				newData->CopyFrom(storage[data.action()]->data);
				continue;
			}
		}

		// prepare
		if (!prepareAdapter(data.action(), pid)) {
			flag = false;
			break;
		}

		// refresh
		(adapter[data.action()])->refresh();

		// get list
		const std::vector<google::protobuf::Message *>& objList =
				adapter[data.action()]->getData();

		// add a new data
		ipc::ipcData* newData = result.add_data();

		// fill data
		newData->set_action(data.action());
		fillPayload(objList, *newData);

		// set cached data as empty
		if (storage[data.action()] != NULL) {
			delete storage[data.action()];
			storage[data.action()] = NULL;
		}

		// save data into cache storage
		cachedData *newCache = new cachedData();
		newCache->id = server.getClientId();
		newCache->data.CopyFrom(*newData);
		newCache->time = time(NULL);
		storage[data.action()] = newCache;
		newCache = NULL;
	}

	// clear up
	command.Clear();

	// send data
	if (!sendData(result)) {
		server.close();
		flag = false;
	}

	return (flag);
}

void processCommandMsg() {
	// process ACTION message
	for (int index = 0; index < command.data_size(); index++) {
		// get data
		ipc::ipcData data = command.data(index);

		// kill process
		if (data.action() == ipc::KILLPROCESS) {
			int pid = atoi(data.payload(0).c_str());
			kill(pid, SIGKILL);
			continue;
		}
	}
	// clear up
	command.Clear();
	return;
}

bool processCommand() {
	// check token
	if (checkToken() == false)
		return (true);

	// receive ipcMessage
	if (receiveCMD() == false)
		return (true);

	// process Message
	switch (command.type()) {
	// process EXIT message
	case ipc::ipcMessage::EXIT:
		endLoop = true;
		return (false);
		// process ACTION message
	case ipc::ipcMessage::ACTION:
		return (processActionMsg());
		// process COMMAND message
	case ipc::ipcMessage::COMMAND:
		processCommandMsg();
		return (true);
	}

	return (false);
}

int main(int argc, char* argv[]) {
	if (argc == 1)
		return (1);

	// prepare IPC
	if (!prepareIPC())
		return (2);

	initAdapter();

	// extract and erase Token
	server.extractToken(argv[1]);

	// receive commands
	endLoop = false;
	while (!endLoop) {
		ipc::ipcserver::EVENT event = server.poll();
		switch (event) {
		// error
		case ipc::ipcserver::ERROR:
			endLoop = true;
			break;
			// wait clients
		case ipc::ipcserver::WAIT:
			if (loop) {
				// do nothing - refresh data set
				__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
						"Core - refresh");
				adapter[ipc::PROCESS]->refresh();
			}
			break;
			// accept connections
		case ipc::ipcserver::CONNECTION:
			__android_log_print(ANDROID_LOG_VERBOSE, APPNAME,
					"core - connection");
			server.accept();
			break;
			// process command
		case ipc::ipcserver::COMMAND:
			processCommand();
			break;
		}
	}
// clean up
	cleanUp();

	return (0);
}

