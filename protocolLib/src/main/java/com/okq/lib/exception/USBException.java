package com.okq.lib.exception;

/**
 * Created by zst on 2016/3/8. USB异常
 */
public class USBException extends Exception {
    private USBException() {
    }

    public USBException(String detailMessage) {
        super(detailMessage);
    }
}
