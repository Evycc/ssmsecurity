package com.evy.jing.controller;

import com.evy.jing.EnumMsg.EnumUpload;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.util.LoggerUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 捕获控制器层异常
 */
@ControllerAdvice
public class ExceptionController {

    /**
     * 捕获文件超出大小异常
     * @param ex
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String uploadException(MaxUploadSizeExceededException ex){
        LoggerUtils.error(getClass(), ex, "上传文件过大!");
        return "redirect:/uploadFile";
    }
}
