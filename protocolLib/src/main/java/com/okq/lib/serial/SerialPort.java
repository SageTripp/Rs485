package com.okq.lib.serial;

import android.content.Context;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 自定义串口对象
 *
 * @author F
 */
public class SerialPort {
    Context context;

    /**
     * 自定义串口对象
     */
    private static SerialPort serialPort;

    /**
     * 调用JNI的串口
     */
    private SerailPortOpt serialportopt;

    /**
     * 读取数据线程
     */

    /**
     * 串口接收到的流
     */
    private InputStream mInputStream;

    /**
     * 用来判断串口是否已打开
     */
    public boolean isOpen = false;

    /*
     * 接收到的数据
     */
    String data;

    /**
     * 实例化并打开串口对象
     *
     * @param devNum   串口号 S0，S1，S2，S3,S4
     * @param dataBits 数据位
     * @param speed    波特率
     * @param stopBits 停止位
     * @param parity   校验位
     */
    public SerialPort(String devNum, int speed, int dataBits, int stopBits,
                      int parity) {
        serialportopt = new SerailPortOpt();
        openSerial(devNum, speed, dataBits, stopBits, parity);
    }

    /**
     * 打开串口时传入参数，可以指定打开某个串口，并设置相应的参数
     *
     * @param devNum   串口号 COM0，COM1，COM2，COM3,COM4
     * @param dataBits 数据位
     * @param speed    波特率
     * @param stopBits 停止位
     * @param parity   校验位
     * @return
     */
    private boolean openSerial(String devNum, int speed, int dataBits,
                               int stopBits, int parity) {
        serialportopt.mDevNum = devNum;
        serialportopt.mDataBits = dataBits;
        serialportopt.mSpeed = speed;
        serialportopt.mStopBits = stopBits;
        serialportopt.mParity = parity;

        // 打开串口
//        FileDescriptor fd = serialportopt.openDev(serialportopt.mDevNum);
        FileDescriptor fd = serialportopt.open485Dev(serialportopt.mDevNum);
        if (fd == null) {
            return false;// 串口打开失败
        } else {
            // 设置串口参数
            serialportopt.setSpeed(fd, speed);
            serialportopt.setParity(fd, dataBits, stopBits, parity);
            serialportopt.set485mod(1);
            mInputStream = serialportopt.getInputStream();
            isOpen = true;
            return true;
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerial() {
        if (serialportopt.mFd != null) {
            serialportopt.close485Dev(serialportopt.mFd);
            isOpen = false;
        }
    }

    /**
     * 发送数据
     *
     * @param data 数据内容
     */
    public void sendData(String data, String type) {
        try {
            Log.i("", "sendData: "+data);
            byte[] hices = type.equals("HEX") ? HexString2Bytes(data
                    .length() % 2 == 1 ? data += "0" : data.replace(" ", ""))
                    : HexString2Bytes(toHexString(data));
            for (int i = 0; i < hices.length; i++) {
                Log.i("", "sendData: "+hices[i]);
            }
            serialportopt.write485Bytes(hices);
        } catch (Exception e) {

        }
    }

    /**
     * 接收数据
     *
     * @param type 收发数据类型
     * @return 接收到的字符串
     */
    public String receiveData(String type) {
        byte[] buf = new byte[1024];
        int size;
        if (mInputStream == null) {
            return null;
        }
        size = serialportopt.read485Bytes(buf);
        if (size > 0) {
            try {
                data = type.equals("HEX") ? bytesToHexString(buf, size)
                        : new String(buf, 0, size, "gb2312").trim().toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return data;
        } else {
            return null;
        }
    }

    /**
     * 转化字符串为十六进制编码
     *
     * @param s
     * @return
     */
    private String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
     * 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    private static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 将Hex数组转换为Hex字符串
     *
     * @param src
     * @param size
     * @return
     */
    public static String bytesToHexString(byte[] src, int size) {
        String ret = "";
        if (src == null || size <= 0) {
            return null;
        }
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            hex += " ";
            ret += hex;
        }
        return ret.toUpperCase();
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

}
