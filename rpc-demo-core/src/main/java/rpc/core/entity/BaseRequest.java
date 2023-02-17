package rpc.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest implements Serializable {

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] parameterTypes;

}
