package com.zl.dubbo.main;

import com.zl.dubbo.main.registry.IRegistryCenter;
import com.zl.dubbo.main.registry.RegistryCenterImpl;

import java.io.IOException;

/**
 * 注册服务
 *
 * @author zhuliang
 * @date 2019/6/3 22:44
 */
public class RegisterDemo {
    public static void main(String[] args) throws IOException {
        IRegistryCenter registryCenter = new RegistryCenterImpl();
        String serviceName = "com.zl.IHello";
        String serviceAddress = "127.0.0.1:9090";
        registryCenter.register(serviceName, serviceAddress);
        System.in.read();
    }
}
