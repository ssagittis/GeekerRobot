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

#include<stdio.h>
   #include<stdlib.h>
   #include<string.h>
   #include<sys/socket.h>
   #include<sys/types.h>
   #include<unistd.h>
   #include<netinet/in.h>
#include "opencv2/opencv.hpp"
   #define PORT 8000
using namespace cv;
typedef struct{
    int ser_sockfd;
    int cli_sockfd;
    struct sockaddr_in ser_addr;
    struct sockaddr_in cli_addr;
}car_tcp_struct;

typedef struct{
    char sensor_id;
    char param_num;
    long param[4];
}car_sensor_info ;
typedef struct{
    long pack_size;
    long sensor_num;
    car_sensor_info *sensor_info;

}car_tcp_package ;

   int main(int argc,char **argv)
   {
       int sockfd;
       int err,n;
       struct sockaddr_in addr_ser;
       uchar *buf=(uchar *)malloc(0x100000);
       //char sendline[20],recvline[20];
 while(1){
       sockfd=socket(AF_INET,SOCK_STREAM,0);
       if(sockfd==-1)
       {
           printf("socket error\n");
           return -1;
       }

       bzero(&addr_ser,sizeof(addr_ser));
       addr_ser.sin_family=AF_INET;
       addr_ser.sin_addr.s_addr=htonl(((long long)127<<24)|((long long )0<<16)|((long)0<<8)|1);//htonl(INADDR_ANY);
      //  addr_ser.sin_addr.s_addr=htonl(((long long)192<<24)|((long long )168<<16)|((long)1<<8)|10);//htonl(INADDR_ANY);
       addr_ser.sin_port=htons(PORT);

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
           printf("rec %d",n);
           }else{
               break;
               printf("rec error\n");
           }
       }
       std::vector<uchar> vec_buf;
       vec_buf.assign(buf,buf+imgsiz);
      cv::Mat imat=cv::imdecode(Mat(vec_buf),CV_LOAD_IMAGE_COLOR);
      imshow("rec",imat);
      cv::waitKey(20);
       close(sockfd);
       }

       //cv::imshow("imrec",imat);


       return 0;
   }
#include <opencv2/video/video.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/core/core.hpp>

#include <iostream>
#include <cstdio>


using namespace std;
using namespace cv;


void tracking(Mat &frame, Mat &output);

bool addNewPoints();

bool acceptTrackedPoint(int i);


string window_name = "optical flow tracking";

Mat gray;        // 当前图片

Mat gray_prev;        // 预测图片

vector<Point2f> points[2];        // point0为特征点的原来位置，point1为特征点的新位置

vector<Point2f> initial;        // 初始化跟踪点的位置

vector<Point2f> features;        // 检测的特征

int maxCount = 500;        // 检测的最大特征数

double qLevel = 0.01;        // 特征检测的等级

double minDist = 10.0;        // 两特征点之间的最小距离

vector<uchar> status;        // 跟踪特征的状态，特征的流发现为1，否则为0

vector<float> err;


int main1()

{

        Mat frame;

        Mat result;


      // CvCapture* capture = cv::( -1 );        // 摄像头读取文件开关

        VideoCapture capture(0);


        if(capture.isOpened()/*capture*/)        // 摄像头读取文件开关

        {

                while(true)

                {

//                         frame = cvQueryFrame( capture );        // 摄像头读取文件开关

                        capture >> frame;


                        if(!frame.empty())

                        {
                                tracking(frame, result);

                        }

                        else

                        {
                                printf(" --(!) No captured frame -- Break!");

                                break;

                        }


                        int c = waitKey(100);

                        if( (char)c == 27 )

                        {

                                break;
                        }
                }

        }

        return 0;

}


//////////////////////////////////////////////////////////////////////////

// function: tracking

// brief: 跟踪

// parameter: frame        输入的视频帧

//                          output 有跟踪结果的视频帧

// return: void

//////////////////////////////////////////////////////////////////////////

void tracking(Mat &frame, Mat &output)

