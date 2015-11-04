/*
 * Copyright (c) 2012 Xilinx, Inc.  All rights reserved.
 *
 * Xilinx, Inc.
 * XILINX IS PROVIDING THIS DESIGN, CODE, OR INFORMATION "AS IS" AS A
 * COURTESY TO YOU.  BY PROVIDING THIS DESIGN, CODE, OR INFORMATION AS
 * ONE POSSIBLE   IMPLEMENTATION OF THIS FEATURE, APPLICATION OR
 * STANDARD, XILINX IS MAKING NO REPRESENTATION THAT THIS IMPLEMENTATION
 * IS FREE FROM ANY CLAIMS OF INFRINGEMENT, AND YOU ARE RESPONSIBLE
 * FOR OBTAINING ANY RIGHTS YOU MAY REQUIRE FOR YOUR IMPLEMENTATION.
 * XILINX EXPRESSLY DISCLAIMS ANY WARRANTY WHATSOEVER WITH RESPECT TO
 * THE ADEQUACY OF THE IMPLEMENTATION, INCLUDING BUT NOT LIMITED TO
 * ANY WARRANTIES OR REPRESENTATIONS THAT THIS IMPLEMENTATION IS FREE
 * FROM CLAIMS OF INFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */

#include <stdio.h>
#include "car_process.h"
#include "tcp_trans.h"
int process_test(){

}

int main()
{
    printf("Hello World\n");
    car_tcp_struct car_tcp;
    car_tcp.PORT=8001;
#ifndef PC_RUN_DEBUG
    startup_init();
    car_device_info dinfo;
    dinfo.device_id=1;
    dinfo.param[0]=DTYPE_CAM;
    dinfo.param[1]=0;
  //  cam_init(dinfo);
#else

#endif
    /*
    int adxl_fd=open("/dev/i2c-0",O_RDWR);
    uchar val[6];
    get_i2c_register(adxl_fd,0x1D,0x1D,0x32,val,6);*/

/*
    HMC_Info *hmc_i1=(HMC_Info*)malloc(sizeof(HMC_Info));
    hmc_i1->fd=open("/dev/i2c-0",O_RDWR);
    if(hmc_i1->fd>=0){printf("i2c-0 opened\n");};
    hmc_i1->level=5;
    set_HMC_Message(*hmc_i1);
    get_HMC_Direction(*hmc_i1);

    HMC_Info *hmc_i2=(HMC_Info*)malloc(sizeof(HMC_Info));
    hmc_i2->fd=open("/dev/i2c-1",O_RDWR);
    if(hmc_i2->fd>=0){printf("i2c-1 opened\n");};
    hmc_i2->level=5;
    set_HMC_Message(*hmc_i2);
    get_HMC_Direction(*hmc_i2);

    HMC_Info *hmc_i3=(HMC_Info*)malloc(sizeof(HMC_Info));
    hmc_i3->fd=open("/dev/i2c-2",O_RDWR);
    if(hmc_i3->fd>=0){printf("i2c-2 opened\n");};
    hmc_i3->level=5;
    set_HMC_Message(*hmc_i3);
    get_HMC_Direction(*hmc_i3);
   // car_process_init();
*/
    //tcp_ser_thread_init(&car_tcp);
   // img_process();
    start_all_process();
    while(1){
        sleep(1);
    }
    /*
    while(1){

    	tcp_ser_thread(&car_tcp,car_process);
    }*/
    return 0;
}
