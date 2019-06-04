package com.zl.dubbo.main.registry;

import com.zl.dubbo.main.properties.ZkProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author zhuliang
 * @date 2019/6/3 22:07
 */
public class RegistryCenterImpl implements IRegistryCenter {

    private CuratorFramework curatorFramework;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkProperties.CONNECTION_URL)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        curatorFramework.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String servicePath = ZkProperties.REGISTRY_PATH + "/" + serviceName;

        try {
            //判断/registry/IHelloService是否存在,不存在就创建
            if (curatorFramework.checkExists().forPath(servicePath) == null) {
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath, "0".getBytes());
            }
            //代码执行到这里肯定存在/registry/IHelloService
            //服务发布的地址:127.0.0.1:8080
            //address:/registry/IHelloService/127.0.0.1:8080
            String addressPath = servicePath + "/" + serviceAddress;
            String rsNode = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(addressPath, "0".getBytes());
            System.out.println("服务注册成功:" + rsNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
