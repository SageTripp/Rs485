package com.okq.lib.Execute;

import android.text.TextUtils;

import com.ftdi.j2xx.FT_Device;
import com.okq.lib.U;
import com.okq.lib.exception.ProtocolException;
import com.okq.lib.exception.USBException;
import com.okq.lib.service.OTGService;
import com.okq.lib.task.TaskInfo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zst on 2016/3/8. 执行器
 */
public class OtgExecutor extends Executor {
    private CountDownLatch end;
    private CountDownLatch begin;

    /**
     * 执行操作
     *
     * @param bytes 协议内容数组字符串
     * @return 执行结果
     * @throws ProtocolException 协议异常
     * @throws USBException      USB异常
     */
    @Override
    protected String execute(String bytes) throws ProtocolException, USBException {
        synchronized (this) {
            if (U.isServiceWork(context, OTGService.class)) {
                if (OTGService.checkUsb()) {
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

    /**
     * Created by zst on 2016/3/8. USB设备交互任务
     */
    private class USBTask extends Thread {

        private final String order;
        private TaskInfo info;
        private final FT_Device ftDev;

        USBTask(String bytes) {
            ftDev = OTGService.getDev();
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
                            switch (OP) {
                                case QUERY_ADDRESS:
                                    result = PROTOCOL.decodeReadAddressResult(sb.toString());
                                    break;
                                case QUERY_DATA:
                                    result = PROTOCOL.decodeQueryResult(sb.toString());
                                    break;
                                case MODIFY_ADDRESS:
                                    result = PROTOCOL.decodeModifyAddressResult(sb.toString());
                                    break;
                                case FC:
                                    result = PROTOCOL.decodeOtherFunctionResult(FC, sb.toString());
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
