package com.zl.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * 自定义http请求
 *
 * @author zhuliang
 * @date 2019/6/22 23:55
 */
public class MyHttpRequest {
    private final ChannelHandlerContext ctx;
    private final HttpRequest request;

    public MyHttpRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getUri() {
        return request.uri();
    }

    public String getMethod() {
        return request.method().name().toString();
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoder = new QueryStringDecoder(getUri());
        return decoder.parameters();
    }

    public String getParameter(String name) {
        return getParameters().get(name).get(0);
    }

}
