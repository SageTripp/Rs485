package com.okq.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.okq.lib.serial.SerialPort;

/**
 * Created by zst on 2016/3/8. USB后台服务
 */
public class SerialService extends Service {

    static SerialPort serialPort;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serialPort = new SerialPort("S0", 9600, 8, 1, 0);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        serialPort.closeSerial();
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
