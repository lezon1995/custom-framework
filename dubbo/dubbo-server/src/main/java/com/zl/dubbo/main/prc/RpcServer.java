package com.zl.dubbo.main.prc;

import com.zl.dubbo.main.annotation.RpcAnnotation;
import com.zl.dubbo.main.registry.IRegistryCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuliang
 * @date 2019/6/3 23:32
 */
public class RpcServer {
    private final IRegistryCenter registryCenter;
    private final String serviceAddress;

    private Map<String, Object> serviceMap = new HashMap<>();

    public RpcServer(IRegistryCenter registryCenter, String serviceAddress) {
        this.registryCenter = registryCenter;
        this.serviceAddress = serviceAddress;
    }


    /**
     * 发布服务 监听端口
     */
    public void publish() {
        //注册服务名称和地址
        serviceMap.keySet().forEach(serviceName -> registryCenter.register(serviceName, serviceAddress));
        //启动netty监听
        initNettyServer();
    }


    /**
     * 绑定每个服务实例与服务名称
     *
     * @param services 服务实例集合
     */
    public void bind(Object... services) {
        for (Object service : services) {
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();
            serviceMap.put(serviceName, service);
        }
    }


    private void initNettyServer() {
        try {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            //启动netty服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //业务
                    ChannelPipeline pipeline = socketChannel.pipeline();

//                    pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//                    pipeline.addLast(new LengthFieldPrepender(4));
//                    pipeline.addLast("encoder", new ObjectEncoder());
//                    pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled()));
                    pipeline.addLast(new RpcServerHandler(serviceMap));
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] addrs = serviceAddress.split(":");
            String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            System.out.println("netty服务端启动成功,等待客户端连接...");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
