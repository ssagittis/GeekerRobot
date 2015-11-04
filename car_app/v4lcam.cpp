#include "v4lcam.h"
/*
 *
 *
 */
#include "asm-generic/errno-base.h"
#include <string>
#include <sstream>
V4lCam::V4lCam()
{

}
void V4lCam::init(int width,int height,int camNumber){
    int p[]={2,10,0};
    IMAGEWIDTH=width;
    IMAGEHEIGHT=height;

    switch(camNumber){
    case 0: FILE_VIDEO="/dev/video0";break;
    case 1: FILE_VIDEO="/dev/video1";break;
     default :FILE_VIDEO="/dev/video0";break;
    }


    //unsigned char tmpBuf[3*IMGWIDTH*IMGHEIGHT];
    //long int i;
    imgBuf.type=V4L2_BUF_TYPE_VIDEO_CAPTURE;
    imgBuf.memory=V4L2_MEMORY_MMAP;

    init_v4l2();
    startCamStream();
    PicState=0;
}

int V4lCam::init_v4l2(void)
{

    static   struct   v4l2_capability   cap;
    struct v4l2_fmtdesc fmtdesc;
    struct v4l2_format fmt;

    struct v4l2_requestbuffers req;



    //opendev
    if ((fd = open(FILE_VIDEO.data(), O_RDWR| O_NONBLOCK)) == -1)
    {
        printf("Error opening V4L interface\n");
        return (FALSE);
    }

    //query cap
    if (ioctl(fd, VIDIOC_QUERYCAP, &cap) == -1)
    {
        printf("Error opening device %s: unable to query device.\n",FILE_VIDEO.data());
        return (FALSE);
    }
    else
    {
        printf("driver:\t\t%s\n",cap.driver);
        printf("card:\t\t%s\n",cap.card);
        printf("bus_info:\t%s\n",cap.bus_info);
        printf("version:\t%d\n",cap.version);
        printf("capabilities:\t%x\n",cap.capabilities);

        if ((cap.capabilities & V4L2_CAP_VIDEO_CAPTURE) == V4L2_CAP_VIDEO_CAPTURE)
        {
            printf("Device %s: supports capture.\n",FILE_VIDEO.data());
        }

        if ((cap.capabilities & V4L2_CAP_STREAMING) == V4L2_CAP_STREAMING)
        {
            printf("Device %s: supports streaming.\n",FILE_VIDEO.data());
        }
    }

    //emu all support fmt
    fmtdesc.index=0;
    fmtdesc.type=V4L2_BUF_TYPE_VIDEO_CAPTURE;
    printf("Support format:\n");
    while(ioctl(fd,VIDIOC_ENUM_FMT,&fmtdesc)!=-1)
    {
        printf("\t%d.%s\n",fmtdesc.index+1,fmtdesc.description);
        fmtdesc.index++;
    }

    //set fmt
    fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    fmt.fmt.pix.pixelformat = V4L2_PIX_FMT_YUYV;
    fmt.fmt.pix.height = IMAGEHEIGHT;
    fmt.fmt.pix.width = IMAGEWIDTH;
    fmt.fmt.pix.field = V4L2_FIELD_INTERLACED;

  ioctl(fd, VIDIOC_S_FMT, &fmt);
  ioctl(fd, VIDIOC_S_FMT, &fmt);
 ioctl(fd, VIDIOC_S_FMT, &fmt);
    if(ioctl(fd, VIDIOC_G_FMT, &fmt) == -1)
    {
        printf("Unable to get format\n");
        return FALSE;
    }else
    {
        printf("fmt.type:\t\t%d\n",fmt.type);
        printf("pix.pixelformat:\t%c%c%c%c\n",fmt.fmt.pix.pixelformat & 0xFF, (fmt.fmt.pix.pixelformat >> 8) & 0xFF,(fmt.fmt.pix.pixelformat >> 16) & 0xFF, (fmt.fmt.pix.pixelformat >> 24) & 0xFF);
        printf("pix.height:\t\t%d\n",fmt.fmt.pix.height);
        printf("pix.width:\t\t%d\n",fmt.fmt.pix.width);
        printf("pix.field:\t\t%d\n",fmt.fmt.pix.field);
        IMAGEHEIGHT=fmt.fmt.pix.height;
        IMAGEWIDTH=fmt.fmt.pix.width;
    }
    //set fps


    //申请4帧缓冲区
//	struct v4l2_requestbuffers req;
    req.count=4;
    req.type=V4L2_BUF_TYPE_VIDEO_CAPTURE;
    req.memory=V4L2_MEMORY_MMAP;
    ioctl(fd,VIDIOC_REQBUFS,&req);

    //获取缓冲帧信息
    ioctl(fd,VIDIOC_QUERYBUF,camera_buf);

    buffers=(buffer*)calloc(req.count,sizeof(*buffers));
    if(!buffers){
        printf("out of memoty\n");
        return FALSE;
    }

    //映射
    unsigned int n_buffers;
    for(n_buffers=0;n_buffers<req.count;++n_buffers){
        struct v4l2_buffer buf;

        buf.type=V4L2_BUF_TYPE_VIDEO_CAPTURE;
        buf.memory=V4L2_MEMORY_MMAP;
        buf.index=n_buffers;
        if (-1 == ioctl(fd, VIDIOC_QUERYBUF, &buf))
            {
            printf("Query Failed\n");
            exit(-1);
            };
        buffers[n_buffers].length=buf.length;
        //映射内存
        buffers[n_buffers].start=mmap (NULL,buf.length,PROT_READ | PROT_WRITE ,MAP_SHARED,fd, buf.m.offset);
        if(MAP_FAILED==buffers[n_buffers].start){
            printf("Map Failed\n");
            exit(-1);
        }

    }



    printf("init %s \t[OK]\n",FILE_VIDEO.data());



    return TRUE;
}

