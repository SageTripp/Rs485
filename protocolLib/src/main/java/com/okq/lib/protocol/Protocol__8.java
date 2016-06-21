package com.okq.lib.protocol;

import android.text.TextUtils;

import com.okq.lib.exception.ProtocolException;

/**
 * Created by zst on 2016/3/10 0010. 获取害虫计数协议
 */
public class Protocol__8 extends Protocol {
    private String newAddr = "";

    public static final String FC_SET_ZERO = "02";
    public static final String FC_SET_TIME = "03";
    public static final String FC_GET_TIME = "04";

    @Override
    public String createQueryBytes() throws ProtocolException {
        if (TextUtils.isEmpty(address))
            throw new ProtocolException("address为空,encode前请先调用setAddress(address)方法");
        return "A55A0301" + address + "5AAA";
    }

    @Override
    public String decodeQueryResult(String data) throws ProtocolException {
        if (!data.matches("^(A55A0501)[A-F0-9]{6}(5AAA)$"))
            throw new ProtocolException("数据格式不对");
        if (!data.matches("^(A55A0501" + address + ")[A-F0-9]{4}(5AAA)$"))
            throw new ProtocolException("数据地址不对");
        return data.substring(10, 14);
    }

    @Override
    public String createModifyAddressBytes(String oldAddress, String newAddress) throws ProtocolException {
        if (TextUtils.isEmpty(oldAddress))
            throw new ProtocolException("原地址不能为空");
        if (TextUtils.isEmpty(newAddress))
            throw new ProtocolException("新地址不能为空");
        return "A55A0405" + oldAddress + newAddress + "5AAA";
    }

    @Override
    public String decodeReadAddressResult(String data) throws ProtocolException {
        throw new ProtocolException("此协议暂不支持读取地址");
    }

    @Override
    public String decodeModifyAddressResult(String data) throws ProtocolException {
        if (data.equals("A55A0405" + newAddr + "015AAA"))
            return newAddr;
        return address;
    }

    @Override
    public String createReadAddressBytes() throws ProtocolException {
        throw new ProtocolException("此协议暂不支持读取地址");
    }

    @Override
    public String createOtherFunctionBytes(String fc, String data) throws ProtocolException {
        if (TextUtils.isEmpty(address))
            throw new ProtocolException("address为空,encode前请先调用setAddress(address)方法");
        if (!fc.matches(FC_GET_TIME + "|" + FC_SET_TIME + "|" + FC_SET_ZERO))
            throw new ProtocolException("不支持此指令");
        StringBuilder sb = new StringBuilder("A55A");
        sb.append(String.format("%02X", data.length() / 2 + 3))
                .append(fc)
                .append(address)
                .append(data)
                .append("5AAA");
        return sb.toString();
    }

    @Override
    public String decodeOtherFunctionResult(String fc, String result) throws ProtocolException {
        if (!result.matches("^(A55A)[A-F0-9]{2}(" + FC_SET_ZERO + "|" + FC_SET_TIME + "|" + FC_GET_TIME + ")[A-F0-9]+(5AAA)$"))
            throw new ProtocolException("数据格式不对");
        if (!result.matches("^(A55A)[A-F0-9]{2}(" + FC_SET_ZERO + "|" + FC_SET_TIME + "|" + FC_GET_TIME + ")" + address + "[A-F0-9]*(5AAA)$"))
            throw new ProtocolException("数据地址不对");
        String length = result.substring(4, 6);
        return result.substring(10, 10 + Integer.parseInt(length, 16) * 2 - 6);
    }
}
