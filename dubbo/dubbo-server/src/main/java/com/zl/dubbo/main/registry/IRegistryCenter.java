package com.zl.dubbo.main.registry;

/**
 * @author zhuliang
 * @date 2019/6/3 21:53
 */
public interface IRegistryCenter {

    /**
     * 注册服务到注册中心
     *
     * @param serviceName    服务名称
     * @param serviceAddress 服务地址
     */
    void register(String serviceName, String serviceAddress);
}
