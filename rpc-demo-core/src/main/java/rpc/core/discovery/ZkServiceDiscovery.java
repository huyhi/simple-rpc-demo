package rpc.core.discovery;

import org.apache.commons.collections4.CollectionUtils;
import rpc.core.entity.BaseRequest;
import rpc.core.enums.RpcErrMsgEnum;
import rpc.core.exception.RpcException;
import rpc.core.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class ZkServiceDiscovery implements ServiceDiscovery {

    @Override
    public InetSocketAddress lookup(BaseRequest request) throws Exception {
        return lookup(request.getInterfaceName());
    }

    @Override
    public InetSocketAddress lookup(String serviceName) throws Exception {
        List<String> addressList = CuratorUtils.getChildrenNode(CuratorUtils.getZkClient(), serviceName);
        if (CollectionUtils.isEmpty(addressList)) {
            throw new RpcException(RpcErrMsgEnum.EMPTY_SERVICE_FROM_ZK, serviceName);
        }
        // TODO, use a better load balance strategy to find a address
        String address = addressList.get(new Random().nextInt(addressList.size()));
        String[] addressSplit = address.split(":");
        return new InetSocketAddress(addressSplit[0], Integer.parseInt(addressSplit[1]));
    }
}
