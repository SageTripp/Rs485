package com.okq.lib.protocol;

import android.text.TextUtils;

import com.okq.lib.exception.ProtocolException;

/**
 * Created by zst on 2016/3/7.
 */
public class Protocol__7 extends Protocol {

    private String newAddr;

    @Override
    public String createQueryBytes() throws ProtocolException {
        if (TextUtils.isEmpty(address))
            throw new ProtocolException("address为空,encode前请先调用setAddress(address)方法");
        String data = address + "03002A0001";
        CRC = crc16(data);
        data += CRC;
        return data;
    }

    @Override
    public String decodeQueryResult(String data) throws ProtocolException {
        if (!data.matches("^([A-F0-9]{2})0302[A-F0-9]{8}"))
            throw new ProtocolException("数据格式不对");
        if (!data.matches("^(" + address + ")0302[A-F0-9]{8}"))
            throw new ProtocolException("数据地址不对");
        if (!data.substring(data.length() - 2).equals(crc16(data.substring(0, data.length() - 2))))
            throw new ProtocolException("数据校验失败");
        return data.substring(6, 10);
    }

    @Override
    public String createModifyAddressBytes(String oldAddress, String newAddress) throws ProtocolException {
        if (TextUtils.isEmpty(oldAddress))
            throw new ProtocolException("原地址不能为空");
        if (TextUtils.isEmpty(newAddress))
            throw new ProtocolException("新地址不能为空");
        this.newAddr = newAddress;
        String data = oldAddress + "06200000" + newAddress;
        data += crc16(data);
        return data;
    }

    @Override
    public String decodeReadAddressResult(String data) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持读取地址");
    }

    @Override
    public String decodeModifyAddressResult(String data) throws ProtocolException {
        if (!data.matches("^([A-F0-9]{2})06200000[A-F0-9]{6}"))
            throw new ProtocolException("数据格式不对");
        if (!data.substring(data.length() - 4).equals(crc16(data.substring(0, data.length() - 4))))
            throw new ProtocolException("数据校验失败");
        if (data.substring(0, 2).equals(newAddr))
            return newAddr;
        else
            return address;
    }

    @Override
    public String createReadAddressBytes() throws ProtocolException {
        throw new ProtocolException("此协议暂不支持读取地址");
    }

    @Override
    public String createOtherFunctionBytes(String fc, String data) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持");
    }

    @Override
    public String decodeOtherFunctionResult(String fc, String result) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持");
    }
}
