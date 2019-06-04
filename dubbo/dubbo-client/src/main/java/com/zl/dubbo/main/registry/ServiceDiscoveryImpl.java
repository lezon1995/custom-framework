package com.zl.dubbo.main.registry;

import com.zl.dubbo.main.loadbalance.ILoadBalance;
import com.zl.dubbo.main.loadbalance.RandomLoadBalance;
import com.zl.dubbo.main.properties.ZkProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务发现实现类
 *
 * @author zhuliang
 * @date 2019/6/3 22:57
 */
public class ServiceDiscoveryImpl implements IServiceDiscovery {

    private CuratorFramework curatorFramework;
    /**
     * zookeeper中注册的服务
     * 由于在zk中 serviceName对应的服务url是父子节点模式
     * 所以一个serviceName可能对应多个子节点
     * 所以使用list接收
     */
    List<String> repos = new ArrayList<>();

    public ServiceDiscoveryImpl() {
        //客户端连接zookeeper 以便发现服务
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkProperties.CONNECTION_URL)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        curatorFramework.start();
    }


    /**
     * 发现服务
     *
     * @param serviceName 服务名称
     * @return 服务url
     */
    @Override
    public String discovery(String serviceName) {
        String path = ZkProperties.REGISTRY_PATH + "/" + serviceName;
        try {
            repos = curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //动态感知服务器节点的变化
        registerWatch(path);

        //负载均衡选择zookeeper服务节点
        ILoadBalance loadBalance = new RandomLoadBalance();
        return loadBalance.select(repos);
    }

    /**
     * 检测服务变化
     *
     * @param path 服务名称
     */
    private void registerWatch(final String path) {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                //当服务变化的时候 会即时更新repos
                repos = curatorFramework.getChildren().forPath(path);
            }
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException("注册PathChildListener 异常:" + e);
        }
    }
}
