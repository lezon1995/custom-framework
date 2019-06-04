package com.zl.dubbo.main.service;

/**
 * @author zhuliang
 * @date 2019/6/3 21:52
 */
public interface IHelloService {

    /**
     * dubbo服务端方法
     *
     * @param msg 消息
     * @return 加工之后的消息
     *
     */
    String sayHello(String msg);
}
