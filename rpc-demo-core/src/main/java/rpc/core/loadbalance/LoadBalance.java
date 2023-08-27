package rpc.core.loadbalance;

import rpc.core.entity.BaseRequest;

import java.util.List;

public interface LoadBalance {

    String selectServiceAddress(List<String> serviceUrlList, BaseRequest rpcRequest);
}
