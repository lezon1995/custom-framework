package com.zl.spring.service.impl;

import com.zl.spring.annotation.MyService;
import com.zl.spring.service.DemoService;

/**
 * @author zhuliang
 * @date 2019/7/12 16:24
 */
@MyService("demoService")
public class DemoServiceImpl implements DemoService {
    @Override
    public String demo() {
        return "demo from DemoServiceImpl";
    }
}
