/*
 * car_process..c
 *
 *  Created on: 2015-5-25
 *      Author: lin
 */

#include "car_process.h"
#include "HMC.h"
#include "HMC.h"
#include <vector>
#include "v4lcam.h"
car_tcp_package *car_process (car_tcp_package *rec_pack){
	int i,j;
    printf("rec instruction %d",rec_pack->instruction);
    switch(rec_pack->instruction){
        case  DEVICE_Init://initialize using table
            car_process_init(rec_pack);
            break;
        case DEVICE_Set://set device
            car_process_Set(rec_pack);
            break;
        case DEVICE_Func:
            break;
    }

	return rec_pack;
};
void car_process_Set(car_tcp_package *rec_pack){
    int i;
    for(i=0;i<rec_pack->device_num;i++){
        car_device_info sinfo=rec_pack->device_info[i];
        printf("sinfo:id=%d,p1=%ld,p2=%ld\n",sinfo.device_id,sinfo.param[0],sinfo.param[1]);
        using_item item;
        item=*get_device_using_iterator(sinfo.device_id);
        switch(item.type){
            case DTYPE_HMC :
                   break;//no setting function
            case DTYPE_MOTOR_Direc:
                    motor_direc_process_set((Steer_Info*)item.device_info_struct,&sinfo);
                    printf("direction:%d",sinfo.param[0]);
                    break;
            case DTYPE_MOTOR_Drive:
                    motor_drive_process_set((MOTOR_Drive_Info*)item.device_info_struct,&sinfo);
                    printf("motor:%d %d",sinfo.param[0],sinfo.param[1]);
            case DTYPE_ROITRACER:
                    break;//no setting function
            case DTYPE_STEER_ARM:
                    steer_arm_Set((Steer_ARM_Info*)item.device_info_struct,&sinfo);
                    break;

            default:

                    break;
             }
    }
}

void car_process_init(car_tcp_package *rec_pack){
    device_using_rmv_all();
    int i;
    for(i=0;i<rec_pack->device_num;i++){
        car_device_info sinfo=rec_pack->device_info[i];
        printf("sinfo:id=%d,p1=%ld,p2=%ld\n",sinfo.device_id,sinfo.param[0],sinfo.param[1]);
        switch((device_type)(sinfo.param[0])){
            case DTYPE_HMC :
                    HMC_process_Init(&sinfo);//no need for init params
                   break;//
            case DTYPE_MOTOR_Direc:
                    motor_direc_process_Init(sinfo);
                    break;
            case DTYPE_MOTOR_Drive:
                    motor_drive_process_Init(sinfo);
                    break;
            case DTYPE_ROITRACER:
                    roitracer_init(sinfo);
                    break;//no setting function
            case DTYPE_CAM:
                    cam_init(sinfo);
                    break;
            case DTYPE_BITS:
                    gpio_init(sinfo);
                    break;
            case DTYPE_STEER_ARM:
                    steer_arm_Init(sinfo);
                    break;
            case DTYPE_ULTRAL_SOUND:
                    ultra_sound_Init(sinfo);
                    break;
            case DTYPE_ADXL:

                    break;
            default :
                    break;


                    }
    }

   /* while(1){
         sleep(1);
    get_HMC_Direction(*hmc_i);

    }*/

}

car_tcp_package  car_send_process(){
    std::vector<using_item>::iterator it=device_using.begin();
    static car_device_info *pack=(car_device_info*)malloc(0x1000);
    car_tcp_package sendpack;
    //std::vector<car_device_info> pac_item;
    car_device_info pac_item_tmp;
    using_item item;
    int i=0;
    for(;it!=device_using.end();it++){
        item=*it;
        switch(item.type){
        case DTYPE_HMC:
            pac_item_tmp=(HMC_Process_read((HMC_Info*)get_device_info_struct(item.id)));
            pac_item_tmp.device_id=item.id;
            pack[i]=pac_item_tmp;i++;
            break;
        case DTYPE_ROITRACER:
            pac_item_tmp=(ROI_Tracer_Read((ROITRACER_Info*)get_device_info_struct(item.id)));
            pac_item_tmp.device_id=item.id;
            pack[i]=pac_item_tmp;i++;
            break;
        case DTYPE_ULTRAL_SOUND:
            pac_item_tmp=ultra_sound_Read((ULTRA_Sound_Info*)get_device_info_struct(item.id));
            pac_item_tmp.device_id=item.id;
            pack[i]=pac_item_tmp;i++;
        default:
            break;
        }
    }
    sendpack.pack_size=sizeof(car_device_info)*i;
    sendpack.instruction=DEVICE_Set;
    sendpack.device_num=i;
    sendpack.device_info=pack;
    return sendpack;
}

