LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_ROOT:=/Users/itoyuichi/Library/Android/OpenCV-android-sdk3/
OPENCV_CAMERA_MODULES:=on
OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on
include ${OPENCV_ROOT}/sdk/native/jni/OpenCV.mk

NDK_MODULE_PATH=/Users/itoyuichi/Library/Android/sdk/ndk-bundle
LOCAL_ARM_NEON := true
LOCAL_SRC_FILES := ImageProcessing.cpp

LOCAL_MODULE    := ImageProcessing
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
