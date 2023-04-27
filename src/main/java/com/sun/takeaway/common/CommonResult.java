package com.sun.takeaway.common;

import lombok.Data;
import java.io.Serializable;

/**
 * @author sun
 */
@Data
public class CommonResult<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    public CommonResult(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(0, data, "ok");
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return new CommonResult<>(errorCode.getCode(), null, errorCode.getMessage());
    }

    public static <T> CommonResult<T> error(int code, String message) {
        return new CommonResult<>(code, null, message);
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode, String message) {
        return new CommonResult<>(errorCode.getCode(), null, message);
    }
}
