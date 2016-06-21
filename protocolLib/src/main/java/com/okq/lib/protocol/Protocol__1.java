package com.okq.lib.protocol;

import android.text.TextUtils;

import com.okq.lib.exception.ProtocolException;

/**
 * Created by zst on 2016/3/7.
 */
public class Protocol__1 extends Protocol {


    @Override
    public String createQueryBytes() throws ProtocolException {
        if (TextUtils.isEmpty(address))
            throw new ProtocolException("address为空,encode前请先调用setAddress(address)方法");
        return "AA0064" + address + "A5";
    }

    @Override
    public String decodeQueryResult(String data) throws ProtocolException {
        if (!data.matches("^(AA64)[A-F0-9]{6}(A5)$"))
            throw new ProtocolException("数据格式不对");
        if (!data.matches("^(AA64" + address + ")[A-F0-9]{4}(A5)$"))
            throw new ProtocolException("数据地址不对");
        return data.substring(6, 10);
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
