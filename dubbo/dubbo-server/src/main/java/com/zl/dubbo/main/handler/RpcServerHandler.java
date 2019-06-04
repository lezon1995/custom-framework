package com.zl.dubbo.main.handler;

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

    /**
     * 当接收到新连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().toString() + "已经连接");
    }

    /**
     * 当连接断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().toString() + "断开连接");
    }


    /**
     * 处理业务
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * {@link RpcRequest} 自定义RPC 请求体
         */
        RpcRequest request = (RpcRequest) msg;
        //准备返回结果
        Object result = new Object();

        //检查serviceMap中是否存在客户端传来的接口全限定名
        if (serviceMap.containsKey(request.getClassName())) {
            //获取该接口的实现类
            Object clazz = serviceMap.get(request.getClassName());
            //利用反射 获取RpcRequest中指定的方法名 以及方法参数类型
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getClazz());
            //通过反射调用该方法
            result = method.invoke(clazz, request.getParams());
        }

        //将结果返回给netty 客户端
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
