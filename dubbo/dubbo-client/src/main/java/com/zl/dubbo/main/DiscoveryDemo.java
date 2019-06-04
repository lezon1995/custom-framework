package com.zl.dubbo.main;

import com.zl.dubbo.main.registry.IServiceDiscovery;
import com.zl.dubbo.main.registry.ServiceDiscoveryImpl;

/**
 * @author zhuliang
 * @date 2019/6/3 23:18
 */
public class DiscoveryDemo {
    public static void main(String[] args) {
        IServiceDiscovery discovery = new ServiceDiscoveryImpl();
        String serviceName = "com.zl.dubbo.main.service.IHelloService";
        String service = discovery.discovery(serviceName);
        System.out.println(service);
    }
}
