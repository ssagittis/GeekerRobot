/*
 * i2c.h
 *
 *  Created on: 2015-5-16
 *      Author: lin
 */

#ifndef I2C_H_
#define I2C_H_

#include <stdio.h>
#include <linux/i2c.h>
#include <linux/i2c-dev.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <string.h>
//#include <stdbool.h>

 int set_i2c_register(int file,
                            unsigned char addr,
                            unsigned char reg,
                            unsigned char value);
 int get_i2c_register(int file,
                      unsigned char addr,
                            unsigned char reg,
                            unsigned char *val,
                            unsigned char val_num);


#endif /* I2C_H_ */
