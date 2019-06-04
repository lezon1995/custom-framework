package com.zl.dubbo.main.bean;

import java.io.Serializable;

/**
 * PRC 自定义请求参数
 *
 * @author zhuliang
 * @date 2019/6/4 0:07
 */
public class RpcRequest implements Serializable {
    private String className;
    private String methodName;
    private Class<?>[] clazz;
    private Object[] params;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getClazz() {
        return clazz;
    }

    public void setClazz(Class<?>[] clazz) {
        this.clazz = clazz;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
