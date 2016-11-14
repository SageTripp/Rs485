package com.okq.lib.Execute;

import android.text.TextUtils;

import com.okq.lib.U;
import com.okq.lib.exception.ProtocolException;
import com.okq.lib.exception.USBException;
import com.okq.lib.serialPort.SerialPort;
import com.okq.lib.service.SerialService;
import com.okq.lib.task.TaskInfo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by zst on 2016/3/8. 执行器
 */
@Deprecated
public class SerialExecutor extends Executor {

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
            if (U.isServiceWork(context, SerialService.class)) {
                if (SerialService.checkUsb()) {
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
                        usbTask.interrupt();
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
        private final SerialPort serialPort;

        USBTask(String bytes) {
            serialPort = SerialService.getDev();
            this.order = bytes;
        }

        @Override
        public void run() {
            String result = "";
            System.out.println("开始");
            try {
                begin.await();
                synchronized (serialPort) {
                    sendMessage(order);
                    boolean isReceive = false;
                    do {
                        Thread.sleep(100);
                        String hex = serialPort.receiveData();
                        if (!TextUtils.isEmpty(hex)) {
                            try {
                                switch (OP) {
                                    case QUERY_ADDRESS:
                                        result = PROTOCOL.decodeReadAddressResult(hex);
                                        break;
                                    case QUERY_DATA:
                                        result = PROTOCOL.decodeQueryResult(hex);
                                        break;
                                    case MODIFY_ADDRESS:
                                        result = PROTOCOL.decodeModifyAddressResult(hex);
                                        break;
                                    case FC:
                                        result = PROTOCOL.decodeOtherFunctionResult(FC, hex);
                                        break;
                                }
                            } catch (ProtocolException ignored) {
                            }
                            isReceive = true;
                        }
                    } while (!isInterrupted() && !isReceive && end.getCount() > 0);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (null != info) {
                    info.onFinish(result);
                }
                end.countDown();
                System.out.println("结束");
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
            if (!serialPort.isOpen) {
                return;
            }

            serialPort.sendData(writeData);
            System.out.println("writeData = [" + writeData + "]");
        }
    }

}
