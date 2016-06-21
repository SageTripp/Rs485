package com.okq.lib;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.ftdi.j2xx.D2xxManager;
import com.okq.lib.exception.USBException;
import com.okq.lib.service.USBService;

import java.util.List;

/**
 * Created by zst on 2016/3/8.
 */
public class U {

    private static Executor exe;

    public static D2xxManager d2xxManager;

    public static Context mContext;

    private U() {
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public static void init(Context context) {
        mContext = context;
        try {
            d2xxManager = D2xxManager.getInstance(context);
            setupD2xxLibrary();
        } catch (D2xxManager.D2xxException ex) {
            ex.printStackTrace();
        }
        if (!isServiceWork(context, USBService.class))
            context.startService(new Intent(context, USBService.class));
    }

    /**
     * 获取执行器
     *
     * @return 执行器
     * @throws USBException 未初始化
     */
    public static Executor getExe() throws USBException {
        if (null == d2xxManager)
            throw new USBException("请先初始化");
        if (null == exe)
            return new Executor();
        else
            return exe;
    }

    public static void destroy(Context context) {
        if (isServiceWork(context, USBService.class))
            context.stopService(new Intent(context, USBService.class));
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
        for (ActivityManager.RunningServiceInfo info: myList) {
            if(info.service.getClassName().equals(service.getName()))
                return true;
        }
        return false;
    }
}
