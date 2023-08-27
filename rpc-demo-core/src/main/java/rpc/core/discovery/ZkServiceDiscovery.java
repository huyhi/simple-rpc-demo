package rpc.core.discovery;

import org.apache.commons.collections4.CollectionUtils;
import rpc.core.entity.BaseRequest;
import rpc.core.enums.RpcErrMsgEnum;
import rpc.core.exception.RpcException;
import rpc.core.loadbalance.ConsistentHashLoadBalance;
import rpc.core.loadbalance.LoadBalance;
import rpc.core.utils.CuratorUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalancer;

    public ZkServiceDiscovery() {
        // TODO, use config to determine which loadBalancer selected
        loadBalancer = new ConsistentHashLoadBalance();
    }

    @Override
    public InetSocketAddress lookup(BaseRequest request) throws Exception {
        String serviceName = request.getInterfaceName();

        List<String> addressList = CuratorUtils.getChildrenNode(CuratorUtils.getZkClient(), serviceName);
        if (CollectionUtils.isEmpty(addressList)) {
            throw new RpcException(RpcErrMsgEnum.EMPTY_SERVICE_FROM_ZK, serviceName);
        }

        String address = loadBalancer.selectServiceAddress(addressList, request);
        String[] addressSplit = address.split(":");
        return new InetSocketAddress(addressSplit[0], Integer.parseInt(addressSplit[1]));
    }

}
