package com.zl.chat.util;

import com.zl.chat.protocol.IMP;
import com.zl.chat.protocol.message.IMPMessage;

/**
 * @author zhuliang
 * @date 2019/6/23 13:42
 */
public class ImpUtil {


    public static boolean isMatched(IMPMessage message, IMP imp) {
        return imp.getCommand().equals(message.getCommand());
    }

    public static boolean isStartsWith(String msg, IMP imp) {
        return msg.startsWith(String.format("[%s]", imp.getCommand()));
    }
}
