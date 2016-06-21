package com.okq.lib.protocol;

import android.support.annotation.NonNull;

import com.okq.lib.exception.ProtocolException;

/**
 * Created by zst on 2016/3/7. 协议
 */
public abstract class Protocol {

    /**
     * 地址
     */
    protected String address = "";

    protected String function = "";
    /**
     * CRC校验
     */
    String CRC = "";

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 生成查询协议字节字符串
     *
     * @return 生成的协议串
     * @throws ProtocolException
     */
    public abstract String createQueryBytes() throws ProtocolException;

    /**
     * 解码
     *
     * @return 解码后的协议串
     * @throws ProtocolException
     */
    public abstract String decodeQueryResult(String data) throws ProtocolException;

    /**
     * 修改设备地址
     *
     * @param oldAddress 原来的地址
     * @param newAddress 新的地址
     * @throws ProtocolException 协议不支持修改地址
     */
    public abstract String createModifyAddressBytes(String oldAddress, String newAddress) throws ProtocolException;

    /**
     * 读取设备地址
     *
     * @return 设备地址
     * @throws ProtocolException 协议不支持读取地址
     */
    public abstract String decodeReadAddressResult(String data) throws ProtocolException;

    /**
     * 读取修改地址结果
     *
     * @return 设备地址或结果
     * @throws ProtocolException 协议不支持修改地址
     */
    public abstract String decodeModifyAddressResult(String data) throws ProtocolException;

    /**
     * 生成查询地址编码
     *
     * @return 查询地址字节字符串
     * @throws ProtocolException 协议不支持
     */
    public abstract String createReadAddressBytes() throws ProtocolException;

    /**
     * 生成其他功能编码
     *
     * @param fc   功能码
     * @param data 数据
     * @return 其他功能编码
     * @throws ProtocolException 协议不支持
     */
    public abstract String createOtherFunctionBytes(String fc, String data) throws ProtocolException;

    /**
     * 解析其他功能编码
     *
     * @param fc     功能码
     * @param result 返回结果
     * @return 解析结果
     * @throws ProtocolException 协议不支持
     */
    public abstract String decodeOtherFunctionResult(String fc, String result) throws ProtocolException;

    /**
     * crc16校验
     *
     * @param aData 要校验的数据
     * @return 校验值
     */
    @NonNull
    static String crc16(String aData) {
        String ad;
        int[] w = new int[aData.length() / 2];
        for (int i = 0; i < aData.length(); i = i + 2) {
            ad = aData.substring(i, i + 2);
            w[i / 2] = Integer.parseInt(ad, 16);
        }
        int a, b, c;
        a = 0xFFFF;
        b = 0xA001;
        for (int aW : w) {
            a ^= aW;
            for (int j = 0; j < 8; j++) {
                c = a & 0x01;
                a >>= 1;
                if (c == 1) {
                    a ^= b;
                }
            }
        }
        return String.format("%04x", a);
    }
}
