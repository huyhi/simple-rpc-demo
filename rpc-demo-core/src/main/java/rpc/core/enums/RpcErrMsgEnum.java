package rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcErrMsgEnum {

    SERVICE_NOT_FOUNT("service not found, service name: {0}"),
    EMPTY_SERVICE_FROM_ZK("get empty service list from zk, service name:{0}"),
    LOAD_CFG_ERROR("load config properties error."),
    ;

    private final String errMsg;

}
