package rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RpcErrMsgEnum {

    SERVICE_NOT_FOUNT("service not found, service name: {0}"),
    ;

    private final String errMsg;

}
