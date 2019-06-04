package com.zl.dubbo.main.prc;

import com.zl.dubbo.main.bean.RpcRequest;
import io.netty.channel.*;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author zhuliang
 * @date 2019/6/3 23:57
 */
public class RpcServerHandler extends ChannelHandlerAdapter {
    private Map<String, Object> serviceMap;

    public RpcServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        Object result = new Object();

        if (serviceMap.containsKey(request.getClassName())) {
            Object clazz = serviceMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getClazz());
            result = method.invoke(clazz, request.getParams());
        }

        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
