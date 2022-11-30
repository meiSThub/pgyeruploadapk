package com.plum.pgyer.plugin.utils

import java.text.SimpleDateFormat

class DateUtils {
    static String format(long time) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time))
    }
}