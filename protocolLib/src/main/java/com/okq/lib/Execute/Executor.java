package com.okq.lib.Execute;

import android.content.Context;

import com.okq.lib.exception.ProtocolException;
import com.okq.lib.exception.USBException;
import com.okq.lib.protocol.Protocol;
import com.okq.lib.protocol.ProtocolFactory;

/**
 * Created by zst on 2016-08-17  0017.
 * 描述:
 */

public abstract class Executor {

    long TIME_OUT = 2000;
    Protocol PROTOCOL;
    OPERATE OP;
    String FC;
    int RETRY = 0;
    int time = 0;
    public Context context;

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
        this.OP = OPERATE.QUERY_DATA;
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
        this.OP = OPERATE.MODIFY_ADDRESS;
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
        this.OP = OPERATE.QUERY_ADDRESS;
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
        this.OP = OPERATE.FC;
        this.FC = fc;
        this.PROTOCOL.setAddress(address);
        return execute(this.PROTOCOL.createOtherFunctionBytes(fc, bytes));
    }

    protected abstract String execute(String bytes) throws ProtocolException, USBException;

    protected enum OPERATE {
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
}
