package com.zl.dubbo.main.service;

import com.zl.dubbo.main.annotation.RpcAnnotation;

/**
 * @author zhuliang
 * @date 2019/6/3 22:55
 */
@RpcAnnotation(IHelloService.class)
public class HelloServiceImpl3 implements IHelloService {
    @Override
    public String sayHello(String msg) {
        return "大家好 我是 " + msg +" from "+getClass().getName();
    }
}
