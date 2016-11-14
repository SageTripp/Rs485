/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.okq.lib.serialPort;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    public boolean isOpen = false;

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

		/* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        isOpen = true;
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            isOpen = false;
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    // JNI
    private native static FileDescriptor open(String path, int baudrate, int flags);

    public native void close();

    static {
        System.loadLibrary("serial_port");
    }

    public boolean sendData(String writeData) {
        if (null == getOutputStream()) return false;
        try {
            getOutputStream().write(hexString2Bytes(writeData));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String receiveData() {
        int size;
        try {
            byte[] buffer = new byte[128];
            if (getInputStream() == null) return "";
            size = getInputStream().read(buffer);
            if (size > 0) {
                String ret = bytesToHexString(buffer, size);
                System.out.println("read = [" + ret + "]");
                return ret;
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private byte[] hexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; ++i) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode(String.format("0x%s", new String(new byte[]{src0})));
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode(String.format("0x%s", new String(new byte[]{src1})));
        return (byte) (_b0 | _b1);
    }

    /**
     * 将Hex数组转换为Hex字符串
     */
    private String bytesToHexString(byte[] src, int size) {
        String ret = "";
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            ret += hex;
        }
        return ret.toUpperCase();
    }

}