//HMC Set
car_device_info HMC_process_Init(car_device_info *sinfo){
    HMC_Info *hmc_i=(HMC_Info*)malloc(sizeof(HMC_Info));
    hmc_i->fd=interface_table[sinfo->param[1]].fd;//  device_table[sinfo->param[0]].fd;
    hmc_i->level=5;
    set_HMC_Message(*hmc_i);
    using_item hmc_it;
    hmc_it.id=sinfo->device_id;
    hmc_it.type=DTYPE_HMC;
    hmc_it.interface_struct=&(interface_table[sinfo->param[1]]);
    //table[0].type=HMC;
    hmc_it.device_info_struct=hmc_i;
    device_using.push_back(hmc_it);
    if(hmc_i->fd>0) printf("inited\n");
    else printf("i2c open error\n");
}
//HMC Read
car_device_info HMC_Process_read(HMC_Info *hinfo){
    HMC_Direction dir;
    car_device_info sinfo;
    dir=get_HMC_Direction(*hinfo);
   // sinfo.param_num=3;
    sinfo.param[0]=dir.axy;
    sinfo.param[1]=dir.ayz;
    sinfo.param[2]=dir.azx;
    return sinfo;
}
//roiTracer read
car_device_info ROI_Tracer_Read(ROITRACER_Info*rinfo ){
    ROI_Tracer_Class*roicla=(ROI_Tracer_Class*)(rinfo->traceclass);
    car_device_info sendinfo;
    //sendinfo.param=rinfo->traceclass
   sendinfo.param[0]=(int)(roicla->trace_res.center.x);
    sendinfo.param[1]=(int)(roicla->trace_res.center.y);
     sendinfo.param[2]=(int)(roicla->trace_res.size.width);
   sendinfo.param[3]=(int)(roicla->trace_res.size.height);
   sendinfo.param[4]=(int)(roicla->trace_res.angle);
   return sendinfo;

}

void motor_direc_process_set(Steer_Info*minfo, car_device_info *setinfo){
    while(1){
        if(minfo->angle>setinfo->param[0]+5){
            minfo->angle-=5;
            (*(minfo->addr))=(int)((minfo->angle+minfo->r_zero)*minfo->ratio);
            usleep(80000);

        }else if(minfo->angle<setinfo->param[0]-5){
            minfo->angle+=5;
            (*(minfo->addr))=(int)((minfo->angle+minfo->r_zero)*minfo->ratio);
            usleep(80000);
        }else{
            minfo->angle=setinfo->param[0];
            (*(minfo->addr))=(int)((minfo->angle+minfo->r_zero)*minfo->ratio);
            break;
        }


    }


    (*(minfo->addr))=(int)((setinfo->param[0]+minfo->r_zero)*minfo->ratio);

    printf("direct write %d\n",(int)((setinfo->param[0]+minfo->r_zero)*minfo->ratio));
}

void motor_drive_process_set(MOTOR_Drive_Info *minfo,car_device_info*sinfo){
    *(minfo->addr)=1000;
    *(minfo->addr+1)=(1000-sinfo->param[0]*10);
    printf("drive addr %lx",(long)minfo->addr);
}

