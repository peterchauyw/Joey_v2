package com.example.os.joey_beta.Services;

/**
 * Created by OS on 04/07/2016.
 */
public class ChatMessage {
    private int msgID;
    private int iconID;
    private String msg;

    public ChatMessage(int msgID, int iconID, String msg){
        super();
        this.msgID = msgID;
        this.iconID = iconID;
        this.msg = msg;
    }

    public int getMsgID() {
        return msgID;
    }

    public int getIconID() {
        return  iconID;
    }

    public String getMsg() {
        return msg;
    }
}
