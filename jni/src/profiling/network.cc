/**
 * @file network.cc
 * @brief Network Class file
 */

#include "network.h"

namespace ets {
namespace genielog {
namespace appwatcher_3 {
namespace profiling {
namespace core {

network::~network() {
	// clean up
	this->clearDataSet(
			(std::vector<google::protobuf::Message*>&) this->_curNetworkList);
	this->clearDataSet(
			(std::vector<google::protobuf::Message*>&) this->_prevNetworkList);
}

void network::refresh() {
	// move current to previous
	this->moveDataSet(
			(std::vector<google::protobuf::Message*>&) this->_curNetworkList,
			(std::vector<google::protobuf::Message*>&) this->_prevNetworkList);

	// clean up
	this->clearDataSet(
			(std::vector<google::protobuf::Message*>&) this->_curNetworkList);

	// get base information
	this->getInterfaceStatistic();

	std::vector<networkInfo*>::iterator curIter = this->_curNetworkList.begin();
	while (curIter != this->_curNetworkList.end()) {
		// get MAC address
		// this->getMACInformation(*curIter);

		// get IPv4 address
		this->getIPv4Information(*curIter);

		// get traffic statistics
		//this->getTrafficInformation(*curIter);

		curIter++;
	}

	// get IPv6 address (for performance, we do it once)
	//this->getIPv6Information();

	// calculate IO utilization
	//this->calculateNetworkIO();

	return;
}

/*
 void network::calculateNetworkIO() {
 // check 2 lists is ready to calculate
 if (this->_curNetworkList.size() == 0 || this->_prevNetworkList.size() == 0)
 return;

 // search for match PID and summary all CPUTime (Remove it for reducing CPU consume)
 for (int curItem = 0; curItem < this->_curNetworkList.size(); curItem++) {
 for (int prevItem = 0; prevItem < this->_prevNetworkList.size();
 prevItem++) {
 if (strcmp(this->_prevNetworkList[prevItem]->name().c_str(),
 this->_curNetworkList[curItem]->name().c_str()) != 0)
 continue;

 this->_curNetworkList[curItem]->set_recvusage(
 this->_curNetworkList[curItem]->recvbytes()
 - this->_prevNetworkList[prevItem]->recvbytes());
 this->_curNetworkList[curItem]->set_transusage(
 this->_curNetworkList[curItem]->transbytes()
 - this->_prevNetworkList[prevItem]->transbytes());
 }
 }
 return;
 }
 */

void network::getInterfaceStatistic() {
	char buffer[BufferSize];

	memset(buffer, 0, BufferSize);
	snprintf(buffer, BufferSize, INT_IPV4_FILE, getpid());
	FILE *ifFile = fopen(buffer, "r");

	if (ifFile == 0)
		return;

	// skip 2 lines
	fgets(buffer, BufferSize, ifFile);
	fgets(buffer, BufferSize, ifFile);

	while (fgets(buffer, BufferSize, ifFile) != NULL) {
		char curName[BufferSize];

		networkInfo* curNetworkInfo = new networkInfo();

		memset(curName, 0, BufferSize);

		sscanf(buffer, INT_IPV4_PATTERN, curName);

		curNetworkInfo->set_name(curName);

		this->_curNetworkList.push_back(curNetworkInfo);
	}
	fclose(ifFile);
}

/*
 void network::getMACInformation(networkInfo* curNetworkInfo) {
 char buffer[BufferSize];
 char curMACAddr[BufferSize];
 int curMAC = 0;

 memset(buffer, 0, BufferSize);
 memset(curMACAddr, 0, BufferSize);

 snprintf(buffer, BufferSize, INT_MAC_FILE, curNetworkInfo->name().c_str());
 if ((curMAC = open(buffer, O_RDONLY)) > 0) {
 read(curMAC, curMACAddr, 17);
 close(curMAC);
 }

 if (strlen(curMACAddr) < 17)
 curMACAddr[0] = 0;

 curNetworkInfo->set_mac(curMACAddr);
 return;
 }
 */

/*
 void network::getIPv6Information() {
 char buffer[BufferSize];
 memset(buffer, 0, BufferSize);
 snprintf(buffer, BufferSize, INT_IPV6_FILE, getpid());

 FILE *ifFile = fopen(buffer, "r");
 if (ifFile == 0)
 return;

 //00000000000000000000000000000001 01 80 10 80       lo
 while (fgets(buffer, BufferSize, ifFile) != NULL) {
 int curNetmaskV6;
 struct in6_addr curIPv6;
 char curName[BufferSize];

 memset(&curIPv6, 0, sizeof(in6_addr));
 memset(curName, 0, BufferSize);

 int matchCounts = sscanf(buffer, INT_IPV6_PATTERN,
 (unsigned int*) &curIPv6.in6_u.u6_addr8[0],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[1],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[2],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[3],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[4],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[5],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[6],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[7],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[8],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[9],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[10],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[11],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[12],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[13],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[14],
 (unsigned int*) &curIPv6.in6_u.u6_addr8[15], &curNetmaskV6,
 curName);

 if (matchCounts == 18) {
 // search matched interface
 std::vector<networkInfo*>::iterator curIter =
 this->_curNetworkList.begin();
 while (curIter != this->_curNetworkList.end()) {
 char addrV6[INET6_ADDRSTRLEN];

 if ((*curIter)->name().compare(curName) != 0) {
 curIter++;
 continue;
 }

 memset(addrV6, 0, INET6_ADDRSTRLEN);
 inet_ntop(AF_INET6, &curIPv6, addrV6, INET6_ADDRSTRLEN);

 (*curIter)->set_ipv6addr(addrV6);
 (*curIter)->set_netmaskv6(curNetmaskV6);

 curIter++;
 }
 }
 }
 fclose(ifFile);
 }
 */

void network::getIPv4Information(networkInfo* curNetworkInfo) {
	char curIPv4[INET_ADDRSTRLEN];
	char curNetMaskv4[INET_ADDRSTRLEN];
	struct ifreq curIFREQ;
	int curSocket = 0;

	memset(curIPv4, 0, INET_ADDRSTRLEN);
	memset(curNetMaskv4, 0, INET_ADDRSTRLEN);
	memset(&curIFREQ, 0, sizeof(struct ifreq));
	strncpy(curIFREQ.ifr_name, curNetworkInfo->name().c_str(),
			strlen(curNetworkInfo->name().c_str()));

	if ((curSocket = socket(AF_INET, SOCK_DGRAM, 0)) >= 0) {
		if (ioctl(curSocket, SIOCGIFFLAGS, &curIFREQ) >= 0)
			curNetworkInfo->set_flags(curIFREQ.ifr_flags);

		close(curSocket);
	}
}

/*
 void network::getTrafficInformation(networkInfo* curNetworkInfo) {
 char buffer[BufferSize];
 char curTrafficData[BufferSize];
 int curTraffic = 0;

 if (curNetworkInfo->recvbytes() == 0) {
 memset(buffer, 0, BufferSize);
 memset(curTrafficData, 0, BufferSize);

 snprintf(buffer, BufferSize, INT_RX_FILE,
 curNetworkInfo->name().c_str());
 if ((curTraffic = open(buffer, O_RDONLY)) > 0) {
 read(curTraffic, curTrafficData, BufferSize);
 close(curTraffic);
 }

 curNetworkInfo->set_recvbytes(strtoul(curTrafficData, NULL, 0));
 }

 if (curNetworkInfo->transbytes() == 0) {
 memset(buffer, 0, BufferSize);
 memset(curTrafficData, 0, BufferSize);

 snprintf(buffer, BufferSize, INT_TX_FILE,
 curNetworkInfo->name().c_str());
 if ((curTraffic = open(buffer, O_RDONLY)) > 0) {
 read(curTraffic, curTrafficData, BufferSize);
 close(curTraffic);
 }

 curNetworkInfo->set_transbytes(strtoul(curTrafficData, NULL, 0));
 }
 }
 */
const std::vector<google::protobuf::Message*>& network::getData() {
	return ((const std::vector<google::protobuf::Message*>&) this->_curNetworkList);
}

}
}
}
}
}
