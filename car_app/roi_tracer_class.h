#ifndef ROI_TRACER_CLASS_H
#define ROI_TRACER_CLASS_H
#include "infotables.h"
#include "pthread.h"
#include "opencv2/opencv.hpp"
using namespace cv;
using namespace std;
class ROI_Tracer_Class
{
public:
    ROI_Tracer_Class();
    ~ROI_Tracer_Class();
    void trace_process();
    void set_Trace_Taget(Rect target);
    bool paused;
    Mat image;
    Mat *img_pt;

    bool backprojMode ; //表示是否要进入反向投影模式，ture表示准备进入反向投影模式
    bool selectObject ;//代表是否在选要跟踪的初始目标，true表示正在用鼠标选择
    int trackObject ; //代表跟踪目标数目
    bool showHist ;//是否显示直方图
    Point origin;//用于保存鼠标选择第一次单击时点的位置
    Rect selection;//用于保存鼠标选择的矩形框
    int vmin, vmax , smin ;
    pthread_t cam_shift_thread;
    int command_key;
    RotatedRect trace_res;
    Rect trackWindow;
    RotatedRect trackBox;//定义一个旋转的矩阵类对象
    int hsize ;
    float hranges[] ;//= {0,180};//hranges在后面的计算直方图函数中要用到
    const float* phranges;// = hranges;

    Mat frame, hsv, hue, mask, hist, histimg ,backproj;

};

#endif // ROI_TRACER_CLASS_H
