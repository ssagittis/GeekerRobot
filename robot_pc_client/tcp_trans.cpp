/*
 * tcp_trans.c

 *
 *  Created on: 2015-5-22
 *      Author: lin
 */
#include "opencv2/opencv.hpp"
#include "tcp_trans.h"
//#include "car_process.h"
//#include "v4lcam.h"
#include "pthread.h"
//#include "img_process.h"
int  tcp_struct_init(car_tcp_struct *tcp_str){
		// int ser_sockfd,cli_sockfd;
    int err;

    tcp_str->cli_sockfd=socket(AF_INET,SOCK_STREAM,0);
    if(tcp_str->cli_sockfd==-1)
    {
        printf("socket error\n");
        return -1;
    }

    bzero(&(tcp_str->ser_addr),sizeof(tcp_str->ser_addr));
    tcp_str->ser_addr.sin_family=AF_INET;
  // tcp_str->ser_addr.sin_addr.s_addr=htonl(((long long)127<<24)|((long long )0<<16)|((long)0<<8)|1);//htonl(INADDR_ANY);
    tcp_str->ser_addr.sin_addr.s_addr=htonl(((long long)192<<24)|((long long )168<<16)|((long)1<<8)|10);//htonl(INADDR_ANY);
    tcp_str->ser_addr.sin_port=htons(tcp_str->PORT);

    err=connect(tcp_str->cli_sockfd,(struct sockaddr *)&(tcp_str->ser_addr),sizeof(tcp_str->ser_addr));
    if(err==-1)
    {
        printf("connect error\n");
        //break;
    }
    /*
		int err;
	    tcp_str->ser_sockfd=socket(AF_INET,SOCK_STREAM,0);
	    if(tcp_str->ser_sockfd==-1)
	    {
	        printf("socket error:%s\n",strerror(errno));
	        return -1;
	    }
        int ret, on=1;
        ret=setsockopt(tcp_str->ser_sockfd,SOL_SOCKET,SO_REUSEADDR,&on,sizeof(on));

	    bzero(&(tcp_str->ser_addr),sizeof(tcp_str->ser_addr));
	    tcp_str->ser_addr.sin_family=AF_INET;
	    tcp_str->ser_addr.sin_addr.s_addr=htonl(INADDR_ANY);
        tcp_str->ser_addr.sin_port=htons(tcp_str->PORT);
	    err=bind(tcp_str->ser_sockfd,(struct sockaddr *)&(tcp_str->ser_addr),sizeof(tcp_str->ser_addr));
	    if(err==-1)
	    {
	        printf("bind error:%s\n",strerror(errno));
	        return -1;
	    }

	    err=listen(tcp_str->ser_sockfd,5);
	    if(err==-1)
	    {
	        printf("listen error\n");
	        return -1;
	    }

        printf("listen the port:\n");*/
	    return 0;
}
//tcp service for receive packages from Client
int tcp_ser_thread_rec(car_tcp_struct *tcp_str,car_tcp_package *(*car_process)(car_tcp_package *rec_pack)){
	int addlen,n;
    //char recvline[200],sendline[200];
	car_tcp_package tcp_pack;
    uchar recBuf[0x1000];
        addlen=sizeof(struct sockaddr);
        while(1){
        tcp_str->cli_sockfd=accept(tcp_str->ser_sockfd,(struct sockaddr *)&(tcp_str->cli_addr),(socklen_t*)&addlen);
        if(tcp_str->cli_sockfd==-1)
        {
            printf("accept error\n");
            sleep(1);

        }else{
            break;
        }
        }
        while(1)
        {
            printf("waiting for client...\n");
             n=recv(tcp_str->cli_sockfd,recBuf,0x1000,0);
             	  if(n==-1){
             		  printf("recv error\n");
             		  break;
             	  }
             tcp_pack.device_info=(car_device_info*)(recBuf+12);
             memcpy(&tcp_pack,recBuf,12);
             car_process(&tcp_pack);
             //tcp_pack.device_info=(car_sensor_info *)calloc(tcp_pack.device_num,sizeof(car_device_info));

        }
        close(tcp_str->cli_sockfd);
        return 0;
}
int tcp_ser_thread_send(car_tcp_struct *tcp_str,car_tcp_package (*car_process_read)()){
    int addlen,n;
    //char recvline[200],sendline[200];
    car_tcp_package tcp_pack;
    uchar recBuf[0x1000];
        addlen=sizeof(struct sockaddr);
        while(1){
        tcp_str->cli_sockfd=accept(tcp_str->ser_sockfd,(struct sockaddr *)&(tcp_str->cli_addr),(socklen_t*)&addlen);
        if(tcp_str->cli_sockfd<0)
        {
            printf("accept error\n");
            sleep(1);

        }else{
            break;
        }
        }
        while(1)
        {
         //   printf("waiting for client...\n");
            // n=recv(tcp_str->cli_sockfd,recBuf,0x1000,0);
                //  if(n==-1){
                    //  printf("recv error\n");
                     // break;
                 // }
            tcp_pack=car_process_read();
             //tcp_pack.device_info=(car_device_info*)(recBuf+12);
             memcpy(recBuf,&tcp_pack,12);
             memcpy(recBuf+12,tcp_pack.device_info,tcp_pack.device_num*sizeof(car_device_info));
            send(tcp_str->cli_sockfd,recBuf,tcp_pack.device_num*sizeof(car_device_info)+12,0);
            usleep(100000);
            // car_process(tcp_pack);
             //tcp_pack.device_info=(car_sensor_info *)calloc(tcp_pack.device_num,sizeof(car_device_info));

        }
        close(tcp_str->cli_sockfd);
        return 0;
}
/*
void *process_rec_thread(void *arg){
    car_tcp_struct tcp_str;
    tcp_str.PORT=8002;
    tcp_ser_thread_init(&tcp_str);
    while(1){
        tcp_ser_thread_rec(&tcp_str,car_process);
    }
}
void* process_send_thread(void*arg){
    car_tcp_struct tcp_str;
    tcp_str.PORT=8003;
    tcp_ser_thread_init(&tcp_str);
    while(1){
        tcp_ser_thread_send(&tcp_str,car_send_process);
    }
}*/
/*
void start_all_process(){
       pthread_t img_send_thread,cmd_send_thread,cmd_rec_thread;
       pthread_create(&img_send_thread,NULL,img_process,NULL);
       pthread_create(&cmd_send_thread,NULL,process_send_thread,NULL);
       pthread_create(&cmd_rec_thread,NULL,process_rec_thread,NULL);
  //     pthread_join(img_send_thread,NULL);
    //   pthread_join(cmd_send_thread,NULL);
    //   pthread_join(cmd_rec_thread,NULL);

}*/

