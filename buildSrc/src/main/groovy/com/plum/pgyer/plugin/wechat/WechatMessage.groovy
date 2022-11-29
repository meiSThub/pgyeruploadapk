package com.plum.pgyer.plugin.wechat

import com.plum.pgyer.plugin.command.GitCommand
import com.plum.pgyer.plugin.http.HttpUtils
import com.plum.pgyer.plugin.utils.DateUtils
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
    static void pushMessage(Object shareApkInfo, String messageKey) {
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
}