{

        cvtColor(frame, gray, CV_BGR2GRAY);

        frame.copyTo(output);

        // 添加特征点

        if (addNewPoints())

        {

                goodFeaturesToTrack(gray, features, maxCount, qLevel, minDist);

                points[0].insert(points[0].end(), features.begin(), features.end());

                initial.insert(initial.end(), features.begin(), features.end());

        }


        if (gray_prev.empty())

        {

                gray.copyTo(gray_prev);

        }

        // l-k光流法运动估计

        calcOpticalFlowPyrLK(gray_prev, gray, points[0], points[1], status, err);

        // 去掉一些不好的特征点

        int k = 0;

        for (size_t i=0; i<points[1].size(); i++)

        {

                if (acceptTrackedPoint(i))

                {

                        initial[k] = initial[i];

                        points[1][k++] = points[1][i];

                }

        }

        points[1].resize(k);

        initial.resize(k);

        // 显示特征点和运动轨迹

        for (size_t i=0; i<points[1].size(); i++)

        {

                line(output, initial[i], points[1][i], Scalar(0, 0, 255));

                circle(output, points[1][i], 3, Scalar(255, 0, 0), -1);

        }


        // 把当前跟踪结果作为下一此参考

        swap(points[1], points[0]);

        swap(gray_prev, gray);


        imshow(window_name, output);

}


//////////////////////////////////////////////////////////////////////////

// function: addNewPoints

// brief: 检测新点是否应该被添加

// parameter:

// return: 是否被添加标志

//////////////////////////////////////////////////////////////////////////

bool addNewPoints()

{

        return points[0].size() <= 10;

}


//////////////////////////////////////////////////////////////////////////

// function: acceptTrackedPoint

// brief: 决定哪些跟踪点被接受

// parameter:

// return:

//////////////////////////////////////////////////////////////////////////

bool acceptTrackedPoint(int i)

{

        return status[i] && ((abs(points[0][i].x - points[1][i].x) + abs(points[0][i].y - points[1][i].y)) > 2);

}

using namespace cv;
using namespace std;


Mat src,dst;
int spatialRad=30,colorRad=30,maxPryLevel=2;
//const Scalar& colorDiff=Scalar::all(1);

void meanshift_seg(int,void *)
{
    //调用meanshift图像金字塔进行分割
    pyrMeanShiftFiltering(src,dst,spatialRad,colorRad,maxPryLevel);
    RNG rng=theRNG();
    Mat mask(dst.rows+2,dst.cols+2,CV_8UC1,Scalar::all(0));
    /*
    for(int i=0;i<dst.rows;i++)    //opencv图像等矩阵也是基于0索引的
        for(int j=0;j<dst.cols;j++)
            if(mask.at<uchar>(i+1,j+1)==0)
            {
                Scalar newcolor(rng(256),rng(256),rng(256));
                //floodFill(dst,mask,Point(i,j),newcolor,0,Scalar::all(1),Scalar::all(1));
                //floodFill(dst,mask,Point(i,j),newcolor,0,colorDiff,colorDiff);
            }*/
    imshow("dst",dst);
}


int main3(int argc, uchar* argv[])
{

    namedWindow("src",WINDOW_AUTOSIZE);
    namedWindow("dst",WINDOW_AUTOSIZE);
    VideoCapture capture(0);
    capture>>src;
    cv::Size siz=cv::Size(320,240);
    cv::resize(src,src,siz);
    //src=imread("stuff.jpg");
    CV_Assert(!src.empty());

    spatialRad=30;
    colorRad=20;
    maxPryLevel=1;

    //虽然createTrackbar函数的参数onChange函数要求其2个参数形式为onChange(int,void*)
    //但是这里是系统响应函数，在使用createTrackbar函数时，其调用的函数可以不用写参数，甚至
    //括号都不用写，但是其调用函数的实现过程中还是需要满足(int,void*)2个参数类型
    createTrackbar("spatialRad","dst",&spatialRad,80,meanshift_seg);
    createTrackbar("colorRad","dst",&colorRad,60,meanshift_seg);
    createTrackbar("maxPryLevel","dst",&maxPryLevel,5,meanshift_seg);

    //meanshift_seg(0,0);

    imshow("src",src);
    /*char c=(char)waitKey();
    if(27==c)
        return 0;*/
    imshow("dst",src);
    waitKey();//无限等待用户交互响应
//    while(1);//这里不能用while(1)的原因是需要等待用户的交互，而while(1)没有该功能。虽然2者都有无限等待的作用。
    return 0;
}
//#include "stdafx.h"
#include "cv.h"
#include "highgui.h"
#define  u_char unsigned char
#define  DIST 0.5
#define  NUM 20

//全局变量
bool pause1 = false;
bool is_tracking = false;
CvRect drawing_box;
IplImage *current;
double *hist1, *hist2;
double *m_wei;																	//权值矩阵
double C = 0.0;																   //归一化系数