void motor_direc_process_Init(car_device_info sinfo){
    Steer_Info *minfo=(Steer_Info*)malloc(sizeof(Steer_Info));
    minfo->ratio=1;
    minfo->r_zero=sinfo.param[2];
    minfo->angle=0;
    minfo->addr= (int *)interface_table[sinfo.param[1]].virt_addr;;//(long*)(device_table[sinfo.param[0]].virt_addr);
            //(long *)mmap(NULL, sizeof(long) , PROT_READ|PROT_WRITE, MAP_SHARED , mem_fd , device_table[sinfo.device_id].phy_addr);

    using_item uitem;
    uitem.type=DTYPE_MOTOR_Direc;
    uitem.interface_struct=&interface_table[sinfo.param[1]];
    uitem.device_info_struct=minfo;
    uitem.id=sinfo.device_id;
    printf("steer init id %d,type %d,uitem.addr%ld\n",uitem.id,uitem.type,(long)(minfo->addr));
    device_using.push_back(uitem);
}


void motor_drive_process_Init(car_device_info sinfo){
    MOTOR_Drive_Info *minfo=(MOTOR_Drive_Info*)malloc(sizeof(MOTOR_Drive_Info));
    minfo->addr= (int *)interface_table[sinfo.param[1]].virt_addr;//(long*)device_table[sinfo.param[0]].virt_addr;
            //(long *)mmap(NULL, sizeof(long) , PROT_READ|PROT_WRITE, MAP_SHARED , mem_fd , device_table[sinfo.device_id].phy_addr);
    using_item uitem;
    uitem.type=DTYPE_MOTOR_Drive;
    uitem.interface_struct=&interface_table[sinfo.param[1]];
    uitem.device_info_struct=minfo;
    uitem.id=sinfo.device_id;
    device_using.push_back(uitem);
}
/*!
 * \brief steer_arm_Init
 * \param sinfo 1-arm1 2-arm2 3-arm3
 */
void steer_arm_Init(car_device_info sinfo){
    Steer_ARM_Info *info=(Steer_ARM_Info*)malloc(sizeof(Steer_ARM_Info));
    info->s1.addr=(int *)interface_table[sinfo.param[1]].virt_addr;
    info->s1.r_zero=sinfo.param[4];
    info->s1.ratio=1;
    info->s2.addr=(int *)interface_table[sinfo.param[2]].virt_addr;
    info->s2.r_zero=sinfo.param[5];
    info->s2.ratio=1;
    info->s3.addr=(int *)interface_table[sinfo.param[3]].virt_addr;
    info->s3.r_zero=sinfo.param[6];
    info->s3.ratio=1;

    using_item uitem;
    uitem.type=DTYPE_STEER_ARM;
    uitem.device_info_struct=info;
    uitem.id=sinfo.device_id;
    device_using.push_back(uitem);
}
void steer_arm_Set(Steer_ARM_Info *dinfo,car_device_info *sinfo){
    (*(dinfo->s1.addr))=(int)((sinfo->param[0]+dinfo->s1.r_zero)*dinfo->s1.ratio);
    (*(dinfo->s2.addr))=(int)((sinfo->param[1]+dinfo->s2.r_zero)*dinfo->s2.ratio);
    (*(dinfo->s3.addr))=(int)((sinfo->param[2]+dinfo->s3.r_zero)*dinfo->s3.ratio);

}


void ultra_sound_Init(car_device_info sinfo){
    ULTRA_Sound_Info *info=(ULTRA_Sound_Info*)malloc(sizeof(ULTRA_Sound_Info));
    info->addr=(int*)interface_table[sinfo.param[1]].virt_addr;
    info->ratio=1;

    using_item uitem;
    uitem.type=DTYPE_ULTRAL_SOUND;
    uitem.device_info_struct=info;
    uitem.id=sinfo.device_id;
    device_using.push_back(uitem);

}
car_device_info ultra_sound_Read(ULTRA_Sound_Info* info){
    car_device_info dinfo;
    dinfo.param[0]=(int)(*(info->addr)*info->ratio);
    return dinfo;
}


void   gpio_init(car_device_info sinfo){
   GPIO_BITS_Info *ginfo=(GPIO_BITS_Info*)malloc(sizeof(GPIO_BITS_Info));
    ginfo->addr= (int *)interface_table[sinfo.param[1]].virt_addr;//(long*)device_table[sinfo.param[0]].virt_addr;
            //(long *)mmap(NULL, sizeof(long) , PROT_READ|PROT_WRITE, MAP_SHARED , mem_fd , device_table[sinfo.device_id].phy_addr);
    using_item uitem;
    uitem.type=DTYPE_BITS;
    uitem.interface_struct=&interface_table[sinfo.param[1]];
    uitem.device_info_struct=ginfo;
    uitem.id=sinfo.device_id;
    device_using.push_back(uitem);
}
void gpio_set(car_device_info sinfo){

}

