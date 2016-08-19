package com.okq.lib.serial;

import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 调用JNI的串口
 *
 * @author zst
 */
public class SerailPortOpt extends SerialPortJni {

    private static final String TAG = "SerialPort";

    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public FileDescriptor openDev(String devNum) {
        super.mFd = super.openDev(devNum);
        if (super.mFd == null) {
            Log.e(TAG, "native open returns null");
            return null;
        }
        mFileInputStream = new FileInputStream(super.mFd);
        mFileOutputStream = new FileOutputStream(super.mFd);
        return super.mFd;
    }

    public FileDescriptor open485Dev(String devNum) {
        super.mFd = super.open485Dev(devNum);
        if (super.mFd == null) {
            Log.e(TAG, "native open returns null");
            return null;
        }
        mFileInputStream = new FileInputStream(super.mFd);
        mFileOutputStream = new FileOutputStream(super.mFd);
        return super.mFd;
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public int setSpeed(FileDescriptor optFd, int speed) {
        return super.setSpeed(optFd, speed);
    }

    public int setParity(FileDescriptor optFd, int databits, int stopbits,
                         int parity) {
        return super.setParity(optFd, databits, stopbits, parity);
    }

    public int closeDev(FileDescriptor optFd) {
        int retStatus;
        retStatus = super.closeDev(optFd);
        super.mFd = null;
        return retStatus;
    }

    public int close485Dev(FileDescriptor optFd) {
        int retStatus;
        retStatus = super.close485Dev(optFd);
        super.mFd = null;
        return retStatus;
    }

    public int readBytes(FileDescriptor fd, byte[] buffer, int length) {
        return super.readBytes(fd, buffer, length);
    }

    public boolean writeBytes(FileDescriptor fd, byte[] buffer, int length) {
        return super.writeBytes(fd, buffer, length);
    }

    public int readBytes(FileDescriptor fd, byte[] buffer) {
        return super.readBytes(fd, buffer, buffer.length);
    }

    public boolean writeBytes(FileDescriptor fd, byte[] buffer) {
        return super.writeBytes(fd, buffer, buffer.length);
    }

    public int readBytes(byte[] buffer) {
        return super.readBytes(mFd, buffer, buffer.length);
    }

    public boolean writeBytes(byte[] buffer) {
        return super.writeBytes(mFd, buffer, buffer.length);
    }

    public int read485Bytes(FileDescriptor fd, byte[] buffer, int length) {
        return super.readBytes(fd, buffer, length);
    }

    public boolean write485Bytes(FileDescriptor fd, byte[] buffer, int length) {
        boolean ret;
        super.set485mod(RS485Write);
        ret = super.writeBytes(fd, buffer, length);
        super.set485mod(RS485Read);
        return ret;
    }

    public int read485Bytes(FileDescriptor fd, byte[] buffer) {
        return super.readBytes(fd, buffer, buffer.length);
    }

    public boolean write485Bytes(FileDescriptor fd, byte[] buffer) {
        boolean ret;
        super.set485mod(RS485Write);
        ret = super.writeBytes(fd, buffer, buffer.length);
        super.set485mod(RS485Read);
        return ret;
    }

    public int read485Bytes(byte[] buffer) {
        return super.readBytes(mFd, buffer, buffer.length);
    }

    public boolean write485Bytes(byte[] buffer) {
        boolean ret;
        super.set485mod(RS485Write);
        ret = super.writeBytes(mFd, buffer, buffer.length);
        super.set485mod(RS485Read);
        return ret;
    }

}
