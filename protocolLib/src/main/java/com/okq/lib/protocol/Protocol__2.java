package com.okq.lib.protocol;

import android.text.TextUtils;

import com.okq.lib.exception.ProtocolException;

/**
 * Created by zst on 2016/3/7.
 */
public class Protocol__2 extends Protocol {

    @Override
    public String createQueryBytes() throws ProtocolException {
        if (TextUtils.isEmpty(address))
            throw new ProtocolException("address为空,encode前请先调用setAddress(address)方法");
        String data = "7E" + address + "07000000";
        CRC = crc16(data);
        data += CRC;
        return data;
    }

    @Override
    public String decodeQueryResult(String data) throws ProtocolException {
        if (!data.matches("^(7E)[A-F0-9]{2}07[A-F0-9]{10}"))
            throw new ProtocolException("数据格式不对");
        if (!data.matches("^(7E" + address + ")07[A-F0-9]{10}"))
            throw new ProtocolException("数据地址不对");
        if (!data.substring(data.length() - 4).equals(crc16(data.substring(0, data.length() - 4))))
            throw new ProtocolException("数据校验失败");
        String length = data.substring(6, 8);
        int i = Integer.parseInt(length, 16);
        return data.substring(8, 8 + 2 * i);
    }

    @Override
    public String createModifyAddressBytes(String oldAddress, String newAddress) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持修改地址");
    }

    @Override
    public String decodeReadAddressResult(String data) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持读取地址");
    }

    @Override
    public String decodeModifyAddressResult(String data) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持修改地址");
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