void motor_drive_process_rmv(car_device_info sinfo){

}

void cam_init(car_device_info dinfo){
    Cam_Info *cinfo=(Cam_Info*)malloc(sizeof(Cam_Info));
    V4lCam *cam=new V4lCam();
    Mat *camMat=new Mat();
    cam->init(640,480,dinfo.param[1]);
    cinfo->V4LCam=cam;

    cinfo->Mat=camMat;
    using_item uitem;
    uitem.device_info_struct=cinfo;
    uitem.id=dinfo.device_id;
    uitem.type=DTYPE_CAM;
    img_out_cam_id=dinfo.device_id;
    device_using.push_back(uitem);
}
void cam_process(Cam_Info *cinfo){
    V4lCam *cam=(V4lCam*)(cinfo->V4LCam);
    Mat *dsc=(Mat*)(cinfo->Mat);
    Mat mat;
    Mat cameraFrame(cam->imgSize().height,cam->imgSize().width,CV_8UC2,cam->getFrameData()->start);
    if(cameraFrame.empty()){
        printf("Cam read Error\n");
    }

    cvtColor(cameraFrame,*dsc,CV_YUV2BGR_YUYV);

   // imshow("cam",*dsc);
    //cinfo->Mat=new Mat(mat);

}
void cam_rmv(Cam_Info *cinfo){
    V4lCam *cam=(V4lCam*)(cinfo->V4LCam);
    cam->releaseCap();
    delete cam;
    Mat *dsc=(Mat*)(cinfo->Mat);
    delete dsc;
  //  delete cinfo;
}

void  roitracer_init(car_device_info sinfo){
    ROI_Tracer_Class *trclas=new ROI_Tracer_Class();
    ROITRACER_Info *roi_info=(ROITRACER_Info*)malloc(sizeof(ROITRACER_Info));
    roi_info->cam_device_id=sinfo.param[1];
    roi_info->traceclass=trclas;
    using_item item;
    item.interface_struct=NULL;
    item.type=DTYPE_ROITRACER;
    item.id=sinfo.device_id;
    item.device_info_struct=roi_info;
    device_using.push_back(item);
    //trclas->img_pt=
    //trclas
}
void roitracer_set(car_device_info sinfo,ROITRACER_Info *rinfo){
    ROI_Tracer_Class *trclas=(ROI_Tracer_Class*)(rinfo->traceclass);
    Rect tarrec;
    tarrec.x=sinfo.param[0];
    tarrec.y=sinfo.param[1];
    tarrec.width=sinfo.param[2];
    tarrec.height=sinfo.param[3];
    trclas->set_Trace_Taget(tarrec);
}

void roitracer_rmv(ROITRACER_Info *rinfo){
    ROI_Tracer_Class *trclas=(ROI_Tracer_Class*)(rinfo->traceclass);
    delete trclas;
    //delete rinfo;
}

void device_using_rmv_all(){
    std::vector<using_item>::iterator it=device_using.begin();

   using_item item;
    int i=0;
    for(;it!=device_using.end();it++){
        item=*it;
        switch(item.type){
           case DTYPE_ROITRACER:
            roitracer_rmv((ROITRACER_Info *)(item.device_info_struct));
           break;
        case DTYPE_CAM:
            cam_rmv((Cam_Info *)(item.device_info_struct));
            break;
        }
        delete item.device_info_struct;
    }
    device_using.clear();
}


void *get_device_info_struct(uchar device_id){
    std::vector<using_item>::iterator it=get_device_using_iterator(device_id);
    if(it!=device_using.end()){
        return (*it).device_info_struct;
    }else{
        return NULL;
    }
}
std::vector<using_item>::iterator get_device_using_iterator(uchar device_id){
     std::vector<using_item>::iterator it=device_using.begin();
    for (;it!=device_using.end();it++) {
        if((*it).id==device_id){
            //printf("get id %d\n",device_id);
            break;
        }
    }
    return it;
}
