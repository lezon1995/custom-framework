package com.zl.chat.server;

import com.zl.chat.handler.HttpHandler;
import com.zl.chat.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ChatServer 用netty来实现
 *
 * @author zhuliang
 * @date 2019/6/23 10:27
 */
public class ChatServer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WebSocketHandler webSocketHandler;

    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    @PostConstruct
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
                                    //HttpServerCodec: 针对http协议进行编解码
                                    .addLast(new HttpServerCodec())
                                    .addLast(new ChunkedWriteHandler())
                                    /**
                                     * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
                                     * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
                                     */
                                    .addLast(new HttpObjectAggregator(64 * 1024))
                                    //用于处理websocket, /ws为访问websocket时的uri
                                    .addLast(new WebSocketServerProtocolHandler("/im"))
                                    .addLast(webSocketHandler);

                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("ChatServer started on port " + port);
            //同步等待
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("ChatServer 捕获异常:[{}]", e.getMessage());
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}
