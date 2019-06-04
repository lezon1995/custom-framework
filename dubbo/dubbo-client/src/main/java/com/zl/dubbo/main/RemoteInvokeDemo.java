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
        //初始化服务发现实现类
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        //初始化 接口的代理者
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);
        //通过代理者返回接口的代理对象
        IHelloService iHelloService = rpcClientProxy.create(IHelloService.class);
        System.out.println(iHelloService.sayHello("wentworth"));
    }
}
