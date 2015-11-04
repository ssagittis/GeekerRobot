#-------------------------------------------------
#
# Project created by QtCreator 2015-07-03T19:21:30
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = robot_pc_client
TEMPLATE = app


SOURCES += main.cpp\
        mainwindow.cpp \
    tcp_trans.cpp

HEADERS  += mainwindow.h \
    tcp_trans.h \
    infotables.h

FORMS    += mainwindow.ui


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


