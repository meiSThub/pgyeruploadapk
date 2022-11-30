package com.plum.pgyer.plugin.bean

import org.gradle.util.internal.ConfigureUtil

class PgyerExtension {
    /*是否需要自动上传Apk到蒲公英，true：上传，false：不上传*/
    public boolean enabled = true

    /*(必填) API Key 点击获取_api_key*/
    public String apiKey = ""
    /*(必填) 需要上传的应用类型，如果是iOS类型请传ios或ipa，如果是Android类型请传android或apk*/
    public String buildType = "android"
    /*(选填) 是否使用海外加速上传，值为：1 使用海外加速上传，2 国内加速上传；留空根据 IP 自动判断海外加速或国内加速*/
    public int oversea = 2
    /*(选填)应用安装方式，值为(1,2,3，默认为1 公开安装)。1：公开安装，2：密码安装，3：邀请安装*/
    public int buildInstallType = 1
    /*(选填) 设置App安装密码，密码为空时默认公开安装*/
    public String buildPassword = ""
    /*(选填) 应用介绍，如没有介绍请传空字符串，或不传。*/
    public String buildDescription = ""
    /*(选填) 版本更新描述，请传空字符串，或不传。*/
    public String buildUpdateDescription = ""
    /*(选填)是否设置安装有效期，值为：1 设置有效时间， 2 长期有效，如果不填写不修改上一次的设置*/
    public int buildInstallDate = 2
    /*(选填)安装有效期开始时间，字符串型，如：2018-01-01*/
    public String buildInstallStartDate = ""
    /*(选填)安装有效期结束时间，字符串型，如：2018-12-31*/
    public String buildInstallEndDate = ""
    /*(选填)所需更新指定的渠道短链接，渠道短链接须为已创建成功的，并且只可指定一个渠道，字符串型，如：abcd*/
    public String buildChannelShortcut = ""

    /*发送消息到*/
    public WechatHookExtension wechatHook = new WechatHookExtension()

    void wechatHook(Closure<WechatHookExtension> closure) {
        ConfigureUtil.configure(closure, wechatHook)
    }
}