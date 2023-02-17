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
/*
 * The Serializable Interface is a marker interface, it doesn't have any functions to be implemented,
 * which means it instance could be Serialized or Deserialized.
 *
 * Serialized is a Java mechanism of converting the state of an object into a byte stream.
 */
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String errMsg;

}
