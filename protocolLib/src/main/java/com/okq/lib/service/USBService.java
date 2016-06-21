package com.okq.lib.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.okq.lib.U;

/**
 * Created by zst on 2016/3/8. USB后台服务
 */
public class USBService extends Service {

    static FT_Device ftDev = null;
    /**
     * 设备数目
     */
    private int devCount = -1;
    /**
     * 打开的设备的下标
     */
    private int openIndex = -1;
    /**
     * 当前的设备的下标
     */
    private int currentIndex = -1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        devCount = -1;
        createDeviceList();
        if (devCount > 0) {
            connectFunction();
            SetConfig();
            ftDev.restartInTask();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        disconnectFunction();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 设置设备串口参数
     */
    public void SetConfig() {
        if (!ftDev.isOpen()) {
            return;
        }
        // configure our port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);
        //设置波特率
        ftDev.setBaudRate(9600);
        //设置停止位,数据位...
        ftDev.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1, D2xxManager.FT_PARITY_NONE);
        ftDev.setFlowControl(D2xxManager.FT_FLOW_RTS_CTS, (byte) 0x0b, (byte) 0x0d);
        ftDev.clrDtr();
        ftDev.clrRts();
        ftDev.setRts();

    }

    /**
     * 生成设备列表
     */
    public void createDeviceList() {
        int tempDevCount = U.d2xxManager.createDeviceInfoList(this);

        if (tempDevCount > 0) {
            if (devCount != tempDevCount) {
                devCount = tempDevCount;
                openIndex = tempDevCount - 1;
            }
        } else {
            devCount = -1;
            currentIndex = -1;
        }
    }

    /**
     * 断开设备连接
     */
    public void disconnectFunction() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ftDev != null) {
            synchronized (ftDev) {
                if (ftDev.isOpen()) {
                    ftDev.close();
                }
            }
        }
    }

    /**
     * 连接设备
     */
    public void connectFunction() {
        if (currentIndex != openIndex) {
            if (null == ftDev) {
                ftDev = U.d2xxManager.openByIndex(this, openIndex);
            } else {
                synchronized (ftDev) {
                    ftDev = U.d2xxManager.openByIndex(this, openIndex);
                }
            }
        } else {
            return;
        }

        if (ftDev == null) {
            return;
        }

        if (ftDev.isOpen()) {
            currentIndex = openIndex;
        }
    }

    /**
     * 检查Usb设备连接状态
     *
     * @return true:已连接 false:未连接
     */
    public static boolean checkUsb() {
        return null != ftDev && ftDev.isOpen();
    }

    public static FT_Device getDev() {
        return ftDev;
    }
}
