package com.zl.chat.processor;

import com.zl.chat.protocol.IMP;
import com.zl.chat.protocol.decoder.IMPDecoder;
import com.zl.chat.protocol.encoder.IMPEncoder;
import com.zl.chat.protocol.message.IMPMessage;
import com.zl.chat.util.ImpUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * IMP 协议处理器
 *
 * @author zhuliang
 * @date 2019/6/23 13:20
 */
@Component
public class IMPProcessor {
    /**
     * 全局在线用户
     */
    public static final ChannelGroup onlineUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private IMPDecoder decoder;
    private IMPEncoder encoder;

    private final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    private final AttributeKey<String> IP_ADDRESS = AttributeKey.valueOf("ipAddress");
    private final AttributeKey<String> ATTRS = AttributeKey.valueOf("attrs");

    @Autowired
    public IMPProcessor(IMPDecoder decoder, IMPEncoder encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public void process(Channel currentChannel, String msg) {
        //解密IMP协议为IMPMessage对象
        IMPMessage message = decoder.decoding(msg);
        if (message == null) {
            return;
        }
        String sender = message.getSender();

        //判断如果是登录动作,就往onlineUser中加入当前连接的channel
        if (ImpUtil.isMatched(message, IMP.LOGIN)) {
            currentChannel.attr(NICK_NAME).getAndSet(message.getSender());

            onlineUsers.add(currentChannel);
            for (Channel onlineUser : onlineUsers) {
                if (currentChannel != onlineUser) {
                    message = new IMPMessage(IMP.SYSTEM.getCommand(), System.currentTimeMillis(), onlineUsers.size(), sender + "加入聊天室");
                } else {
                    message = new IMPMessage(IMP.SYSTEM.getCommand(), System.currentTimeMillis(), onlineUsers.size(), "已于服务器建立连接");
                }
                String response = encoder.encoding(message);
                onlineUser.writeAndFlush(new TextWebSocketFrame(response));

            }
        } else if (ImpUtil.isMatched(message, IMP.LOGOUT)) {
            onlineUsers.remove(currentChannel);
        } else if (ImpUtil.isMatched(message, IMP.CHAT)) {
            for (Channel onlineUser : onlineUsers) {
                if (currentChannel != onlineUser) {
                    message.setSender(currentChannel.attr(NICK_NAME).get());
                    String text = encoder.encoding(message);
                    onlineUser.writeAndFlush(new TextWebSocketFrame(text));
                } else {
                    message.setSender("You");
                }
            }
        }
    }


    public void logout(Channel currentChannel) {
        onlineUsers.remove(currentChannel);
    }
}
