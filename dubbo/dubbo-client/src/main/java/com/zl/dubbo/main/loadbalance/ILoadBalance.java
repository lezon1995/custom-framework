package com.zl.dubbo.main.loadbalance;

import java.util.List;

/**
 * 负载均衡算法
 *
 * @author zhuliang
 * @date 2019/6/3 23:09
 */
public interface ILoadBalance {
    /**
     * 从repos中选择一个
     *
     * @param repos url集合
     * @return 其中一个
     */
    String select(List<String> repos);
}
