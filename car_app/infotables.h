#ifndef INFOTABLES_H
#define INFOTABLES_H
#include <vector>
#include "roi_tracer_class.h"
#include <sys/mman.h>
#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>
//#include "csapp.h"
#include <sys/stat.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

//#define PC_RUN_DEBUG
void startup_init();//this function should work when startup to initialize table infos
extern int img_out_cam_id;//img out cam
typedef unsigned char uchar;
typedef enum{
    DEVICE_Set,DEVICE_Init,DEVICE_Func
}car_instructions;
typedef struct{
    char device_id;
    int param[8];
}car_device_info;
/*
typedef struct{
    char device_id;    
    long param[8];
}car_device_info_Set ;//struct received from App,and send to each process
typedef struct{
    char device_id;
    device_type type;
    union{
    long param[7];
    };
}car_device_info_Init ;//struct received from App,and send to each process

union car_device_info{
    car_device_info_Set sinfo;
    car_device_info_Init iinfo;
};
typedef union {
    Init_Param_ADXL adxl;
    Init_Param_Cam cam;
    Init_Param_HMC hmc;
    Init_Param_MOTOR motor;
    long param[7];
}Init_Params_union;
*/
typedef struct{
    int header;
    int pack_size;
    int instruction;
    int device_num;
    car_device_info *device_info;
}car_tcp_package ;//tcp package

typedef enum{
    DTYPE_I2C,//0
    DTYPE_SPI,//1
    DTYPE_BITS,//2 only has one register to write
    DTYPE_HMC,//3
    DTYPE_MOTOR_Drive,//4
    DTYPE_MOTOR_Direc,//5
    DTYPE_ROITRACER,//6
    DTYPE_CAM,//7
    DTYPE_ADXL,//8
    DTYPE_IMG_OUT,//9
    DTYPE_STEER_ARM,//10
    DTYPE_ULTRAL_SOUND
}device_type;

typedef enum{
    ITYPE_I2C,//0
    ITYPE_BITS,//1
    ITYPE_MOTOR,//2
    ITYPE_ACTUAL,//3 duo ji
    ITYPE_CAM,//4
    ITYPE_VIRTUAL,//5
    ITYPE_ULTRA_SOUND//6
}interface_type;

typedef struct{
    unsigned int id;
    interface_type type;
    volatile long phy_addr;
    void *virt_addr;
    char *file_name;
    int fd;
}device_interface_item;
//some device has fd,initialized at system startup;some is conctroled by phy_addr,also mapped at system startup
//however USB devices is initialized at initializing process
typedef struct{
    unsigned int id;
    device_type type;
    device_interface_item *interface_struct;
    void *device_info_struct;
}using_item;

extern device_interface_item interface_table[20];

extern std::vector<using_item> device_using;

extern int mem_fd;//memory

//Camera definitons
typedef struct{
    int video_num;//video id
    void *V4LCam;
    void *Mat;//mat
}Cam_Info;
typedef struct{
    int camnum;
}Init_Param_Cam;

//HMC Definitions
typedef struct{
    int fd;
    char continu;
    uchar level;
    char * file;
}HMC_Info ;
typedef struct{
    int interface_id;
}Init_Param_HMC;
//ADXL Definitions
typedef struct{
    int fd;
    char * file;
}ADXL_Info ;
typedef struct{
    int interface_id;
    char *file;
}Init_Param_ADXL;
//MOTOR_Drive Definitions
typedef struct{
    int *addr;
    long speed;
}MOTOR_Drive_Info;
typedef struct{
    int interface_id;
}Init_Param_MOTOR;
//MOTOR_Direc Definitions
typedef struct{
    int *addr;
    int r_zero;//relative zero
    float ratio;//offset ratio
    int angle;
}Steer_Info;
//MOTOR_Direct_Info;
//ROITRACER Definitions
typedef struct{
    void *traceclass;
    int cam_device_id;
}ROITRACER_Info;
typedef struct{
   int *addr;
}GPIO_BITS_Info;

//IMA_Out Definitions
typedef struct{
    int cam_device_id;
}IMGOUT_Info;
//ultra
typedef struct{
    int *addr;
    float ratio;

}ULTRA_Sound_Info;
//arm
typedef struct{
    Steer_Info s1,s2,s3;//
}Steer_ARM_Info;
void infotables_init();
#endif // INFOTABLES_H
