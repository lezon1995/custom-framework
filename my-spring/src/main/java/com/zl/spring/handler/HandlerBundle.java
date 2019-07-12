package com.zl.spring.handler;

import java.lang.reflect.Method;

/**
 * @author zhuliang
 * @date 2019/7/12 17:12
 */
public class HandlerBundle {
    private Method method;
    private Object controller;
    private String mapping;

    public HandlerBundle(Method method, Object controller, String mapping) {
        this.method = method;
        this.controller = controller;
        this.mapping = mapping;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
}
