#-------------------------------------------------
#
# Project created by QtCreator 2015-06-17T16:17:07
#
#-------------------------------------------------

QT       += gui core

#QT       -= gui

TARGET = car_app
CONFIG   += console
#CONFIG   -= app_bundle

TEMPLATE = app


SOURCES += \
    main.cpp








LIBS += /usr/local/lib/libopencv_core.so\
             /usr/local/lib/libopencv_highgui.so\
            /usr/local/lib/libopencv_features2d.so\
            /usr/local/lib/libopencv_imgproc.so\
            /usr/local/lib/libopencv_contrib.so\
            /usr/local/lib/libopencv_ml.so  \
            /usr/local/lib/libopencv_objdetect.so\
            /usr/local/lib/libopencv_video.so
INCLUDEPATH +=/usr/local/include/opencv
HEADERS += \