void init_target(double *hist1, double *m_wei, IplImage *current)
{
    IplImage *pic_hist = 0;
    int t_h, t_w, t_x, t_y;
    double h, dist;
    int i, j;
    int q_r, q_g, q_b, q_temp;

    t_h = drawing_box.height;
    t_w = drawing_box.width;
    t_x = drawing_box.x;
    t_y = drawing_box.y;

    h = pow(((double)t_w)/2,2) + pow(((double)t_h)/2,2);			//带宽
    pic_hist = cvCreateImage(cvSize(300,200),IPL_DEPTH_8U,3);     //生成直方图图像

    //初始化权值矩阵和目标直方图
    for (i = 0;i < t_w*t_h;i++)
    {
        m_wei[i] = 0.0;
    }

    for (i=0;i<4096;i++)
    {
        hist1[i] = 0.0;
    }

    for (i = 0;i < t_h; i++)
    {
        for (j = 0;j < t_w; j++)
        {
            dist = pow(i - (double)t_h/2,2) + pow(j - (double)t_w/2,2);
            m_wei[i * t_w + j] = 1 - dist / h;
            //printf("%f\n",m_wei[i * t_w + j]);
            C += m_wei[i * t_w + j] ;
        }
    }

    //计算目标权值直方
    for (i = t_y;i < t_y + t_h; i++)
    {
        for (j = t_x;j < t_x + t_w; j++)
        {
            //rgb颜色空间量化为16*16*16 bins
            q_r = ((u_char)current->imageData[i * current->widthStep + j * 3 + 2]) / 16;
            q_g = ((u_char)current->imageData[i * current->widthStep + j * 3 + 1]) / 16;
            q_b = ((u_char)current->imageData[i * current->widthStep + j * 3 + 0]) / 16;
            q_temp = q_r * 256 + q_g * 16 + q_b;
            hist1[q_temp] =  hist1[q_temp] +  m_wei[(i - t_y) * t_w + (j - t_x)] ;
        }
    }

    //归一化直方图
    for (i=0;i<4096;i++)
    {
        hist1[i] = hist1[i] / C;
        //printf("%f\n",hist1[i]);
    }

    //生成目标直方图
    double temp_max=0.0;

    for (i = 0;i < 4096;i++)			//求直方图最大值，为了归一化
    {
        //printf("%f\n",val_hist[i]);
        if (temp_max < hist1[i])
        {
            temp_max = hist1[i];
        }
    }
    //画直方图
    CvPoint p1,p2;
    double bin_width=(double)pic_hist->width/4096;
    double bin_unith=(double)pic_hist->height/temp_max;

    for (i = 0;i < 4096; i++)
    {
        p1.x = i * bin_width;
        p1.y = pic_hist->height;
        p2.x = (i + 1)*bin_width;
        p2.y = pic_hist->height - hist1[i] * bin_unith;
        //printf("%d,%d,%d,%d\n",p1.x,p1.y,p2.x,p2.y);
        cvRectangle(pic_hist,p1,p2,cvScalar(0,255,0),-1,8,0);
    }
    cvSaveImage("hist1.jpg",pic_hist);
    cvReleaseImage(&pic_hist);
}

