package com.zl.dubbo.main.handler;

import io.netty.channel.*;

/**
 * @author zhuliang
 * @date 2019/6/4 0:24
 */
public class RpcProxyHandler extends ChannelHandlerAdapter {
    /**
     * 服务端返回的结果
     */
    private Object response;

    public Object getResponse() {
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        response = msg;
    }
}
