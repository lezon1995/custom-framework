package com.zl.dubbo.main.proxy;

import com.zl.dubbo.main.bean.RpcRequest;
import com.zl.dubbo.main.handler.RpcProxyHandler;
import com.zl.dubbo.main.registry.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zhuliang
 * @date 2019/6/4 0:10
 */
@SuppressWarnings("unchecked")
public class RpcClientProxy {

    private IServiceDiscovery serviceDiscovery;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<T> interfaceClass) {
        T instance= ((T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //封装RPC 请求参数
                        RpcRequest request = new RpcRequest();
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setClazz(method.getParameterTypes());
                        request.setParams(args);

                        //服务发现
                        String serviceName = interfaceClass.getName();

                        //url地址
                        String serviceAddress = serviceDiscovery.discovery(serviceName);

                        //解析IP和端口
                        String[] split = serviceAddress.split(":");
                        String ip = split[0];
                        int port = Integer.parseInt(split[1]);
                        Object o = initNettyClient(ip, port, request);
                        return o;
                    }
                }));
        return instance;
    }


    private Object initNettyClient(String ip, int port, RpcRequest request) {
        final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //启动netty服务
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //业务
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(rpcProxyHandler);
                        }
                    });
            //连接netty server
            ChannelFuture future = bootstrap.connect(ip, port).sync();
            //将封装好的request对象写过去
            future.channel().writeAndFlush(request);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return rpcProxyHandler.getResponse();
    }
}