using namespace cv;
#define PIC_PACK_SIZE 0x10000
typedef struct{
    car_tcp_struct *tcp_str;
    cv::Mat **src;
}tcp_cam_params;
//img transfer service
/*
void *tcp_ser_cam_thread(void *arg){
    tcp_cam_params *param=(tcp_cam_params*)arg;
    car_tcp_struct *tcp_str=param->tcp_str;
    cv::Mat **src=param->src;
	cv::vector<uchar> buf;
    cv::vector<int>params=cv::vector<int>(2);
    params[0]=CV_IMWRITE_JPEG_QUALITY;
    params[1]=30;
  while(1){
    while ((*src)->empty()){
        usleep(300000);
        printf("wait pic\n");
    }
     imshow("im",**src);
    cv::imencode(".jpg",**src,buf,params);
    int addlen;
    addlen=sizeof(struct sockaddr);
    while(1){
    tcp_str->cli_sockfd=accept(tcp_str->ser_sockfd,(struct sockaddr *)&(tcp_str->cli_addr),(socklen_t*)&addlen);
    if(tcp_str->cli_sockfd==-1)
    {
        printf("accept error\n");
        sleep(1);
       // close(tcp_str->ser_sockfd);
    //    tcp_ser_thread_init(tcp_str);
    //   break;
    }else{
        printf("connected\n");
        break;
    }
    }
    int n,i;
    int sendsiz=0;
    n=buf.size()/PIC_PACK_SIZE;
    uchar *pt=buf.data();
    int packsize=buf.size();
   send(tcp_str->cli_sockfd,&packsize,4,0);
    for(i=1;i<n;i++)
    {
        //car_tcp_package *send_pack;
        //send_pack=car_process(&tcp_pack);
        send(tcp_str->cli_sockfd,pt,PIC_PACK_SIZE,0);
        pt+=PIC_PACK_SIZE;
        sendsiz+=PIC_PACK_SIZE;
        //printf("send %d",pt-buf.data());
        //send(tcp_str->cli_sockfd,send_pack->sensor_info,send_pack->sensor_num*sizeof(car_sensor_info),0);
    }
    sendsiz+=send(tcp_str->cli_sockfd,pt,buf.size()%PIC_PACK_SIZE,0);
    printf("send %d",sendsiz);
    close(tcp_str->cli_sockfd);
   // cv::waitKey(30);
    usleep(50000);
  }
    //return ;
}*/
cv::Mat img_Out;
/*
//img process
void *img_process(void *arg){
    pthread_t img_send_thread;
    tcp_cam_params cam_thr_arg;
    Mat *imgToSend;
    car_tcp_struct tcp_str;
    tcp_str.PORT=8000;
    Mat displayedFrame;
    cam_thr_arg.src=&imgToSend;
    imgToSend=&displayedFrame;
    tcp_ser_thread_init(&tcp_str);
    cam_thr_arg.tcp_str=&tcp_str;
    //V4lCam *cam=new V4lCam();
    //cam->init(640,480,0);
     //get_cam_img(cam);
    pthread_create(&img_send_thread,NULL,tcp_ser_cam_thread,&cam_thr_arg);
    while(1){
        img_process_loop_body();

        if((img_out_cam_id>=0)){
               Cam_Info* send_img_info=(Cam_Info*)(get_device_info_struct(img_out_cam_id));
              if(send_img_info!=NULL&&(send_img_info->Mat!=NULL)){
                  if(((Mat*)(send_img_info->Mat))->empty()){
                      imgToSend=&displayedFrame;
                  }else{
                imgToSend= (Mat*)(send_img_info->Mat);
                imshow("img",*imgToSend);
                  }
              }
              else
                   imgToSend=&displayedFrame;
        }
        else{
            imgToSend=&displayedFrame;
        }
        /*
        Mat cameraFrame(cam->imgSize().height,cam->imgSize().width,CV_8UC2,cam->getFrameData()->start);

        if( cameraFrame.empty() ) {
            printf( "ERROR: Couldn't grab the next camera frame.");// << std::endl;
            exit(1);
        }

        cvtColor(cameraFrame,displayedFrame,CV_YUV2BGR_YUYV);*/
        //cvWaitKey(30);
 /*       usleep(30000);
    }

}
*/
//tcp service for send data to client


