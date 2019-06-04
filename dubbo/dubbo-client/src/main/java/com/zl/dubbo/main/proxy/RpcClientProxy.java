package com.zl.dubbo.main.proxy;

import com.zl.dubbo.main.bean.RpcRequest;
import com.zl.dubbo.main.handler.RpcProxyHandler;
import com.zl.dubbo.main.registry.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

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
    private Bootstrap bootstrap;

    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 根据接口的class对象返回接口的动态代理对象
     *
     * @param interfaceClass 被代理接口的class对象
     * @param <T>
     * @return 动态代理对象
     */
    public <T> T create(final Class<T> interfaceClass) {
        T instance = ((T) Proxy.newProxyInstance(
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
                        //netty 客户端 处理handler
                        final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
                        EventLoopGroup group = new NioEventLoopGroup();
                        try {
                            //启动netty服务
                            bootstrap = new Bootstrap();
                            bootstrap.group(group)
                                    .channel(NioSocketChannel.class)
                                    .option(ChannelOption.TCP_NODELAY, true)
                                    .handler(new ChannelInitializer<SocketChannel>() {
                                        @Override
                                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                                            //业务
                                            ChannelPipeline pipeline = socketChannel.pipeline();
                                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                                            pipeline.addLast("encoder", new ObjectEncoder());
                                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(getClass().getClassLoader())));
                                            pipeline.addLast("handler", rpcProxyHandler);
                                        }
                                    });
                            //连接netty server
                            ChannelFuture future = bootstrap.connect(ip, port).sync();
                            //将封装好的request对象写过去
                            future.channel().writeAndFlush(request);
                            //客户端在这里阻塞 直到服务端返回结果
                            future.channel().closeFuture().sync();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            group.shutdownGracefully();
                        }
                        return rpcProxyHandler.getResponse();
                    }
                }));
        return instance;
    }

}