void MeanShift_Tracking(IplImage *current)
{
    int num = 0, i = 0, j = 0;
    int t_w = 0, t_h = 0, t_x = 0, t_y = 0;
    double *w = 0, *hist2 = 0;
    double sum_w = 0, x1 = 0, x2 = 0,y1 = 2.0, y2 = 2.0;
    int q_r, q_g, q_b;
    int *q_temp;
    IplImage *pic_hist = 0;

    t_w = drawing_box.width;
    t_h = drawing_box.height;

    pic_hist = cvCreateImage(cvSize(300,200),IPL_DEPTH_8U,3);     //生成直方图图像
    hist2 = (double *)malloc(sizeof(double)*4096);
    w = (double *)malloc(sizeof(double)*4096);
    q_temp = (int *)malloc(sizeof(int)*t_w*t_h);

    while ((pow(y2,2) + pow(y1,2) > 0.5)&& (num < NUM))
    {
        num++;
        t_x = drawing_box.x;
        t_y = drawing_box.y;
        memset(q_temp,0,sizeof(int)*t_w*t_h);
        for (i = 0;i<4096;i++)
        {
            w[i] = 0.0;
            hist2[i] = 0.0;
        }

        for (i = t_y;i < t_h + t_y;i++)
        {
            for (j = t_x;j < t_w + t_x;j++)
            {
                //rgb颜色空间量化为16*16*16 bins
                q_r = ((u_char)current->imageData[i * current->widthStep + j * 3 + 2]) / 16;
                q_g = ((u_char)current->imageData[i * current->widthStep + j * 3 + 1]) / 16;
                q_b = ((u_char)current->imageData[i * current->widthStep + j * 3 + 0]) / 16;
                q_temp[(i - t_y) *t_w + j - t_x] = q_r * 256 + q_g * 16 + q_b;
                hist2[q_temp[(i - t_y) *t_w + j - t_x]] =  hist2[q_temp[(i - t_y) *t_w + j - t_x]] +  m_wei[(i - t_y) * t_w + j - t_x] ;
            }
        }

        //归一化直方图
        for (i=0;i<4096;i++)
        {
            hist2[i] = hist2[i] / C;
            //printf("%f\n",hist2[i]);
        }
        //生成目标直方图
        double temp_max=0.0;

        for (i=0;i<4096;i++)			//求直方图最大值，为了归一化
        {
            if (temp_max < hist2[i])
            {
                temp_max = hist2[i];
            }
        }
        //画直方图
        CvPoint p1,p2;
        double bin_width=(double)pic_hist->width/(4368);
        double bin_unith=(double)pic_hist->height/temp_max;

        for (i = 0;i < 4096; i++)
        {
            p1.x = i * bin_width;
            p1.y = pic_hist->height;
            p2.x = (i + 1)*bin_width;
            p2.y = pic_hist->height - hist2[i] * bin_unith;
            cvRectangle(pic_hist,p1,p2,cvScalar(0,255,0),-1,8,0);
        }
        cvSaveImage("hist2.jpg",pic_hist);

        for (i = 0;i < 4096;i++)
        {
            if (hist2[i] != 0)
            {
                w[i] = sqrt(hist1[i]/hist2[i]);
            }else
            {
                w[i] = 0;
            }
        }

        sum_w = 0.0;
        x1 = 0.0;
        x2 = 0.0;

        for (i = 0;i < t_h; i++)
        {
            for (j = 0;j < t_w; j++)
            {
                //printf("%d\n",q_temp[i * t_w + j]);
                sum_w = sum_w + w[q_temp[i * t_w + j]];
                x1 = x1 + w[q_temp[i * t_w + j]] * (i - t_h/2);
                x2 = x2 + w[q_temp[i * t_w + j]] * (j - t_w/2);
            }
        }
        y1 = x1 / sum_w;
        y2 = x2 / sum_w;

        //中心点位置更新
        drawing_box.x += y2;
        drawing_box.y += y1;

        //printf("%d,%d\n",drawing_box.x,drawing_box.y);
    }
    free(hist2);
    free(w);
    free(q_temp);
    //显示跟踪结果
    cvRectangle(current,cvPoint(drawing_box.x,drawing_box.y),cvPoint(drawing_box.x+drawing_box.width,drawing_box.y+drawing_box.height),CV_RGB(255,0,0),2);
    cvShowImage("Meanshift",current);
    //cvSaveImage("result.jpg",current);
    cvReleaseImage(&pic_hist);
}

void onMouse( int event, int x, int y, int flags, void *param )
{
    if (pause1)
    {
        switch(event)
        {
        case CV_EVENT_LBUTTONDOWN:
            //the left up point of the rect
            drawing_box.x=x;
            drawing_box.y=y;
            break;
        case CV_EVENT_LBUTTONUP:
            //finish drawing the rect (use color green for finish)
            drawing_box.width=x-drawing_box.x;
            drawing_box.height=y-drawing_box.y;
            cvRectangle(current,cvPoint(drawing_box.x,drawing_box.y),cvPoint(drawing_box.x+drawing_box.width,drawing_box.y+drawing_box.height),CV_RGB(255,0,0),2);
            cvShowImage("Meanshift",current);

            //目标初始化
            hist1 = (double *)malloc(sizeof(double)*16*16*16);
            m_wei =  (double *)malloc(sizeof(double)*drawing_box.height*drawing_box.width);
            init_target(hist1, m_wei, current);
            is_tracking = true;
            break;
        }
        return;
    }
}



int main4(int argc, uchar* argv[])
{
    CvCapture *capture=cvCreateCameraCapture(0);
    current = cvQueryFrame(capture);
    char res[20];
    int nframe = 0;

    while (1)
    {
    /*	sprintf(res,"result%d.jpg",nframe);
        cvSaveImage(res,current);
        nframe++;*/
        if(is_tracking)
        {
            MeanShift_Tracking(current);
        }

        int c=cvWaitKey(1);
        //暂停
        if(c == 'p')
        {
            pause1 = true;
            cvSetMouseCallback( "Meanshift", onMouse, 0 );
        }
        while(pause1){
            if(cvWaitKey(0) == 'p')
                pause1 = false;
        }
        cvShowImage("Meanshift",current);
        current = cvQueryFrame(capture); //抓取一帧
    }

    cvNamedWindow("Meanshift",1);
    cvReleaseCapture(&capture);
    cvDestroyWindow("Meanshift");
    return 0;
}

