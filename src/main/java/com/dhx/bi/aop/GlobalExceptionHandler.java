package com.dhx.bi.aop;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * @author adorabled4
 * @className GlobalExceptionHandler
 * @date : 2023/05/04/ 17:18
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 常见的参数异常处理
     */
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class,
            ConversionFailedException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public BaseResponse handleMethodArgumentTypeMismatchException(HttpMessageNotReadableException e) {
        return ResultUtil.error(ErrorCode.PARAMS_ERROR);
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Object> handleRRException(BusinessException e) {
        log.error("BusinessException", e.getDescription());
        return ResultUtil.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseResponse handlerNoFoundException(Exception e) {
        log.error("RuntimeException", e);
        return ResultUtil.error(ErrorCode.NOT_FOUND);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : allErrors) {
                sb.append(objectError.getDefaultMessage()).append(";");
            }
        }
        log.error("MethodArgumentNotValidException", e);
        return ResultUtil.error(ErrorCode.PARAMS_ERROR, sb.toString());
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        log.error("Exception", e);
        return ResultUtil.error();
    }
}
