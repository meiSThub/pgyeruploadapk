package com.test.pgyer.upload.apk.http;

import java.io.File;
import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * Author: Lenovo
 * Date: 2022/11/25 16:29
 * Desc:
 */
public class UploadFile {

    /**
     * 获取上传Apk的url地址
     *
     * @throws IOException
     */
    public String getCOSToken(PgyerExtension extension) throws IOException {
        // 1.构建请求体
        FormBody.Builder builder = new FormBody.Builder().add("_api_key", extension.apiKey)
                .add("buildType", extension.buildType)
                .add("oversea", String.valueOf(extension.oversea))
                .add("buildInstallType", String.valueOf(extension.buildInstallType))
                .add("buildDescription", extension.buildDescription)
                .add("buildUpdateDescription", extension.buildUpdateDescription);
        if (extension.buildInstallType == 2 && !extension.buildPassword.isEmpty()) {
            builder.add("buildPassword", extension.buildPassword);
        }
        if (extension.buildInstallDate == 1) {
            builder.add("buildInstallDate", "1");
            builder.add("buildInstallStartDate", extension.buildInstallStartDate);
            builder.add("buildInstallEndDate", extension.buildInstallEndDate);
        } else {
            builder.add("buildInstallDate", "2");
        }

        if (!extension.buildChannelShortcut.isEmpty()) {
            builder.add("buildChannelShortcut", extension.buildChannelShortcut);
        }
        // 2.创建请求对象
        Request request =
                new Request.Builder().url("https://www.pgyer.com/apiv2/app/getCOSToken").post(builder.build()).build();
        // 3.执行请求
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        String body = "";
        if (response.body() != null) {
            body = response.body().string();
            System.out.println("body=" + body);
        } else {
            System.out.println("body is null");
        }
        return body;
    }

    /**
     * 上传Apk
     *
     * @throws Exception
     */
    public boolean upload(File file, String cosTokenJson) throws Exception {
        if (cosTokenJson == null || cosTokenJson.isEmpty()) {
            System.out.println("获取COSToken失败");
            return false;
        }
        JSONObject jsonObject = new JSONObject(cosTokenJson);
        String uploadFileKey = jsonObject.optString("key");
        String uploadUrl = jsonObject.optString("endpoint");
        JSONObject params = jsonObject.optJSONObject("params");
        if (params == null) {
            return false;
        }
        String signature = params.optString("signature");
        String securityToken = params.optString("x-cos-security-token");
        String key = params.optString("key");

        MultipartBody requestBody = new MultipartBody.Builder().addFormDataPart("key", key)
                .addFormDataPart("signature", signature)
                .addFormDataPart("x-cos-security-token", securityToken)
                .addFormDataPart("x-cos-meta-file-name", file.getName())
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/vnd.android.package-archive")))
                .build();

        Request request = new Request.Builder().url(uploadUrl).post(requestBody).build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if (response.code() == 204) {
            return true;// Apk上传成功
        }
        return false;
    }

    /**
     * 获取Apk上传的信息
     *
     * @throws Exception
     */
    public void getUploadApkInfo(String apiKey, String buildKey) throws Exception {
        String url = "https://www.pgyer.com/apiv2/app/buildInfo?_api_key=" + apiKey + "&buildKey=" + buildKey;
        System.out.println("url=" + url);
        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        int code = response.code();
        System.out.println("code=" + code);
        switch (code) {
            case 1246:
            case 1247:
                Thread.sleep(3000);
                getUploadApkInfo(apiKey, buildKey);
                break;
            case 0:
                if (response.body() == null) {
                    System.out.println("body is null");
                    return;
                }
                String body = response.body().string();
                JSONObject jsonObject = new JSONObject(body);
                String buildShortcutUrl = jsonObject.optString("buildShortcutUrl");
                String buildQRCodeURL = jsonObject.optString("buildQRCodeURL");
                System.out.println("buildShortcutUrl=" + buildShortcutUrl + ",buildQRCodeURL=" + buildQRCodeURL);
                break;
            case 1216:
            default:
                return;
        }
    }
}
