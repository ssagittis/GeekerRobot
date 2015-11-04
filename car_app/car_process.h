/*
 * car_process.h
 *
 *  Created on: 2015-5-25
 *      Author: lin
 */


#ifndef CAR_PROCESS_H_
#define CAR_PROCESS_H_
#include "i2c.h"
#include "tcp_trans.h"
#include "HMC.h"
#define sensor_table_size 10
#include "infotables.h"
void *get_device_info_struct(uchar device_id);
car_device_info HMC_Process(HMC_Info *hinfo);
car_tcp_package *car_process (car_tcp_package *rec_pack);
void car_process_init(car_tcp_package *rec_pack);
void car_process_Set(car_tcp_package *rec_pack);
car_tcp_package  car_send_process();
car_device_info HMC_process_Init(car_device_info *sinfo);
car_device_info HMC_Process_read(HMC_Info *hinfo);
void motor_direc_process_set(Steer_Info*minfo, car_device_info *setinfo);
void motor_drive_process_set(MOTOR_Drive_Info *minfo,car_device_info*sinfo);
void motor_direc_process_Init(car_device_info sinfo);
void motor_drive_process_Init(car_device_info sinfo);
void steer_arm_Init(car_device_info sinfo);
void steer_arm_Set(Steer_ARM_Info *dinfo,car_device_info *sinfo);
void ultra_sound_Init(car_device_info sinfo);
car_device_info ultra_sound_Read(ULTRA_Sound_Info* info);
void cam_init(car_device_info dinfo);
void cam_process(Cam_Info *cinfo);
void  roitracer_init(car_device_info sinfo);
car_device_info ROI_Tracer_Read(ROITRACER_Info*rinfo );
void device_using_rmv_all();
void *get_device_info_struct(uchar device_id);
std::vector<using_item>::iterator get_device_using_iterator(uchar device_id);
void   gpio_init(car_device_info sinfo);
#endif /* CAR_PROCESS_H_ */
