package com.plum.pgyer.plugin.tasks

import com.plum.pgyer.plugin.bean.PgyerExtension
import com.plum.pgyer.plugin.bean.WechatHookExtension
import com.plum.pgyer.plugin.http.HttpUtils
import com.plum.pgyer.plugin.utils.StringUtils
import com.plum.pgyer.plugin.wechat.WechatMessage
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * 上传Apk任务
 */
class UploadApkTask extends DefaultTask {

    public String buildType = ""
    public String flavor = ""

    @TaskAction
    void uploadApk() {
        println "UploadApkTak,pgyer=${project.extensions.pgyer}"
        println "taskName=$name"
        String outputs = "${project.getBuildDir()}${File.separator}outputs${File.separator}apk${File.separator}"
        if (flavor == null || flavor.isEmpty()) {
            outputs = "$outputs$buildType"
        } else {
            outputs = "$outputs$flavor${File.separator}$buildType"
        }
        println "outputs=$outputs"
        println "listFiles = ${project.file(outputs).listFiles()}"
        project.fileTree(outputs).filter { file ->
            file.name.endsWith(".apk")
        }.each { file ->
            println "file name = ${file.absolutePath}"
            // 找到Apk文件，开始上传Apk到蒲公英
            def uploadResult = uploadApk(file, project.extensions.pgyer)
            // 推送消息到企业微信，让测试方便下载Apk
            pushMessage(uploadResult)
        }
    }

    /**
     * 推送消息到企业微信
     * @param shareApkInfo
     */
    void pushMessage(Object shareApkInfo) {
        WechatHookExtension wechatHook = project.extensions.pgyer.wechatHook
        if (!wechatHook.open || StringUtils.isEmpty(wechatHook.messageKey)) {
            println "pushMessage：wechatHook,open=${wechatHook.open},messageKey=${wechatHook.messageKey}"
            return
        }
        // {"buildKey":"e92ff62b3f04f3ae893ce15b54b1ab80","buildType":"2","buildIsFirst":"0","buildIsLastest":"1","buildFileKey":"e92ff62b3f04f3ae893ce15b54b1ab80.apk","buildFileName":"app-huawei-debug.apk","buildFileSize":"5826489","buildName":"pgyer-upload-apk","buildVersion":"1.0","buildVersionNo":"1","buildBuildVersion":"12","buildIdentifier":"com.test.pgyer.upload.apk","buildIcon":"ae61335d32863e6a882839434cf52bb7","buildDescription":"","buildUpdateDescription":"","buildScreenshots":"","buildShortcutUrl":"orIh","buildCreated":"2022-11-26 20:00:39","buildUpdated":"2022-11-26 20:00:39","buildQRCodeURL":"https:\/\/www.pgyer.com\/app\/qrcodeHistory\/cd33a8930d0a5ab0475ad73043837ca7b26d9a9eead66aa7bb2df4c964a0f228"}
        //        WechatMessage.pushMessage(shareApkInfo, wechatHook.messageKey)

        switch (wechatHook.msgType) {
            case 0: WechatMessage.pushTemplateCardMsg(shareApkInfo, wechatHook.messageKey)
                break
            case 1: WechatMessage.pushNewsMsg(shareApkInfo, wechatHook.messageKey)
                break
            case 2: WechatMessage.pushImageMsg(wechatHook.messageKey, shareApkInfo.buildQRCodeURL)
                break
            default:
                println "未知消息类型：${wechatHook.msgType}"
        }
    }

    /**
     * 上传Apk到蒲公英
     * @param file Apk文件
     * @param extension 扩招信息
     * @return
     */
    Object uploadApk(File file, PgyerExtension extension) {
        //1.获取上传Apk的url
        String cosTokenResult = HttpUtils.getCOSToken(extension)
        //2.上传Apk
        JsonSlurper slurper = new JsonSlurper()
        def cosToken = slurper.parseText(cosTokenResult).data
        println "cosToken=$cosToken"
        String uploadApkUrl = cosToken.endpoint
        String cosKey = cosToken.params.key
        String signature = cosToken.params.signature
        String cosSecurityToken = cosToken.params.get("x-cos-security-token")
        String uploadSuccess = HttpUtils.upload(file, uploadApkUrl, cosKey, signature, cosSecurityToken)
        println "Apk上传是否成功：$uploadSuccess,file=$file"
        //3.Apk上传成功之后，获取Apk下载链接
        String buildKey = cosToken.key
        String shareApkInfo = HttpUtils.getUploadApkInfo(extension.apiKey, buildKey)
        println "shareApkInfo=$shareApkInfo"

        def shareResult = checkShareInfo(shareApkInfo, extension.apiKey, buildKey)
        println "shareResult=$shareResult"
        return shareResult
    }

    /**
     * 解析 Apk上传成功后 的信息，获取Apk的下载地址和二维码图片等
     * @param shareApkInfo
     * @param apiKey
     * @param buildKey
     * @return
     */
    private Object checkShareInfo(String shareApkInfo, String apiKey, String buildKey) {
        if (StringUtils.isEmpty(shareApkInfo)) {
            return null
        }
        JsonSlurper slurper = new JsonSlurper()
        def shareResult = slurper.parseText(shareApkInfo)
        switch (shareResult.code) {
            case 1246:
                //  应用正在发布中
            case 1247:
                println "Apk正在发布中，等待1s再请求"
                Thread.sleep(1000)
                checkShareInfo(HttpUtils.getUploadApkInfo(apiKey, buildKey), apiKey, buildKey)
                break
            case 0:
                println "获取Apk下载链接成功"
                return shareResult.data
            case 1216:
            default:
                return null
        }
    }
}