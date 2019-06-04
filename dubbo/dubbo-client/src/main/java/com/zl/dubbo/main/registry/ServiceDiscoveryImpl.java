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
 * @author zhuliang
 * @date 2019/6/3 22:57
 */
public class ServiceDiscoveryImpl implements IServiceDiscovery {

    private CuratorFramework curatorFramework;
    List<String> repos = new ArrayList<>();

    public ServiceDiscoveryImpl() {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkProperties.CONNECTION_URL)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        curatorFramework.start();
    }


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


        ILoadBalance loadBalance = new RandomLoadBalance();
        return loadBalance.select(repos);
    }

    private void registerWatch(final String path) {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
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
