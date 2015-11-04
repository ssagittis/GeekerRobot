#-------------------------------------------------
#
# Project created by QtCreator 2015-06-17T08:36:50
#
#-------------------------------------------------

QT       += gui core

#QT       -= gui

TARGET = car_app
CONFIG   += console
#CONFIG   -= app_bundle

TEMPLATE = app


SOURCES += \
    i2c_demo.cpp\
     HMC.cpp \
    car_process.cpp\
    tcp_trans.cpp \
    helloworld.cpp \
    v4lcam.cpp \
    img_process.cpp \
    infotables.cpp \
    roi_tracer_process.cpp \
    roi_tracer_class.cpp







LIBS += /home/lin/development/zedBoard_lib/lib/libopencv_core.so\
             /home/lin/development/zedBoard_lib/lib/libopencv_highgui.so\
            /home/lin/development/zedBoard_lib/lib/libopencv_features2d.so\
            /home/lin/development/zedBoard_lib/lib/libopencv_imgproc.so\
            //home/lin/development/zedBoard_lib/lib/libopencv_contrib.so\
            /home/lin/development/zedBoard_lib/lib/libopencv_ml.so  \
            /home/lin/development/zedBoard_lib/lib/libopencv_video.so  \
            /home/lin/development/zedBoard_lib/lib/libopencv_objdetect.so
INCLUDEPATH +=/home/lin/development/zedBoard_lib/include
HEADERS += \
    tcp_trans.h \
    i2c.h \
    HMC.h \
    car_process.h \
    v4lcam.h \
    img_process.h \
    infotables.h \
    roi_tracer_process.h \
    roi_tracer_class.h

