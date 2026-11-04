package me.scpark.blogsystem.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一的异常处理。
 *
 * <p>不加这个类，参数校验失败时 Spring 会返回一大坨默认的错误 JSON（还带 timestamp、path 等），
 * 邮箱重复这种数据库约束错误更会直接变成 500。加上 @RestControllerAdvice 之后，
 * 这里写的三个方法就会接管这些异常，把 HTTP 状态码和响应体改成前端能直接用的形状。
 *
 * <p>注意：这里只处理「框架层」的异常。业务层如果需要表达「不允许这样操作」，
 * 可以再定义自己的异常类，在这里加一个 @ExceptionHandler 映射成想要的 4xx。
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 处理 {@code @Valid} 校验失败，例如标题为空、邮箱格式不对。
     *
     * @param ex Spring 抛出的校验异常，里面带着每个字段的报错信息
     * @return 400 和字段级的错误说明
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "请求参数校验失败");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 处理请求体不是合法 JSON、或者字段类型对不上的情况。
     *
     * @param ex 解析请求体时抛出的异常
     * @return 400 和一句提示
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "请求体不是合法的 JSON，或者字段类型不正确");
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 处理数据库约束冲突，最常见的是 members.email 唯一约束。
     *
     * <p>原来这种情况会变成 500，但「邮箱被占了」明明是调用方的输入问题，
     * 返回 409 Conflict 更准确，前端也能据此提示用户换一个邮箱。
     *
     * @param ex 数据库抛出的约束异常
     * @return 409 和提示信息
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "数据冲突：可能是邮箱已被使用，请换一个再试");
        return ResponseEntity.status(409).body(body);
    }
}
