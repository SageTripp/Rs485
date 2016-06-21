package com.okq.lib.protocol;

import com.okq.lib.exception.ProtocolException;

/**
 * Created by zst on 2016/3/7.
 * 协议工厂
 */
public class ProtocolFactory {

    public static Protocol create(Class<? extends Protocol> protocol) throws ProtocolException {
        try {
            return protocol.newInstance();
        } catch (InstantiationException e) {
            throw new ProtocolException("协议错误");
        } catch (IllegalAccessException e) {
            throw new ProtocolException("协议错误");
        }
    }
}
