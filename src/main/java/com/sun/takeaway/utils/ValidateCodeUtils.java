package com.sun.takeaway.utils;

import java.util.Random;

/**
 * 随机生成验证码工具类
 */
public class ValidateCodeUtils {
    /**
     * 随机生成验证码
     * @param length 长度为 4 或 6 位
     */
    public static Integer generateValidateCode(int length) {
        Integer code = null;
        if (length == 4) {
            // 生成随机数，最大为 9999
            code = new Random().nextInt(9999);
            if (code < 1000) {
                // 保证随机数为4位数字
                code = code + 1000;
            }
        } else if (length == 6) {
            // 生成随机数，最大为 999999
            code = new Random().nextInt(999999);
            if (code < 100000) {
                // 保证随机数为6位数字
                code = code + 100000;
            }
        } else {
            throw new RuntimeException("只能生成 4 或 6 位数字验证码");
        }
        return code;
    }

    /**
     * 随机生成指定长度字符串验证码
     * @param length 长度
     */
    public static String generateValidateCode4String(int length) {
        String hash = Integer.toHexString(new Random().nextInt());
        return hash.substring(0, length);
    }
}
