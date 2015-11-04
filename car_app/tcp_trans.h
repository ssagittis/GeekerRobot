/*
 * tcp_trans.h
 *
 *  Created on: 2015-5-22
 *      Author: lin
 */

#ifndef TCP_TRANS_H_
#define TCP_TRANS_H_
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<unistd.h>
#include<netinet/in.h>
#include <errno.h>
#include "infotables.h"
//#define PORT 6666
typedef struct{
	int ser_sockfd;
	int cli_sockfd;
	struct sockaddr_in ser_addr;
	struct sockaddr_in cli_addr;
	long PORT;
}car_tcp_struct;

int  tcp_ser_thread_init(car_tcp_struct *tcp_str);
int tcp_ser_thread(car_tcp_struct *tcp_str,car_tcp_package *(*car_process)(car_tcp_package *rec_pack));

void *img_process(void *arg);
void start_all_process();

#endif /* TCP_TRANS_H_ */
