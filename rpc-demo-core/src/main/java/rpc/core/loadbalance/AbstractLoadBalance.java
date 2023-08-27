package rpc.core.loadbalance;

import org.apache.commons.collections4.CollectionUtils;
import rpc.core.entity.BaseRequest;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceUrlList, BaseRequest rpcRequest) {
        if (CollectionUtils.isEmpty(serviceUrlList)) {
            return null;
        }
        if (serviceUrlList.size() == 1) {
            return serviceUrlList.get(0);
        }

        return doSelect(serviceUrlList, rpcRequest);
    }


    protected abstract String doSelect(List<String> serviceUrlList, BaseRequest rpcRequest);
}