void V4lCam::startCamStream(){
    unsigned int i;
    enum v4l2_buf_type type;
    //设置帧频
    struct v4l2_streamparm setfps;
    ioctl(fd, VIDIOC_G_PARM, setfps);
    setfps.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    setfps.parm.capture.timeperframe.numerator = 10;
    setfps.parm.capture.timeperframe.denominator = 1;
    setfps.parm.capture.capability=V4L2_CAP_TIMEPERFRAME;
    ioctl(fd, VIDIOC_S_PARM, &setfps);
    // 将缓冲帧放入队列
    for (i = 0; i< 4; ++i)
    {
    struct v4l2_buffer buf;
    buf.type =V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory =V4L2_MEMORY_MMAP;
    buf.index = i;
    ioctl (fd,VIDIOC_QBUF, &buf);
    }
    type =V4L2_BUF_TYPE_VIDEO_CAPTURE;
    ioctl (fd,VIDIOC_STREAMON, &type);
}
buffer *V4lCam::getFrameData(){
    //取队列
    if(PicState){
        ioctl (fd, VIDIOC_QBUF,&imgBuf);
    }
    PicState=1;
    ioctl (fd,VIDIOC_DQBUF, &imgBuf);
    return &(buffers[imgBuf.index]);

}
void V4lCam::releaseCap(){
    if(PicState)  {
        enum v4l2_buf_type type;
        type=V4L2_BUF_TYPE_VIDEO_CAPTURE;

       // ioctl (fd, VIDIOC_QBUF,&imgBuf);
        int r;
        //do r = ioctl (fd, VIDIOC_STREAMOFF, &type);
          //      while ((-1 == r) &&   (EINTR=errno));
        if(ioctl(fd,VIDIOC_STREAMOFF,&type)==-1){
            printf("cant stop stream\n");
        }
        int i;
        for(i=0;i<4;i++){
         munmap( buffers[i].start,buffers[i].length);
        }
        // munmap (NULL,,PROT_READ | PROT_WRITE ,MAP_SHARED,fd, buf.m.offset);
        printf("stop stream\n");
        //ioctl(fd,VIDIOC_);
        }
    if(close(fd)==-1){
        printf("cant close cam\n");
    }
    printf("cap released\n");
}
V4Lsize V4lCam::imgSize(){
    V4Lsize siz;
    siz.width=IMAGEWIDTH;
    siz.height=IMAGEHEIGHT;
    return siz;
}


