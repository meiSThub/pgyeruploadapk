package com.plum.pgyer.plugin.wechat

import com.plum.pgyer.plugin.command.GitCommand
import com.plum.pgyer.plugin.http.HttpUtils
import com.plum.pgyer.plugin.utils.Base64Utils
import com.plum.pgyer.plugin.utils.DateUtils
import com.plum.pgyer.plugin.utils.MD5Utils
import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils

/**
 * 企业微信，推送消息
 */
class WechatMessage {

    /**
     * 图文展示模板卡片
     * 推送消息到企业微信
     * @param shareApkInfo
     */
    static void pushTemplateCardMsg(Object shareApkInfo, String messageKey) {
        String buildQRCodeURL = shareApkInfo.buildQRCodeURL
        String buildShortcutUrl = shareApkInfo.buildShortcutUrl
        // 消息体
        def messageContent = [
            msgtype: "template_card",
            template_card: [
                card_type: "news_notice",
                main_title: [
                    title: "Apk上传成功",
                    desc: "扫描如下二维码下载Apk并安装："
                ],
                card_image: [
                    url: buildQRCodeURL,
                    aspect_ratio: 1.0
                ],
                vertical_content_list: [
                    [
                        title: "打包分支：",
                        desc: "${GitCommand.getCurrentBranch()}"
                    ],
                    [
                        title: "最近一条提交记录：",
                        desc: GitCommand.getOneRecentLog()
                    ],
                    [
                        title: "打包时间：",
                        desc: "${DateUtils.format(System.currentTimeMillis())}"
                    ]
                ],
                card_action: [
                    type: 1,
                    url: "https://www.pgyer.com/$buildShortcutUrl",
                ]
            ]
        ]
        String messageJson = StringEscapeUtils.unescapeJava(JsonOutput.toJson(messageContent))
        println "pushMessage：messageJson=$messageJson"
        HttpUtils.pushMessage(messageKey, messageJson)
    }

    /**
     * 图文消息
     * @param shareApkInfo
     * @param messageKey
     */
    static void pushNewsMsg(Object shareApkInfo, String messageKey) {
        String buildQRCodeURL = shareApkInfo.buildQRCodeURL
        String buildShortcutUrl = shareApkInfo.buildShortcutUrl
        // 消息体
        def messageContent = [
            msgtype: "news",
            news: [
                articles: [
                    title: "Apk上传成功",
                    description: """
打包分支：
${GitCommand.getCurrentBranch()}

最近一条提交记录：
${GitCommand.getOneRecentLog()}

打包时间：
${DateUtils.format(System.currentTimeMillis())}
""",
                    url: "https://www.pgyer.com/$buildShortcutUrl",
                    picurl: buildQRCodeURL
                ]
            ]
        ]
        String messageJson = StringEscapeUtils.unescapeJava(JsonOutput.toJson(messageContent))
        println "pushMessage：messageJson=$messageJson"
        HttpUtils.pushMessage(messageKey, messageJson)
    }

    /**
     * 下载图片，并把获取图片的 md5值，base64值
     * @param messageKey
     * @param imageUrl 图片url
     */
    static void pushImageMsg(String messageKey, String imageUrl) {
        // 下载图片
        def bytes = HttpUtils.downloadImg(imageUrl)
        // 把图片转成md5
        String fileMd5 = MD5Utils.convertMD5(bytes)
        // 把图片转成base64
        String base64 = Base64Utils.encoder(bytes)
        // 发送图片消息
        pushImageMsg(messageKey, fileMd5, base64)
    }

    /**
     * 图片消息
     * @param shareApkInfo
     * @param messageKey
     */
    static void pushImageMsg(String messageKey, String fileMd5, String fileBase64) {
        // 消息体
        def messageContent = [
            msgtype: "image",
            image: [
                base64: fileBase64,
                md5: fileMd5
            ]
        ]
        String messageJson = StringEscapeUtils.unescapeJava(JsonOutput.toJson(messageContent))
        println "pushMessage：messageJson=$messageJson"
        HttpUtils.pushMessage(messageKey, messageJson)
    }

    /**
     * markdown消息
     * @param shareApkInfo
     * @param messageKey
     */
    static void pushMarkdownMsg(String messageKey, String buildShortcutUrl, String appName, String appVersionName) {
        String apkUrl = "https://www.pgyer.com/$buildShortcutUrl"
        // 消息体
        def messageContent = [
            msgtype: "markdown",
            markdown: [
                content: """
## 应用更新提醒\n
应用名称: <font color=\\"comment\\">$appName</font>
应用类型: <font color=\\"comment\\">Android</font>
版本名称: <font color=\\"comment\\">$appVersionName</font>
打包分支: <font color=\\"comment\\">${GitCommand.getCurrentBranch()}</font>
更新时间: <font color=\\"comment\\">${DateUtils.format(System.currentTimeMillis())}</font>
更新内容: <font color=\\"comment\\">${GitCommand.getOneRecentLog()}</font>
点击查看应用: <font color=\\"comment\\">[$apkUrl]($apkUrl)</font>
""",
                mentioned_mobile_list: ["@all"]
            ]
        ]
        String messageJson = StringEscapeUtils.unescapeJava(JsonOutput.toJson(messageContent))
        println "pushMessage：messageJson=$messageJson"
        HttpUtils.pushMessage(messageKey, messageJson)
    }
}