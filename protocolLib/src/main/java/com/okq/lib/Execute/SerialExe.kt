package com.okq.lib.Execute

import com.okq.lib.U
import com.okq.lib.exception.ProtocolException
import com.okq.lib.exception.USBException
import com.okq.lib.serialPort.SerialPort
import com.okq.lib.service.SerialService

/**
 * Created by zst on 2016-11-12  0012.
 * 描述:
 */
class SerialExe : Executor() {

    var serialPort: SerialPort? = null
    var receiveData: String = ""

    val receive = object : Thread() {
        override fun run() {
            super.run()
            while (!isInterrupted) {
                val receiveData = serialPort!!.receiveData()
                if (receiveData.isNotEmpty()) {
                    try {
                        when (OP) {
                            Executor.OPERATE.QUERY_ADDRESS -> this@SerialExe.receiveData = PROTOCOL.decodeReadAddressResult(receiveData)
                            Executor.OPERATE.QUERY_DATA -> this@SerialExe.receiveData = PROTOCOL.decodeQueryResult(receiveData)
                            Executor.OPERATE.MODIFY_ADDRESS -> this@SerialExe.receiveData = PROTOCOL.decodeModifyAddressResult(receiveData)
                            Executor.OPERATE.FC -> this@SerialExe.receiveData = PROTOCOL.decodeOtherFunctionResult(FC, receiveData)
                        }
                    } catch (ignored: ProtocolException) {
                    }
                    wait = false
                }
            }
        }
    }

    override fun execute(bytes: String): String {
        var ret = ""
        synchronized(this) {
            if (U.isServiceWork(context, SerialService::class.java)) {
                serialPort = SerialService.getDev()
                if (!receive.isAlive) {
                    receive.start()
                }
                if (SerialService.checkUsb()) {
                    sendMessage(bytes)
                    await()
                    if (receiveData.isEmpty()) {
                        if (RETRY-- > 0) {
                            time++
                            ret = execute(bytes)
                        } else
                            throw ProtocolException("超时:" + TIME_OUT + "ms,重试:" + time + "次")
                    }
                    ret = receiveData
                    receiveData = ""
                    return ret
                } else
                    throw USBException("串口设备未连接")
            } else
                throw USBException("串口服务未启动")
        }
    }

    private var wait: Boolean = true

    fun await() {
        var t = 0
        while (wait && t++ < TIME_OUT) {
            Thread.sleep(1)
        }
        wait = true
    }

    /**
     * 向传感器发送消息

     * @param writeData 要发送的消息
     */
    internal fun sendMessage(writeData: String) {
        if (!serialPort!!.isOpen) {
            return
        }
        serialPort?.sendData(writeData)
        println("writeData = [$writeData]")
    }

}