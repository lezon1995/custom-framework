package com.zl.chat.protocol.encoder;

import com.zl.chat.protocol.IMP;
import com.zl.chat.protocol.message.IMPMessage;
import com.zl.chat.util.ImpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.springframework.stereotype.Component;

/**
 * IMP协议编码器
 * 将{@link IMPMessage}编码之后返回给前端解析
 *
 * @author zhuliang
 * @date 2019/6/23 11:27
 */

@Component
public class IMPEncoder extends MessageToByteEncoder<IMPMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, IMPMessage msg, ByteBuf out) throws Exception {

    }

    public String encoding(IMPMessage message) {
        if (message == null) {
            return "";
        }
        StringBuilder prex = new StringBuilder(String.format("[%s][%d]", message.getCommand(), message.getTime()));
        if (ImpUtil.isMatched(message, IMP.LOGIN) ||
                ImpUtil.isMatched(message, IMP.CHAT) ||
                ImpUtil.isMatched(message, IMP.FLOWER)
        ) {
            prex.append(String.format("[%s]", message.getSender()));
        } else if (ImpUtil.isMatched(message, IMP.SYSTEM)) {
            prex.append(String.format("[%s]", message.getOnline()));
        }

        if (message.getCommand() != null || !message.getContent().equals("")) {
            prex.append(String.format(" - %s", message.getContent()));
        }
        return prex.toString();
    }
}
