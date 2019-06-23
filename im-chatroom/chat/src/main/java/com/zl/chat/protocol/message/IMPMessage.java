package com.zl.chat.protocol.message;

/**
 * 封装自定义协议
 *
 * @author zhuliang
 * @date 2019/6/23 11:20
 */

public class IMPMessage {
    /**
     * ip:port
     */
    private String address;
    /**
     * 命令类型
     * {@link com.zl.chat.protocol.IMP}
     */
    private String command;
    /**
     * 发送时间 毫秒值
     */
    private Long time;
    /**
     * 在线人数
     */
    private Integer online;
    /**
     * 发送者
     */
    private String sender;
    /**
     * 接收者
     */
    private String receiver;
    /**
     * 发送内容
     */
    private String content;

    public IMPMessage() {
    }

    public IMPMessage(String command, Long time, Integer online, String content) {
        this.command = command;
        this.time = time;
        this.online = online;
        this.content = content;
    }

    public IMPMessage(String command, Long time, String sender) {
        this.command = command;
        this.time = time;
        this.sender = sender;
    }

    public IMPMessage(String command, Long time, String sender, String content) {
        this.command = command;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "IMPMessage{" +
                "address='" + address + '\'' +
                ", command='" + command + '\'' +
                ", time=" + time +
                ", online=" + online +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
