package com.zl.dubbo.main.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * @author zhuliang
 * @date 2019/6/3 23:10
 */
public class RandomLoadBalance implements ILoadBalance {

    private Random random=new Random();

    @Override
    public String select(List<String> repos) {
        int size = repos.size();
        return repos.get(random.nextInt(size));
    }
}
