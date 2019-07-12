package com.zl.spring.controller;

import com.zl.spring.annotation.MyAutowired;
import com.zl.spring.annotation.MyController;
import com.zl.spring.annotation.MyRequestMapping;
import com.zl.spring.service.DemoService;
import com.zl.spring.service.TestService;

/**
 * @author zhuliang
 * @date 2019/7/12 16:23
 */
@MyController
@MyRequestMapping("/test")
public class TestController {

    @MyAutowired
    private TestService testService;

    @MyAutowired
    private DemoService demoService;

    @MyRequestMapping("/test")
    public String test() {
        return testService.test();
    }

    @MyRequestMapping("/demo")
    public String demo() {
        return demoService.demo();
    }

}
