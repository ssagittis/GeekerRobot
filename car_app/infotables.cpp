#include "infotables.h"
device_interface_item interface_table[20];
std::vector<using_item> device_using;
int mem_fd;
int img_out_cam_id;
/*
 * typedef struct{
    unsigned int id;
    device_type type;
    volatile long phy_addr;
}device_interface_item;
*/
void startup_init(){
    mem_fd=open("/dev/mem",O_RDWR,0);
    img_out_cam_id=-1;
    infotables_init();
  }

void infotables_init(){
    device_interface_item ditem;
    ditem.id=0;
    ditem.type=ITYPE_I2C;
    ditem.file_name="/dev/i2c-0";
    interface_table[0]=ditem;

    ditem.id=1;
    ditem.type=ITYPE_CAM;
    ditem.file_name="/dev/video0";
    interface_table[1]=ditem;
    ditem.id=2;
    ditem.type=ITYPE_CAM;
    ditem.file_name="/dev/video1";
    interface_table[2]=ditem;

    ditem.id=3;
    ditem.type=ITYPE_MOTOR;
    ditem.phy_addr=0x7B400000;
    interface_table[3]=ditem;
    ditem.id=4;
    ditem.type=ITYPE_MOTOR;
    ditem.phy_addr=0x7B420000;
    interface_table[4]=ditem;
    ditem.id=5;
    ditem.type=ITYPE_ACTUAL;
    ditem.phy_addr=0x80620000;
    interface_table[5]=ditem;
    ditem.id=6;
    ditem.type=ITYPE_ACTUAL;
    ditem.phy_addr=0x80640000;
    interface_table[6]=ditem;
    ditem.id=7;
    ditem.type=ITYPE_ACTUAL;
    ditem.phy_addr=0x80660000;
    interface_table[7]=ditem;
    ditem.id=8;
    ditem.type=ITYPE_ACTUAL;
    ditem.phy_addr=0x80700000;
    interface_table[8]=ditem;
    ditem.id=9;
    ditem.type=ITYPE_ACTUAL;
    ditem.phy_addr=0x80720000;
    interface_table[9]=ditem;
    ditem.id=10;
    ditem.type=ITYPE_BITS;
    ditem.phy_addr=0x41240000;
    interface_table[10]=ditem;
    ditem.id=11;
    ditem.type=ITYPE_ULTRA_SOUND;
    ditem.phy_addr=0x61600000;
    interface_table[11]=ditem;


    int i;
    for(i=0;i<11;i++){
        switch(interface_table[i].type){
        case ITYPE_I2C:
            interface_table[i].fd=open(interface_table[i].file_name,O_RDWR);
            break;
        case ITYPE_MOTOR:
        case ITYPE_ACTUAL:
        case ITYPE_BITS:
        case ITYPE_ULTRA_SOUND:
            interface_table[i].virt_addr=mmap(NULL, 4096 , PROT_READ|PROT_WRITE, MAP_SHARED , mem_fd , interface_table[i].phy_addr);
            printf("type %d, phy_add %lx, vir_add %lx\n",(int)interface_table[i].type,interface_table[i].phy_addr,(long)interface_table[i].virt_addr);
            break;
        default:
            break;
         }
    }
}
