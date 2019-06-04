package com.zl.dubbo.main;

import com.zl.dubbo.main.proxy.RpcClientProxy;
import com.zl.dubbo.main.registry.ServiceDiscoveryImpl;
import com.zl.dubbo.main.service.IHelloService;

/**
 * @author zhuliang
 * @date 2019/6/4 0:45
 */
public class RemoteInvokeDemo {
    public static void main(String[] args) {
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);
        IHelloService iHelloService = rpcClientProxy.create(IHelloService.class);
        System.out.println(iHelloService.sayHello("wentworth"));
    }
}
