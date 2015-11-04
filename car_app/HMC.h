/*
 * HMC.h
 *
 *  Created on: 2015-5-16
 *      Author: lin
 */

#ifndef HMC_H_
#define HMC_H_
#include "i2c.h"
#include <math.h>
#include "infotables.h"
#define HMC_REG_X 03
#define HMC_REG_Y 05
#define HMC_REG_Z 07
//typedef _Bool bool;
typedef unsigned char uchar;
extern  const uchar HMC_I2C_ADDR;

typedef struct{
	int fd;
    short x;
    short y;
    short z;
} HMC_Data_Message;
typedef struct{
	int axy,ayz,azx;
}HMC_Direction ;
int set_HMC_Message(HMC_Info msg ) ;
HMC_Data_Message get_HMC_Data(HMC_Info msg);
HMC_Direction get_HMC_Direction(HMC_Info msg);

#endif /* HMC_H_ */
