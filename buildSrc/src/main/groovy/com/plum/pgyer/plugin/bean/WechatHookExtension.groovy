package com.plum.pgyer.plugin.bean

class WechatHookExtension {
    /*是否开启企业微信消息推送*/
    public boolean open = true
    /*企业微信，发送消息所需要的唯一标识：key*/
    public String messageKey = ""
    /*消息类型*/
    public int msgType = 0
}