package com.zl.dubbo.main.prc;

import com.zl.dubbo.main.annotation.RpcAnnotation;
import com.zl.dubbo.main.handler.RpcServerHandler;
import com.zl.dubbo.main.registry.IRegistryCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author zhuliang
 * @date 2019/6/3 23:32
 */
public class RpcServer {
    private final IRegistryCenter registryCenter;
    private final String serviceAddress;

    private Map<String, Object> serviceMap = new HashMap<>();

    /**
     *
     * @param registryCenter 注册中心实例
     * @param serviceAddress 服务提供者url
     */
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
        //遍历服务实现类 将其添加到serviceMap中
        Arrays.stream(services).forEach(service -> {
            //获取每个实现类的注解RpcAnnotation
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            //获取注解中的value中class的名字 com.zl.dubbo.main.service.IHelloService
            String serviceName = annotation.value().getName();
            serviceMap.put(serviceName, service);
        });
//        for (Object service : services) {
//            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
//            String serviceName = annotation.value().getName();
//            serviceMap.put(serviceName, service);
//        }
    }


    private void initNettyServer() {
        try {
            //boss线程组用来监听新连接
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            //worker线程组用来处理请求
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            //启动netty服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            //指定channel类型为NioServerSocketChannel
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //业务
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //加密解密处理handler
                    pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                    /**
                     *  真正处理业务的handler {@link RpcServerHandler}
                     */
                    pipeline.addLast(new RpcServerHandler(serviceMap));
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            //serviceAddress 服务端发布的服务url 127.0.0.1:8080
            String[] addrs = serviceAddress.split(":");
            String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            //netty 客户端启动 端口为serviceAddress的端口
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("netty服务端启动成功,等待客户端连接...");
            //服务端阻塞到这里 监听客户端发起的请求
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
