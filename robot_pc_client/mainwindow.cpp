#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "QTimer"
#include "pthread.h"
#include "opencv2/opencv.hpp"
typedef struct{
    car_tcp_struct tcp_s;
    QTableWidget *wid;
}Rec_Tcp_Arg ;
void tcp_rec_tab(car_tcp_package pack,QTableWidget *wid);
void*recTcp_c(void *arg){
  //  recTimer->stop();
    Rec_Tcp_Arg *tcp_arg=(Rec_Tcp_Arg*)arg;
     int headerSize=sizeof(car_tcp_package)-sizeof(car_device_info*);
     QTableWidget *wid;
    char buf[0x8000];
    char *p_buf=buf;
    car_tcp_struct tcp_rec=(tcp_arg->tcp_s);
    wid=tcp_arg->wid;
   // printf("size is %d ",packsize);
    int size=0;
    car_tcp_package pack;
    pack.device_info=(car_device_info*)(buf+headerSize);

    while(1){
        int n;

        n=recv(tcp_rec.cli_sockfd,buf,0x8000,0);
        if(n>0){
           // p_buf+=n;
          //  size+=n;
            memcpy(&pack,buf,headerSize);
            printf("n=%d,header=%x,pack_size=%d,deviceNum=%d\n",n,pack.header,pack.pack_size,pack.device_num);
            //if((pack.header==0xf8f8)&&(n==(pack.pack_size+headerSize))){
                tcp_rec_tab(pack,wid);
            //}
            printf("n=%d ",n);
            //usleep();
        }else{
            //printf("rec %d\n",size);
            size=0;
       //     break;
            //printf("rec error\n");
        }
   }
 //   recTimer->start(20);
}
void *img_rec_thread(void *){
    int sockfd;
    int err,n;
    struct sockaddr_in addr_ser;
    uchar *buf=(uchar *)malloc(0x10000);
    //char sendline[20],recvline[20];
while(1){
    sockfd=socket(AF_INET,SOCK_STREAM,0);
    if(sockfd==-1)
    {
        printf("socket error\n");
        //return -1;
    }

    bzero(&addr_ser,sizeof(addr_ser));
    addr_ser.sin_family=AF_INET;
 //   addr_ser.sin_addr.s_addr=htonl(((long long)127<<24)|((long long )0<<16)|((long)0<<8)|1);//htonl(INADDR_ANY);
    addr_ser.sin_addr.s_addr=htonl(((long long)192<<24)|((long long )168<<16)|((long)1<<8)|10);//htonl(INADDR_ANY);
    addr_ser.sin_port=htons(8000);

    err=connect(sockfd,(struct sockaddr *)&addr_ser,sizeof(addr_ser));
    if(err==-1)
    {
        printf("connect error\n");
        break;
    }
#define PIC_PACK_SIZE 0x10000
    printf("connect with server...\n");

    uchar *pt=buf;
    long imgsiz=0;
    int packsize;
    recv(sockfd,&packsize,4,0);
    printf("size is %d ",packsize);
    while(1){
        int n;

        n=recv(sockfd,pt,PIC_PACK_SIZE,0);
        if(n>0){
        imgsiz+=n;
        pt+=n;
//        printf("rec %d",n);
        }else{
            break;
          //  printf("rec error\n");
        }
    }
    std::vector<uchar> vec_buf;
    vec_buf.assign(buf,buf+imgsiz);
   cv::Mat imat=cv::imdecode(cv::Mat(vec_buf),CV_LOAD_IMAGE_COLOR);
   imshow("rec",imat);
  // cv::waitKey(20);
    close(sockfd);
}
}

void tcp_rec_tab(car_tcp_package pack,QTableWidget *wid){
    int i,j;
    wid->setRowCount(pack.device_num);
    for(i=0;i<pack.device_num;i++){
        wid->setItem(i,0,new QTableWidgetItem(QString::number(pack.device_info[i].device_id)));
        for(j=0;j<7;j++){
            wid->setItem(i,j+1,new QTableWidgetItem(QString::number(pack.device_info[i].param[j])));
        }
    }
}


MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    tcp_rec.PORT=8003;
    tcp_send.PORT=8002;
    tcp_struct_init(&tcp_rec);
    tcp_struct_init(&tcp_send);
    printf("size of pack %d total %d int %d char %d\n",sizeof(car_tcp_package)-sizeof(car_device_info *),sizeof(car_tcp_package),sizeof(int),sizeof(char));
   // recTimer=new QTimer();
  //  recTimer->setInterval(20);
    QObject::connect(recTimer,SIGNAL(timeout()),this,SLOT(recTcp()));
    pthread_t rec_thread,img_thread;
    Rec_Tcp_Arg *tcp_arg=(Rec_Tcp_Arg*)malloc(sizeof(Rec_Tcp_Arg));
    tcp_arg->tcp_s=tcp_rec;
    tcp_arg->wid=this->ui->TabRec;
    pthread_create(&rec_thread,NULL,recTcp_c,tcp_arg);
    pthread_create(&img_thread,NULL,img_rec_thread,NULL);

   // recTimer->start();

}

void MainWindow::recTcp(void ){
  //  recTimer->stop();
    char buf[0x8000];
    char *p_buf=buf;
   // printf("size is %d ",packsize);
    int size=0;
    //while(1){
        int n;

        n=recv(tcp_rec.cli_sockfd,p_buf,0x8000,0);
        if(n>0){
           // p_buf+=n;
            size+=n;
            printf("n=%d ",n);
            usleep(1000);
        }else{
            printf("rec %d\n",size);
       //     break;
            //printf("rec error\n");
        }
  //  }
 //   recTimer->start(20);
}

MainWindow::~MainWindow()
{
    delete ui;
}


void MainWindow::on_BtAddInit_clicked()
{
    int i;
   this->ui->TabInit->insertRow( this->ui->TabInit->rowCount());
   /* for(i=0;i<this->ui->TabInit->columnCount();i++){
        this->ui->TabInit->item(this->ui->TabInit->rowCount()-1,i)->setText("0");
    }*/
}

void MainWindow::on_BtDelInit_clicked()
{
   // if()
    this->ui->TabInit->removeRow(this->ui->TabInit->currentRow());
}

void MainWindow::on_BtAddSet_clicked()
{
     this->ui->TabSet->insertRow( this->ui->TabSet->rowCount());
    int i;
    /*
    for(i=0;i<this->ui->TabSet->columnCount();i++){
        this->ui->TabSet->item(this->ui->TabSet->rowCount()-1,i)->setText("0");
    }*/
}

void MainWindow::on_BtDelDel_clicked()
{
      this->ui->TabSet->removeRow(this->ui->TabSet->currentRow());
}

void tcp_send_tab(QTableWidget *wid,car_tcp_struct tcp_str,car_instructions instruction){
//    QTableWidget *wid;
     int headerSize=sizeof(car_tcp_package)-sizeof(car_device_info*);
  //  wid=this->ui->TabInit;
     car_device_info pack[wid->rowCount()];
    int i,j;
    car_tcp_package sendpack;
    sendpack.pack_size=sizeof(car_device_info)*wid->rowCount();
    sendpack.instruction=instruction;
    sendpack.device_num=wid->rowCount();
    sendpack.device_info=pack;
    for(i=0;i<wid->rowCount();i++){
       // for(j=0;j<wid->columnCount();j++){
            pack[i].device_id=wid->item(i,0)->text().toShort();
            for(j=0;j<7;j++){
               // if(wid->item(i,j+1)->)
                if(wid->item(i,j+1)==0){
                 pack[i].param[j]=0;
                }else{
                     pack[i].param[j]=wid->item(i,j+1)->text().toInt();


                }

            }
       // }
    }
    char sendbuf[0x8000];
    sendpack.header=0xf8f8;
    memcpy(sendbuf,&sendpack,headerSize);
    memcpy(sendbuf+headerSize,&pack,sizeof(car_device_info)*wid->rowCount());
    send(tcp_str.cli_sockfd,sendbuf,sendpack.pack_size,0);
}

void MainWindow::on_BtInit_clicked()
{
    tcp_send_tab(this->ui->TabInit,tcp_send,DEVICE_Init);
  //  this->ui->TabInit->item();
}

void MainWindow::on_BtSet_clicked()
{
    tcp_send_tab(this->ui->TabSet,tcp_send,DEVICE_Set);
}
