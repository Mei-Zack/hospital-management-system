package com.hims.exception;

import com.hims.common.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "没有权限访问该资源");
    }

    /**
     * 处理数据完整性违反异常（包括JSON格式错误）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String message = e.getMessage();

        // 检查是否是JSON格式错误
        if (message != null && message.contains("Invalid JSON")) {
            return Result.error("JSON格式错误：请确保数据符合正确的JSON格式。例如：{\"周一\": \"上午9:00-11:30\", \"周三\": \"下午2:00-5:00\"}");
        }

        return Result.error("数据格式错误：" + message);
    }

    /**
     * 处理JSON解析异常
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIOException(IOException e) {
        if (e.getMessage().contains("JSON")) {
            return Result.error("JSON解析错误：" + e.getMessage());
        }
        return Result.error("IO错误：" + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "服务器内部错误：" + e.getMessage());
    }
}