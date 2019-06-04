package com.zl.dubbo.main.registry;

/**
 * 服务发现接口
 *
 * @author zhuliang
 * @date 2019/6/3 22:54
 */
public interface IServiceDiscovery {
    /**
     * 根据服务名称发现服务
     *
     * @param serviceName 服务名称
     * @return 服务
     */
    String discovery(String serviceName);
}
