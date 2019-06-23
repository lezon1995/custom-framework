package com.zl.chat.protocol;


/**
 * 自定义协议体
 *
 * @author zhuliang
 * @date 2019/6/23 11:12
 */
public enum IMP {

    /**
     * 系统命令
     */
    SYSTEM("SYSTEM"),
    /**
     * 登录命令
     */
    LOGIN("LOGIN"),
    /**
     * 注销命令
     */
    LOGOUT("LOGOUT"),
    /**
     * 聊天命令
     */
    CHAT("CHAT"),
    /**
     * 发送鲜花命令
     */
    FLOWER("FLOWER");

    private String command;

    /**
     * 判断指令是否合法
     *
     * @param content 指令
     * @return true表示合法
     */
    public static boolean isIMP(String content) {
        return content.matches("\\[(SYSTEM|LOGIN|LOGOUT|CHAT|FLOWER)\\]");
    }

    IMP(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "IMP{" +
                "command='" + command + '\'' +
                '}';
    }
}
