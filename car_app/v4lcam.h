#ifndef V4LCAM_H
#define V4LCAM_H
#include <stdio.h>

//#define FILE_VIDEO "/dev/video0"

#define  TRUE	1
#define  FALSE	0


#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/ioctl.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <linux/types.h>
#include <linux/videodev2.h>
#include <string>

#define  FREE(x)       if((x)){free((x));(x)=NULL;}



typedef unsigned char  BYTE;
typedef unsigned short	WORD;
typedef unsigned long  DWORD;

/**/
#pragma pack(1)

typedef struct tagBITMAPFILEHEADER{
     WORD	bfType;                // the flag of bmp, value is "BM"
     DWORD    bfSize;                // size BMP file ,unit is bytes
     DWORD    bfReserved;            // 0
     DWORD    bfOffBits;             // must be 54

}BITMAPFILEHEADER;


typedef struct tagBITMAPINFOHEADER{
     DWORD    biSize;                // must be 0x28
     DWORD    biWidth;           //
     DWORD    biHeight;          //
     WORD		biPlanes;          // must be 1
     WORD		biBitCount;            //
     DWORD    biCompression;         //
     DWORD    biSizeImage;       //
     DWORD    biXPelsPerMeter;   //
     DWORD    biYPelsPerMeter;   //
     DWORD    biClrUsed;             //
     DWORD    biClrImportant;        //
}BITMAPINFOHEADER;

typedef struct tagRGBQUAD{
     BYTE	rgbBlue;
     BYTE	rgbGreen;
     BYTE	rgbRed;
     BYTE	rgbReserved;
}RGBQUAD;

typedef struct{
    int width;
    int height;
}V4Lsize;


typedef struct{
    void *start;
    unsigned length;
}buffer;
class V4lCam
{
public:
    V4lCam();
    struct v4l2_buffer camera_buf;
    void init(int width, int height, int camNumber);
    std::string FILE_VIDEO;

    buffer *buffers;

//enum v4l2_buf_type type;
//unsigned char frame_buffer [IMAGEWIDTH*IMAGEHEIGHT*3];

void startCamStream();
int init_v4l2(void);
struct v4l2_buffer imgBuf;
buffer *getFrameData();
void releaseCap();
V4Lsize  imgSize();
private:
int PicState;
unsigned short IMAGEWIDTH;
unsigned short IMAGEHEIGHT;
int  fd;

};


#endif // V4LCAM_H
