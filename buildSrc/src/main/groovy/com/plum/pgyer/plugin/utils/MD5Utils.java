package com.plum.pgyer.plugin.utils;

/**
 * Author: Lenovo
 * Date: 2022/11/29 19:14
 * Desc:
 */

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密
 *
 * @author a
 */
public class MD5Utils {

    /***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr) {
        return DigestUtils.md5Hex(inStr);
    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(InputStream inStr) throws IOException {
        return DigestUtils.md5Hex(inStr);
    }

    public static String convertMD5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}