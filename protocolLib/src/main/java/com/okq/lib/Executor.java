package com.okq.lib;

import android.text.TextUtils;

import com.ftdi.j2xx.FT_Device;
import com.okq.lib.exception.ProtocolException;
import com.okq.lib.exception.USBException;
import com.okq.lib.protocol.Protocol;
import com.okq.lib.protocol.ProtocolFactory;
import com.okq.lib.service.USBService;
import com.okq.lib.task.TaskInfo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zst on 2016/3/8. 执行器
 */
public class Executor {
    private long TIME_OUT = 2000;
    private Protocol PROTOCOL;
    private op OP;
    private CountDownLatch end;
    private CountDownLatch begin;
    private String FC;
    private int RETRY = 0;
    private int time = 0;

    /**
     * 选择要使用的协议
     *
     * @param protocol 协议
     * @return 执行器
     * @throws ProtocolException 协议异常:创建失败
     */
    public Executor use(Class<? extends Protocol> protocol) throws ProtocolException {
        this.PROTOCOL = ProtocolFactory.create(protocol);
        time = 0;
        return this;
    }

    /**
     * 设置超时时间 默认为2000ms
     *
     * @param millions 超时的时间 单位:ms
     * @return 执行器
     */
    public Executor setTimeOut(long millions) {
        this.TIME_OUT = millions;
        return this;
    }

    /**
     * 设置重试次数 默认不重试
     *
     * @param times 重试的次数
     * @return 执行器
     */
    public Executor setReTryTimes(int times) {
        this.RETRY = times;
        return this;
    }

    /**
     * 执行查询数据
     *
     * @param address 要查询的地址
     * @return 查询结果
     * @throws ProtocolException 协议异常
     * @throws USBException      USB异常
     */
    public String query(String address) throws ProtocolException, USBException {
        this.OP = op.QUERY_DATA;
        this.PROTOCOL.setAddress(address);
        return execute(this.PROTOCOL.createQueryBytes());
    }

    /**
     * 执行修改地址
     *
     * @param oldAddress 老地址
     * @param newAddress 新地址
     * @return 修改结果
     * @throws ProtocolException 协议异常
     * @throws USBException      USB异常
     */
    public String modifyAddress(String oldAddress, String newAddress) throws ProtocolException, USBException {
        this.OP = op.MODIFY_ADDRESS;
        this.PROTOCOL.setAddress(oldAddress);
        return execute(this.PROTOCOL.createModifyAddressBytes(oldAddress, newAddress));
    }

    /**
     * 查询地址
     *
     * @return 地址
     * @throws ProtocolException 协议异常
     * @throws USBException      USB异常
     */
    public String queryAddress() throws ProtocolException, USBException {
        this.OP = op.QUERY_ADDRESS;
        return execute(this.PROTOCOL.createReadAddressBytes());
    }

    /**
     * 执行其他功能
     *
     * @param fc    功能码
     * @param bytes 要输入的内容
     * @return 执行结果
     * @throws ProtocolException 协议异常
     * @throws USBException      USB异常
     */
    public String execute(String address, String fc, String bytes) throws ProtocolException, USBException {
        this.OP = op.FC;
        this.FC = fc;
        this.PROTOCOL.setAddress(address);
        return execute(this.PROTOCOL.createOtherFunctionBytes(fc, bytes));
    }

    /**
     * 执行操作
     *
     * @param bytes 协议内容数组字符串
     * @return 执行结果
     * @throws ProtocolException 协议异常
     * @throws USBException      USB异常
     */
    private String execute(String bytes) throws ProtocolException, USBException {
        synchronized (this) {
            if (U.isServiceWork(U.mContext, USBService.class)) {
                if (USBService.checkUsb()) {
                    final String[] result = {""};
                    //任务准备
                    begin = new CountDownLatch(1);
                    USBTask usbTask = new USBTask(bytes);
                    usbTask.setTaskInfo(new TaskInfo() {
                        @Override
                        public void onFinish(String res) {
                            result[0] = res;
                        }
                    });
                    usbTask.start();
                    //任务开始
                    begin.countDown();
                    end = new CountDownLatch(1);
                    try {
                        //等待任务结束
                        end.await(TIME_OUT, TimeUnit.MILLISECONDS);
                        end.countDown();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //判断是否是超时,如果超时抛出超时异常
                    if (TextUtils.isEmpty(result[0])) {
                        if (RETRY-- > 0) {
                            time++;
                            result[0] = execute(bytes);
                        } else
                            throw new ProtocolException("超时:" + TIME_OUT + "ms,重试:" + time + "次");
                    }
                    return result[0];
                } else {
                    throw new USBException("usb设备未连接");
                }
            } else
                throw new USBException("usb服务未启动");
        }
    }

    private enum op {
        /**
         * 查询数据
         */
        QUERY_DATA,
        /**
         * 查询地址
         */
        QUERY_ADDRESS,
        /**
         * 其他指令
         */
        FC,
        /**
         * 修改地址
         */
        MODIFY_ADDRESS
    }

    /**
     * Created by zst on 2016/3/8. USB设备交互任务
     */
    private class USBTask extends Thread {

        private final String order;
        private TaskInfo info;
        private final FT_Device ftDev;

        USBTask(String bytes) {
            ftDev = USBService.getDev();
            this.order = bytes;
        }

        @Override
        public void run() {
            String result = "";
            try {
                begin.await();
                synchronized (ftDev) {
                    sendMessage(order);
                    boolean isReceive = false;
                    StringBuilder sb = new StringBuilder();
                    do {
                        Thread.sleep(100);
                        int isAvailable = ftDev.getQueueStatus();
                        byte[] readData = new byte[isAvailable];
                        ftDev.read(readData, isAvailable, 2000);
                        for (byte b : readData) {
                            sb.append(String.format("%02X", b));
                        }
                        try {
                            switch (Executor.this.OP) {
                                case QUERY_ADDRESS:
                                    result = Executor.this.PROTOCOL.decodeReadAddressResult(sb.toString());
                                    break;
                                case QUERY_DATA:
                                    result = Executor.this.PROTOCOL.decodeQueryResult(sb.toString());
                                    break;
                                case MODIFY_ADDRESS:
                                    result = Executor.this.PROTOCOL.decodeModifyAddressResult(sb.toString());
                                    break;
                                case FC:
                                    result = Executor.this.PROTOCOL.decodeOtherFunctionResult(FC, sb.toString());
                                    break;
                            }
                            isReceive = true;
                        } catch (ProtocolException ignored) {
                        }
                    } while (!isReceive && end.getCount() > 0);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (null != info) {
                    info.onFinish(result);
                }
                end.countDown();
            }

        }

        void setTaskInfo(TaskInfo info) {
            this.info = info;
        }

        /**
         * 向传感器发送消息
         *
         * @param writeData 要发送的消息
         */
        void sendMessage(String writeData) {
            if (!ftDev.isOpen()) {
                return;
            }

            ftDev.setLatencyTimer((byte) 16);
            byte[] OutData = hexString2Bytes(writeData);
            ftDev.write(OutData);
            System.out.println("writeData = [" + writeData + "]");
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
    }

}
