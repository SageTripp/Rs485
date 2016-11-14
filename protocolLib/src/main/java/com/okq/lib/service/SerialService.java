package com.okq.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.okq.lib.serialPort.SerialPort;

import java.io.File;
import java.io.IOException;


/**
 * Created by zst on 2016/3/8. USB后台服务
 */
public class SerialService extends Service {

    static SerialPort serialPort;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            serialPort = new SerialPort(new File("/dev/ttyS4"), 9600, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        serialPort.close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 检查Usb设备连接状态
     *
     * @return true:已连接 false:未连接
     */
    public static boolean checkUsb() {
        return null != serialPort && serialPort.isOpen;
    }

    public static SerialPort getDev() {
        return serialPort;
    }
}
