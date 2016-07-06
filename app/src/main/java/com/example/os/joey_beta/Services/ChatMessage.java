package com.example.os.joey_beta.Services;

/**
 * Created by OS on 04/07/2016.
 */
public class ChatMessage {
    private int msgID;      // Type of message: received or sent
    private int iconID;     // Type of icon: empty (only for strings), iconed (without strings).
    private String msg;     // Message written: if iconed then msg = "".

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
