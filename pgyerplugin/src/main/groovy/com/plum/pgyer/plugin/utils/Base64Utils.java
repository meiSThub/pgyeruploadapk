package com.plum.pgyer.plugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Author: Lenovo
 * Date: 2022/11/29 18:47
 * Desc:
 */
public class Base64Utils {
    /**
     * 编码
     *
     * @throws UnsupportedEncodingException
     */
    public static String encoder(String text) throws UnsupportedEncodingException {
        Base64.Encoder encoder = Base64.getEncoder();
        final byte[] textByte = text.getBytes("UTF-8");
        final String encodedText = encoder.encodeToString(textByte);
        System.out.println("encodedText=" + encodedText);
        return encodedText;
    }

    /**
     * 解码
     *
     * @throws UnsupportedEncodingException
     */
    public static String decoder(String encodedText) throws UnsupportedEncodingException {
        final Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(encodedText), "UTF-8");
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param file 生成64编码的图片的路径
     */
    public static String encoder(File file) {
        Base64.Encoder encoder = Base64.getEncoder();
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对字节数组Base64编码
        return encoder.encodeToString(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param bytes 图片字节码数组
     */
    public static String encoder(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        // 对字节数组Base64编码
        return encoder.encodeToString(bytes);// 返回Base64编码过的字节数组字符串
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imgStr        转换为图片的字符串
     * @param imgCreatePath 将64编码生成图片的路径
     */
    public static boolean decoder(String imgStr, String imgCreatePath) {
        final Base64.Decoder decoder = Base64.getDecoder();
        if (imgStr == null) {
            // 图像数据为空
            return false;
        }
        try {
            // Base64解码
            byte[] b = decoder.decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgCreatePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
