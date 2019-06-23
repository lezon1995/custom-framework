package com.zl.chat.protocol.decoder;

import com.zl.chat.protocol.IMP;
import com.zl.chat.protocol.message.IMPMessage;
import com.zl.chat.util.ImpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IMP协议解码器
 * 将前端编码之后的协议体解析
 *
 * @author zhuliang
 * @date 2019/6/23 11:27
 */
@Component
public class IMPDecoder extends ByteToMessageDecoder {

    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String msg = new String(in.array(), StandardCharsets.UTF_8);
        IMPMessage impMessage = decoding(msg);
    }

    public IMPMessage decoding(String msg) {
        if (msg == null || msg.trim().equals("")) {
            return null;
        }
        try {
            Matcher m = pattern.matcher(msg);
            String header = null;
            String content = null;
            if (m.matches()) {
                header = m.group(1);
                content = m.group(3);
            }
            String[] split = header.split("\\]\\[");
            long sendTime = Long.parseLong(split[1]);
            String nickName = split[2];
            if (ImpUtil.isStartsWith(msg, IMP.LOGIN)) {
                return new IMPMessage(split[0], sendTime, nickName);
            } else if (ImpUtil.isStartsWith(msg, IMP.CHAT)) {
                return new IMPMessage(split[0], sendTime, nickName, content);
            } else if (ImpUtil.isStartsWith(msg, IMP.FLOWER)) {
                return new IMPMessage(split[0], sendTime, nickName);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }


}
