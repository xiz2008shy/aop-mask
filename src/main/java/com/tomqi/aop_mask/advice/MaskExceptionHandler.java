package com.tomqi.aop_mask.advice;

import com.tomqi.aop_mask.Exception.MValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 360 Financial Copyright
 *
 * @author YanWenqi
 * @description 异常处理器
 * @date 2020/10/20 11:06
 */

@RestControllerAdvice
public class MaskExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(MaskExceptionHandler.class);

    @ExceptionHandler(MValidationException.class)
    public ResponseEntity<String> handleValidationException(MValidationException e) {
        log.info("PRO 参数验证失败MValidationException! >>>>>>>>>>>\n>>>>>>>>>>> 错误消息:[{}]\n>>>>>>>>>>> 对应方法:[{}#{}]", e.getMessage(),e.getClazzName(),e.getMethodName());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
