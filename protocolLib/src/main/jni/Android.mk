#
#
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_LDLIBS := -lm -llog
LOCAL_MODULE := SerialPort
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	E:\work\lib\Rs485\protocolLib\src\main\jni\SerialPort.c \

LOCAL_C_INCLUDES += E:\work\lib\Rs485\protocolLib\src\main\jni
LOCAL_C_INCLUDES += E:\work\lib\Rs485\protocolLib\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
