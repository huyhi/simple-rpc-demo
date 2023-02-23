package rpc.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CfgNameEnum {

    SERVICE_PORT("service.port"),

    ZK_ADDRESS("zk.address"),
    ZK_ROOT_PATH("zk.root.path")
    ;

    private final String name;
}
