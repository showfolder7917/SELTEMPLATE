package com.sp.selfsp.common.web;

// 统一响应壳用于把异常收口成固定 JSON 结构。
import com.sp.selfsp.common.util.CommonResponse;
// HttpStatus 负责标记失败接口的 HTTP 状态。
import org.springframework.http.HttpStatus;
// ResponseEntity 负责同时返回状态码和响应体。
import org.springframework.http.ResponseEntity;
// ExceptionHandler 用于声明异常收口入口。
import org.springframework.web.bind.annotation.ExceptionHandler;
// RestControllerAdvice 负责对所有控制器统一生效。
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常收口器。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数和业务校验异常。
     *
     * @param error 异常对象
     * @return 统一失败响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgument(IllegalArgumentException error) {
        // 缺省消息兜底成固定文案，避免前端拿到空字符串。
        String message = error.getMessage() == null || error.getMessage().isBlank()
            ? "请求参数不合法"
            : error.getMessage();
        // 参数或业务校验失败统一返回 400。
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(CommonResponse.failure(400, message));
    }
}
