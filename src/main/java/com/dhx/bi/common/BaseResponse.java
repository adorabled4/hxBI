package com.dhx.bi.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dhx_
 * @className BaseResponse
 * @date : 2023/01/07/ 14:33
 **/
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    private int code;

    private T data;//  controller 中的不同的方法返回值的类型不同

    private String message;

    private String description;


    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String description) {
        if(code!=200){
            new BaseResponse<>(code, data, "error", description);
        }
        new BaseResponse<>(code, data, "ok", description);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode, String description) {
        this(errorCode.getCode(), null, errorCode.getMessage(), description);
    }


    public BaseResponse(ErrorCode errorCode, String message, String description) {
        this(errorCode.getCode(), null, message, description);
    }

    /**
     * 正常返回
     * @param data
     * @param <T>
     * @return
     */
    public <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(200, data, "ok", "");
    }
}
