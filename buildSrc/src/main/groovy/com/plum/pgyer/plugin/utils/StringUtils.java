package com.plum.pgyer.plugin.utils;

/**
 * Author: Lenovo
 * Date: 2022/11/26 19:26
 * Desc:
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
