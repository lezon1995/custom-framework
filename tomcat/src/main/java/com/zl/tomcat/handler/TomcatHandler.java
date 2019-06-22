package com.zl.tomcat.handler;

import com.zl.tomcat.http.MyHttpRequest;
import com.zl.tomcat.http.MyHttpResponse;
import com.zl.tomcat.servlet.MyServlet;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tomcat请求处理器
 *
 * @author zhuliang
 * @date 2019/6/22 23:55
 */
public class TomcatHandler extends ChannelHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest request = msg instanceof HttpRequest ? ((HttpRequest) msg) : null;

        MyHttpRequest req = new MyHttpRequest(ctx, request);
        MyHttpResponse res = new MyHttpResponse(ctx, request);
        new MyServlet().doGet(req, res);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
    }
}
