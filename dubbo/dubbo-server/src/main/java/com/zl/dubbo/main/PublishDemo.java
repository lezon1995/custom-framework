package com.zl.dubbo.main;

import com.zl.dubbo.main.prc.RpcServer;
import com.zl.dubbo.main.registry.IRegistryCenter;
import com.zl.dubbo.main.registry.RegistryCenterImpl;
import com.zl.dubbo.main.service.HelloServiceImpl;

/**
 * 发布服务
 *
 * @author zhuliang
 * @date 2019/6/3 23:30
 */
public class PublishDemo {
    public static void main(String[] args) {
        //根据服务名称 初始化对应实例
        HelloServiceImpl service = new HelloServiceImpl();
        IRegistryCenter center = new RegistryCenterImpl();
        //服务发布 监听端口
        RpcServer rpcServer = new RpcServer(center, "127.0.0.1:9090");
        //服务名称和实例对象绑定
        rpcServer.bind(service);
        rpcServer.publish();
    }
}
