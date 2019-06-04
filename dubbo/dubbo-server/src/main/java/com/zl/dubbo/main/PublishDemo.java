package com.zl.dubbo.main;

import com.zl.dubbo.main.prc.RpcServer;
import com.zl.dubbo.main.registry.IRegistryCenter;
import com.zl.dubbo.main.registry.RegistryCenterImpl;
import com.zl.dubbo.main.service.HelloServiceImpl1;
import com.zl.dubbo.main.service.HelloServiceImpl2;
import com.zl.dubbo.main.service.HelloServiceImpl3;
import com.zl.dubbo.main.service.IHelloService;

/**
 * 发布服务
 *
 * @author zhuliang
 * @date 2019/6/3 23:30
 */
public class PublishDemo {
    public static void main(String[] args) {
        //根据服务名称 初始化对应实例 有可能有多个不同的实现类
        IHelloService service3 = new HelloServiceImpl3();
        IHelloService service2 = new HelloServiceImpl2();
        IHelloService service1 = new HelloServiceImpl1();
        //初始化注册中心
        IRegistryCenter center = new RegistryCenterImpl();
        //服务发布 监听端口
        RpcServer rpcServer = new RpcServer(center, "127.0.0.1:9090");
        //服务名称和实例对象绑定
        rpcServer.bind(service1, service2, service3);
        rpcServer.publish();
    }
}
