#include "roi_tracer_class.h"

ROI_Tracer_Class::ROI_Tracer_Class()
{
    backprojMode = false; //表示是否要进入反向投影模式，ture表示准备进入反向投影模式
    selectObject = false;//代表是否在选要跟踪的初始目标，true表示正在用鼠标选择
    trackObject = 0; //代表跟踪目标数目
    showHist = true;//是否显示直方图
    vmin = 10; vmax = 256; smin = 30;
    //img_pt=img;
    command_key=0;
    trace_res=RotatedRect();
     hsize = 16;
    hranges[0] = 0;hranges[1]=180;//{0,180};//hranges在后面的计算直方图函数中要用到
    phranges = hranges;
     histimg = Mat::zeros(200, 320, CV_8UC3);
    paused = false;
    //pthread_create(cam_shift_thread,0,trace_process,0);
}
void ROI_Tracer_Class::set_Trace_Taget(Rect target){
    selection=target;
    trackObject=-1;
}
ROI_Tracer_Class::~ROI_Tracer_Class(){
    pthread_cancel(cam_shift_thread);
    }

void ROI_Tracer_Class:: trace_process(){
   // VideoCapture cap; //定义一个摄像头捕捉的类对象


        if( !paused )//没有按暂停键
        {
            image=*img_pt;
            if(image.empty()){sleep(2);return;};
            cvtColor(image, hsv, CV_BGR2HSV);//将rgb摄像头帧转化成hsv空间的

            if( trackObject )//trackObject初始化为0,或者按完键盘的'c'键后也为0，当鼠标单击松开后为-1
            {
                int _vmin = vmin, _vmax = vmax;

                //inRange函数的功能是检查输入数组每个元素大小是否在2个给定数值之间，可以有多通道,mask保存0通道的最小值，也就是h分量
//这里利用了hsv的3个通道，比较h,0~180,s,smin~256,v,min(vmin,vmax),max(vmin,vmax)。如果3个通道都在对应的范围内，则
//mask对应的那个点的值全为1(0xff)，否则为0(0x00).
                inRange(hsv, Scalar(0, smin, MIN(_vmin,_vmax)),
                        Scalar(180, 256, MAX(_vmin, _vmax)), mask);
                int ch[] = {0, 0};
                hue.create(hsv.size(), hsv.depth());//hue初始化为与hsv大小深度一样的矩阵，色调的度量是用角度表示的，红绿蓝之间相差120度，反色相差180度
                mixChannels(&hsv, 1, &hue, 1, ch, 1);//将hsv第一个通道(也就是色调)的数复制到hue中，0索引数组

                if( trackObject < 0 )//鼠标选择区域松开后，该函数内部又将其赋值1
                {
                    //此处的构造函数roi用的是Mat hue的矩阵头，且roi的数据指针指向hue，即共用相同的数据，select为其感兴趣的区域
                    Mat roi(hue, selection), maskroi(mask, selection);//mask保存的hsv的最小值

//calcHist()函数第一个参数为输入矩阵序列，第2个参数表示输入的矩阵数目，第3个参数表示将被计算直方图维数通道的列表，第4个参数表示可选的掩码函数
//第5个参数表示输出直方图，第6个参数表示直方图的维数，第7个参数为每一维直方图数组的大小，第8个参数为每一维直方图bin的边界
                    calcHist(&roi, 1, 0, maskroi, hist, 1, &hsize, &phranges);//将roi的0通道计算直方图并通过mask放入hist中，hsize为每一维直方图的大小
                    normalize(hist, hist, 0, 255, CV_MINMAX);//将hist矩阵进行数组范围归一化，都归一化到0~255

                    trackWindow = selection;
                    trackObject = 1;//只要鼠标选完区域松开后，且没有按键盘清0键'c'，则trackObject一直保持为1，因此该if函数只能执行一次，除非重新选择跟踪区域

                    histimg = Scalar::all(0);//与按下'c'键是一样的，这里的all(0)表示的是标量全部清0
                    int binW = histimg.cols / hsize;  //histing是一个200*300的矩阵，hsize应该是每一个bin的宽度，也就是histing矩阵能分出几个bin出来
                    Mat buf(1, hsize, CV_8UC3);//定义一个缓冲单bin矩阵
                    for( int i = 0; i < hsize; i++ )//saturate_case函数为从一个初始类型准确变换到另一个初始类型
                        buf.at<Vec3b>(i) = Vec3b(saturate_cast<uchar>(i*180./hsize), 255, 255);//Vec3b为3个char值的向量
                    cvtColor(buf, buf, CV_HSV2BGR);//将hsv又转换成bgr

                    for( int i = 0; i < hsize; i++ )
                    {
                        int val = saturate_cast<int>(hist.at<float>(i)*histimg.rows/255);//at函数为返回一个指定数组元素的参考值
                        rectangle( histimg, Point(i*binW,histimg.rows),    //在一幅输入图像上画一个简单抽的矩形，指定左上角和右下角，并定义颜色，大小，线型等
                                   Point((i+1)*binW,histimg.rows - val),
                                   Scalar(buf.at<Vec3b>(i)), -1, 8 );
                    }
                }

                calcBackProject(&hue, 1, 0, hist, backproj, &phranges);//计算直方图的反向投影，计算hue图像0通道直方图hist的反向投影，并让入backproj中
                backproj &= mask;

                //opencv2.0以后的版本函数命名前没有cv两字了，并且如果函数名是由2个意思的单词片段组成的话，且前面那个片段不够成单词，则第一个字母要
//大写，比如Camshift，如果第一个字母是个单词，则小写，比如meanShift，但是第二个字母一定要大写
                RotatedRect trackBox = CamShift(backproj, trackWindow,               //trackWindow为鼠标选择的区域，TermCriteria为确定迭代终止的准则
                                    TermCriteria( CV_TERMCRIT_EPS | CV_TERMCRIT_ITER, 10, 1 ));//CV_TERMCRIT_EPS是通过forest_accuracy,CV_TERMCRIT_ITER
                if( trackWindow.area() <= 1 )                                                  //是通过max_num_of_trees_in_the_forest
                {
                    int cols = backproj.cols, rows = backproj.rows, r = (MIN(cols, rows) + 5)/6;
                    trackWindow = Rect(trackWindow.x - r, trackWindow.y - r,
                                       trackWindow.x + r, trackWindow.y + r) &
                                  Rect(0, 0, cols, rows);//Rect函数为矩阵的偏移和大小，即第一二个参数为矩阵的左上角点坐标，第三四个参数为矩阵的宽和高
                }

                if( backprojMode )
                    cvtColor( backproj, image, CV_GRAY2BGR );//因此投影模式下显示的也是rgb图？
                ellipse( image, trackBox, Scalar(0,0,255), 3, CV_AA );//跟踪的时候以椭圆为代表目标
            }
        }

        //后面的代码是不管pause为真还是为假都要执行的
        else if( trackObject < 0 )//同时也是在按了暂停字母以后
            paused = false;

        if( selectObject && selection.width > 0 && selection.height > 0 )
        {
            Mat roi(image, selection);
            bitwise_not(roi, roi);//bitwise_not为将每一个bit位取反
        }

        //imshow( "CamShift Demo", image );
        //imshow( "Histogram", histimg );
      //  waitKey(30);
        usleep(30000);
        char c = command_key;
        switch(c)
        {
        case 'b':             //反向投影模型交替
            backprojMode = !backprojMode;
            break;
        case 'c':            //清零跟踪目标对象
            trackObject = 0;
            histimg = Scalar::all(0);
            break;
        case 'h':          //显示直方图交替
            showHist = !showHist;

            break;
        case 'p':       //暂停跟踪交替
            paused = !paused;
            break;
        default:
            ;
        }

    return ;
}
