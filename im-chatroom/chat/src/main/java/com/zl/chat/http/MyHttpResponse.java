package com.zl.chat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * 自定义http响应
 *
 * @author zhuliang
 * @date 2019/6/22 23:55
 */
public class MyHttpResponse {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final ChannelHandlerContext ctx;
    private final HttpRequest request;

    public MyHttpResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String out) {
        if (out == null) {
            return;
        }
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(out.getBytes(StandardCharsets.UTF_8)));

        response
                .headers()
                .set(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json")
                .set(HttpHeaderNames.CONTENT_LENGTH.toString(), String.valueOf(response.content().readableBytes()))
                .set(HttpHeaderNames.EXPIRES.toString(), "0")
                .set(HttpHeaderNames.CONNECTION.toString());

        logger.info("向浏览器响应:[{}]", out);
        ctx.writeAndFlush(response);
    }
}
