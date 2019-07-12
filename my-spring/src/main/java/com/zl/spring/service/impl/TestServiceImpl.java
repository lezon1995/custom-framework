package com.zl.spring.service.impl;

import com.zl.spring.annotation.MyService;
import com.zl.spring.service.TestService;

/**
 * @author zhuliang
 * @date 2019/7/12 16:24
 */
@MyService
public class TestServiceImpl implements TestService {
    @Override
    public String test() {
        return "test from TestServiceImpl";
    }
}
