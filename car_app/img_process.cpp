#include "img_process.h"
#include "pthread.h"
#include "infotables.h"
#include "roi_tracer_class.h"
#include "car_process.h"
using namespace cv;
cv::Mat get_cam_img(V4lCam *cam){
    Mat cameraFrame(cam->imgSize().height,cam->imgSize().width,CV_8UC2,cam->getFrameData()->start);
   // videoCapture >> cameraFrame;
    if( cameraFrame.empty() ) {
        printf( "ERROR: Couldn't grab the next camera frame.");// << endl;
        exit(1);
    }
    //cout<<"imgget!\n";
    // Get a copy of the camera frame that we can draw onto.
    Mat displayedFrame;
    cvtColor(cameraFrame,displayedFrame,CV_YUV2RGB_YUYV);
    //tcp_ser_cam_thread(car_tcp_struct *tcp_str,cv::Mat &src)
  //  pthread_create();
    return displayedFrame;
}
//meanshift getObject opticalFlow trace
cv::Point get_trace_object(Mat &src, Rect roi){

}

void img_process_loop_body(){
    std::vector<using_item>::iterator it=device_using.begin();

    using_item item;
    Mat *matout=NULL;
    int i=0;
    for(;it!=device_using.end();it++){
        item=*it;
        switch(item.type){
        case DTYPE_ROITRACER:
            ROITRACER_Info*rinfo;
            rinfo=(ROITRACER_Info*)(get_device_info_struct(item.id));
           ROI_Tracer_Class*tracer;
           tracer= (ROI_Tracer_Class*)(((ROITRACER_Info*)get_device_info_struct(item.id))->traceclass);
           tracer->img_pt=(Mat*)(((Cam_Info*)get_device_info_struct( rinfo->cam_device_id))->Mat);
           tracer->trace_process();
            break;
         case DTYPE_CAM:
            Cam_Info *cami=(Cam_Info*)(get_device_info_struct(item.id));
            cam_process(cami);
            break;
        }
    }
}


