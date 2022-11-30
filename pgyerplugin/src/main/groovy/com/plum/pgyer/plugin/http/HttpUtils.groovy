package com.plum.pgyer.plugin.http

import com.plum.pgyer.plugin.bean.PgyerExtension
import com.plum.pgyer.plugin.utils.StringUtils
import okhttp3.*

class HttpUtils {

    /**
     * 获取上传Apk的url地址
     *
     * @throws IOException
     */
    static String getCOSToken(PgyerExtension extension) throws IOException {
        // 1.构建请求体
        FormBody.Builder builder = new FormBody.Builder().add("_api_key", extension.apiKey)
            .add("buildType", extension.buildType)
            .add("oversea", String.valueOf(extension.oversea))
            .add("buildInstallType", String.valueOf(extension.buildInstallType))
            .add("buildDescription", extension.buildDescription)
            .add("buildUpdateDescription", extension.buildUpdateDescription)
        //  设置App安装密码
        if (extension.buildInstallType == 2 && !extension.buildPassword.isEmpty()) {
            builder.add("buildPassword", extension.buildPassword)
        }
        // 设置安装有效期
        if (extension.buildInstallDate == 1) {
            builder.add("buildInstallDate", "1")
            builder.add("buildInstallStartDate", extension.buildInstallStartDate)
            builder.add("buildInstallEndDate", extension.buildInstallEndDate)
        } else {
            builder.add("buildInstallDate", "2")
        }

        if (!extension.buildChannelShortcut.isEmpty()) {
            builder.add("buildChannelShortcut", extension.buildChannelShortcut)
        }
        // 2.创建请求对象
        String url = "https://www.pgyer.com/apiv2/app/getCOSToken"
        Request request = new Request.Builder().url(url).post(builder.build()).build()
        // 3.执行请求
        OkHttpClient client = new OkHttpClient()
        Response response = client.newCall(request).execute()
        String body = ""
        if (response.body() != null) {
            body = response.body().string()
            System.out.println("url=$url\nbody=" + body)
        } else {
            System.out.println("body is null")
        }
        return body
    }

    /**
     * 上传Apk
     * @params file              上传的Apk文件
     * @params uploadApkUrl      https://www.pgyer.com/apiv2/app/getCOSToken 接口返回的 endpoint 参数，上传文件的 URL
     * @params cosKey            https://www.pgyer.com/apiv2/app/getCOSToken 接口返回的 params 参数中的 key 参数
     * @params signature         https://www.pgyer.com/apiv2/app/getCOSToken 接口返回的 params 参数中的 signature 参数
     * @params cosSecurityToken  https://www.pgyer.com/apiv2/app/getCOSToken 接口返回的 params 参数中的 x-cos-security-token 参数
     * @throws Exception
     */
    static boolean upload(File file, String uploadApkUrl, String cosKey, String signature, String cosSecurityToken)
        throws Exception {
        println "file=$file,\nuploadApkUrl=$uploadApkUrl,\ncosKey=$cosKey,\nsignature=$signature,\ncosSecurityToken=$cosSecurityToken"
        if (file == null || StringUtils.isEmpty(uploadApkUrl)
            || StringUtils.isEmpty(cosKey)
            || StringUtils.isEmpty(signature)
            || StringUtils.isEmpty(cosSecurityToken)) {
            println "缺少参数：上传Apk失败"
            return false
        }

        MultipartBody requestBody = new MultipartBody.Builder()
            .addFormDataPart("key", cosKey)
            .addFormDataPart("signature", signature)
            .addFormDataPart("x-cos-security-token", cosSecurityToken)
            .addFormDataPart("x-cos-meta-file-name", file.name)
            .addFormDataPart("file", file.getName(),
                RequestBody.create(file, MediaType.parse("application/vnd.android.package-archive")))
            .build()

        Request request = new Request.Builder().url(uploadApkUrl).post(requestBody).build()

        OkHttpClient client = new OkHttpClient()
        Response response = client.newCall(request).execute()
        println "response code = ${response.code()}"
        if (response.code() == 204) {
            return true // Apk上传成功
        }
        return false
    }

    /**
     * 获取Apk上传的信息
     *
     * @throws Exception
     */
    static String getUploadApkInfo(String apiKey, String buildKey) throws Exception {
        String url = "https://www.pgyer.com/apiv2/app/buildInfo?_api_key=" + apiKey + "&buildKey=" + buildKey
        println "url=$url"
        Request request = new Request.Builder().url(url).get().build()
        OkHttpClient client = new OkHttpClient()
        Response response = client.newCall(request).execute()
        int code = response.code()
        String body = response.body().string()
        println "code=$code,body=$body"
        return body
    }

    /**
     * 发送消息到企业微信
     * @param messageToken
     * @param message 消息内容，json格式
     */
    static void pushMessage(String messageToken, String message) {
        println "====================推送消息start==========================="
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=$messageToken"
        println "url=$url"
        def body = RequestBody.create(MediaType.parse("application/json"), message)
        def request = new Request.Builder().url(url).post(body).build()
        OkHttpClient client = new OkHttpClient()
        def response = client.newCall(request).execute()
        println "code=${response.code()}"
        println "body=${response.body().string()}"
        println "====================推送消息end==========================="
    }

    /**
     * 下载图片
     * @param imgUrl
     * @param file
     */
    static void downloadImg(String imgUrl, File file) {
        def request = new Request.Builder().url(imgUrl).build()
        def client = new OkHttpClient()
        def response = client.newCall(request).execute()
        def inputStream = response.body().byteStream()
        // apk-huawei-debug.png
        if (file.exists()) {
            file.delete()
        }
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        FileOutputStream fos = null
        BufferedInputStream bis = null
        try {
            fos = new FileOutputStream(file)
            byte[] buffer = new byte[1024]
            bis = new BufferedInputStream(inputStream)
            int count = 0
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fos.write(buffer, 0, count)
            }
        } catch (Exception e) {
        } finally {
            try {
                if (fos != null) {
                    fos.flush()
                    fos.close()
                }
                if (bis != null) {
                    bis.close()
                }
            } catch (Exception e1) {
            }
        }
    }

    /**
     * 下载图片
     * @param imgUrl
     * @param file
     */
    static byte[] downloadImg(String imgUrl) {
        def request = new Request.Builder().url(imgUrl).build()
        def client = new OkHttpClient()
        def response = client.newCall(request).execute()
        return response.body().bytes()
    }
}