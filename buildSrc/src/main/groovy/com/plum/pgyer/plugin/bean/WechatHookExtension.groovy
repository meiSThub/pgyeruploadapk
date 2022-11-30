package com.plum.pgyer.plugin.bean

class WechatHookExtension {
    /*是否开启企业微信消息推送*/
    public boolean open = true
    /*企业微信，发送消息所需要的唯一标识：key*/
    public String messageKey = ""
    /**消息类型,
     * 0:TemplateCard 类型
     * 1：news ，普通图文消息类型
     * 2：image，图片消息类型
     * 3：markdown，文本消息
     * 4：markdown+image，混合消息类型
     */
    public int msgType = 4
    /*发送消息的时候，通知指定的人的号码*/
    public List<String> phones = null
}