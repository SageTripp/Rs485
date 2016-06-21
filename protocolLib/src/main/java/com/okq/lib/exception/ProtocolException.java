package com.okq.lib.exception;

/**
 * Created by zst on 2016/3/7.
 */
public class ProtocolException extends Exception {
    private ProtocolException(){

    }
    public ProtocolException(String detailMessage) {
        super(detailMessage);
    }
}
