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
    motors.cpp \
    infotables.cpp \
    roi_tracer_process.cpp \
    roi_tracer_class.cpp








LIBS += /usr/local/lib/libopencv_core.so\
             /usr/local/lib/libopencv_highgui.so\
            /usr/local/lib/libopencv_features2d.so\
            /usr/local/lib/libopencv_imgproc.so\
            /usr/local/lib/libopencv_contrib.so\
            /usr/local/lib/libopencv_ml.so  \
            /usr/local/lib/libopencv_objdetect.so \
            /usr/local/lib/libopencv_video.so
INCLUDEPATH +=/usr/local/include/opencv
HEADERS += \
    tcp_trans.h \
    i2c.h \
    HMC.h \
    car_process.h \
    v4lcam.h \
    img_process.h \
    motors.h \
    infotables.h \
    roi_tracer_process.h \
    roi_tracer_class.h