/*
int tcp_thread(){
    int ser_sockfd,cli_sockfd;
    int err,n;
    int addlen;
    struct sockaddr_in ser_addr;
    struct sockaddr_in cli_addr;
    char recvline[200],sendline[200];

    ser_sockfd=socket(AF_INET,SOCK_STREAM,0);
    if(ser_sockfd==-1)
    {
        printf("socket error:%s\n",strerror(errno));
        return -1;
    }

    bzero(&ser_addr,sizeof(ser_addr));
    ser_addr.sin_family=AF_INET;
    ser_addr.sin_addr.s_addr=htonl(INADDR_ANY);
    ser_addr.sin_port=htons(PORT);
    err=bind(ser_sockfd,(struct sockaddr *)&ser_addr,sizeof(ser_addr));
    if(err==-1)
    {
        printf("bind error:%s\n",strerror(errno));
        return -1;
    }

    err=listen(ser_sockfd,5);
    if(err==-1)
    {
        printf("listen error\n");
        return -1;
    }

    printf("listen the port:\n");

    while(1)
    {
        addlen=sizeof(struct sockaddr);
        cli_sockfd=accept(ser_sockfd,(struct sockaddr *)&cli_addr,&addlen);
        if(cli_sockfd==-1)
        {
            printf("accept error\n");
        }
        while(1)
        {
            printf("waiting for client...\n");
            n=recv(cli_sockfd,recvline,1024,0);
            if(n==-1)
            {
                printf("recv error\n");
            }
            recvline[n]='\0';

            printf("recv data is:%s\n",recvline);

            printf("Input your words:");
            scanf("%s",&sendline);
            send(cli_sockfd,sendline,strlen(sendline),0);
        }
        close(cli_sockfd);
    }

    close(ser_sockfd);
}*/
