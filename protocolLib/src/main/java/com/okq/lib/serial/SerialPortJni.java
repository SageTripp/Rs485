package com.okq.lib.serial;

import java.io.FileDescriptor;

/**
 * Created by zst on 2016-08-17  0017.
 * 描述:
 */

public class SerialPortJni {

    static {
        System.loadLibrary("SerialPort");
    }

    public FileDescriptor mFd;

    public String mDevNum;
    public int mSpeed;
    public int mDataBits;
    public int mStopBits;
    public int mParity;

    public int RS485ModFp = -1;

    public static int RS485Read = 0;
    public static int RS485Write = 1;

    public native int setSpeed(FileDescriptor fd, int speed);

    public native int setParity(FileDescriptor fd, int dataBits, int stopBits,
                                int parity);

    public native FileDescriptor openDev(String devNum);

    public native FileDescriptor open485Dev(String devNum);

    public native int closeDev(FileDescriptor fd);

    public native int close485Dev(FileDescriptor fd);

    public native int readBytes(FileDescriptor fd, byte[] buffer, int length);

    public native boolean writeBytes(FileDescriptor fd, byte[] buffer,
                                     int length);

    public native int set485mod(int mode);
}
