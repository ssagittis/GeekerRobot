#include "HMC.h"
const  uchar HMC_I2C_ADDR=(0x3C>>1);
int set_HMC_Message(HMC_Info msg ) {
	uchar regA,regB,regC;
	uchar addrA,addrB,addrC;
	regA=0x74;
	regB=(msg.level)<<5;
	regC=0x00;
	addrA=0x00;addrB=0x01;addrC=0x02;
    set_i2c_register(msg.fd,HMC_I2C_ADDR,addrA,regA);
    set_i2c_register(msg.fd,HMC_I2C_ADDR,addrB,regB);
    set_i2c_register(msg.fd,HMC_I2C_ADDR,addrC,regC);
	//set_i2c_register(msg.fd);
	return 0;
}
HMC_Data_Message get_HMC_Data(HMC_Info msg){
	HMC_Data_Message dat;
	uchar reg=0x03;
	dat.fd=msg.fd;
    get_i2c_register(msg.fd,HMC_I2C_ADDR,reg,(uchar*)&(dat.x),6);
    printf("x%d,y%d,z%d\n",dat.x,dat.y,dat.z);
	return dat;
}
HMC_Direction get_HMC_Direction(HMC_Info msg){
	HMC_Direction dir;
	HMC_Data_Message dat;
	dat=get_HMC_Data(msg);
	dir.axy=(int)(atan2((double)(dat.y),(double)(dat.x)) * (180 / 3.14159265) + 180);
	dir.ayz=(int)(atan2((double)(dat.z),(double)(dat.y)) * (180 / 3.14159265) + 180);
	dir.azx=(int)(atan2((double)(dat.x),(double)(dat.z)) * (180 / 3.14159265) + 180);
	printf("axy:%d ayz:%d azx:%d",dir.axy,dir.ayz,dir.azx);
	return dir;
}

