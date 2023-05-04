package com.dhx.template.common.exception;

import com.dhx.template.common.ErrorCode;
import lombok.Data;

/**
 * @author dhx_
 * @className BusinessException
 * @date : 2023/01/07/ 14:28
 **/
@Data
public class BusinessException extends RuntimeException{

    private static final long serialVersionUID = 165474231423634L;
    /**
     * 错误码
     */
    private int code=500;


    private String message;
    /**
     * 错误描述
     */
    private String description;

    public BusinessException(int code,String description,String message){
        super(message);// 错误信息
        this.code=code;
        this.description=description;
    }

    public BusinessException(ErrorCode errorCode, String description){
        super(errorCode.getMessage());// 错误信息
        this.description=description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());// 错误信息
        this.code=errorCode.getCode();
        this.description=errorCode.getDescription();
    }
}
