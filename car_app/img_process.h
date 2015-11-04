#ifndef IMG_PROCESS_H
#define IMG_PROCESS_H
#include <opencv2/opencv.hpp>
#include "v4lcam.h"
cv::Mat get_cam_img(V4lCam*cam);
void img_process_loop_body();
#endif // IMG_PROCESS_H
