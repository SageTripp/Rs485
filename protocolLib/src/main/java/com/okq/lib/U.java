package com.okq.lib;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.ftdi.j2xx.D2xxManager;
import com.okq.lib.Execute.Executor;
import com.okq.lib.Execute.OtgExecutor;
import com.okq.lib.Execute.SerialExecutor;
import com.okq.lib.exception.USBException;
import com.okq.lib.serial.SerialPort;
import com.okq.lib.service.OTGService;
import com.okq.lib.service.SerialService;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by zst on 2016/3/8.
 */
public class U {

    private static Executor exe;

    public static D2xxManager d2xxManager;

    private static int type = 0;

    private static SerialPort serialPort;

    private U() {
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @param mode    0:OTG 1:RS485
     */
    public static void init(Context context, int mode) {
        type = mode;
        switch (mode) {
            case 0:
                try {
                    d2xxManager = D2xxManager.getInstance(context);
                    setupD2xxLibrary();
                } catch (D2xxManager.D2xxException ex) {
                    ex.printStackTrace();
                }
                if (!isServiceWork(context, OTGService.class)) {
                    Intent intent = new Intent(context, OTGService.class);
                    intent.putExtra("d2xx", (Parcelable) d2xxManager);
                    context.startService(intent);
                }
                exe = new OtgExecutor();
                exe.context = context;
                break;
            case 1:
                if (!isServiceWork(context, SerialService.class)) {
                    Intent intent = new Intent(context, SerialService.class);
                    context.startService(intent);
                }
                exe = new SerialExecutor();
                break;
            default:
                throw new InvalidParameterException("参数[mode]的值只能为 0(OTG) 或 1(RS485) ");
        }
    }

    /**
     * 获取执行器
     *
     * @return 执行器
     * @throws USBException 未初始化
     */
    public static Executor getExe() throws USBException {
        if (null == exe)
            switch (type) {
                case 0:
                    return new OtgExecutor();
                case 1:
                    return new SerialExecutor();
                default:
                    throw new InvalidParameterException("在 init(context,mode) 方法中输入的 mode值有误");
            }
        else
            return exe;
    }

    public static void destroy(Context context) {
        if (isServiceWork(context, OTGService.class))
            context.stopService(new Intent(context, OTGService.class));
        if (isServiceWork(context, SerialService.class))
            context.stopService(new Intent(context, SerialService.class));
        exe = null;
    }


    private static void setupD2xxLibrary() {
        // Specify a non-default VID and PID combination to match if required
        d2xxManager.setVIDPID(0x0403, 0xada1);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext 上下文对象
     * @param service  service类
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, Class<? extends Service> service) {
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        for (ActivityManager.RunningServiceInfo info : myList) {
            if (info.service.getClassName().equals(service.getName()))
                return true;
        }
        return false;
    }
}
