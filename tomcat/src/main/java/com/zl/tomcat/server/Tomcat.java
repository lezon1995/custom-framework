package com.zl.tomcat.server;

import com.zl.tomcat.handler.TomcatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * tomcat启动类 用netty来实现
 *
 * @author zhuliang
 * @date 2019/6/22 23:55
 */
public class Tomcat {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int port;

    public Tomcat(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    //设置boss线程和worker线程
                    .group(boss, worker)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    //设置主线程的等待队列长度
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置子线程为长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //设置子线程处理类
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel
                                    .pipeline()
                                    .addLast(new HttpResponseEncoder())
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new TomcatHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("Tomcat started on port " + port);
            //同步等待
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("tomcat 捕获异常:[{}]", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
