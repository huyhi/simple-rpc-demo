package rpc.core.exception;

import rpc.core.enums.RpcErrMsgEnum;

import java.text.MessageFormat;

public class RpcException extends RuntimeException {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrMsgEnum errMsgEnum, Object... args) {
        super(MessageFormat.format(errMsgEnum.getErrMsg(), args));
    }
}
