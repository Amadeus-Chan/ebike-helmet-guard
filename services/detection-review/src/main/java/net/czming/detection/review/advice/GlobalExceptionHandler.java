package net.czming.detection.review.advice;

import feign.FeignException;
import net.czming.common.exception.AccessDeniedException;
import net.czming.common.exception.BusinessException;
import net.czming.common.result.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<Void> exception(Exception e) {
        String finalMessage = "异常类名：" + e.getClass() + "； 异常信息：" + e.getMessage();
        return R.fail(finalMessage);
    }

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(final BusinessException e) {
        String finalMessage =  "错误代码：" + e.getErrorCode() +  "；错误信息：" + e.getMessage() + "；" + "错误描述：" + e.getDetailMessage();
        return R.fail(finalMessage);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public R<Map<String,String>> handleHandlerMethodValidationException(final HandlerMethodValidationException e) {
        Map<String, String> errorMap = new HashMap<>();

        e.getAllValidationResults().forEach(validationResult -> {
            String parameterName = validationResult.getMethodParameter().getParameterName();
            String defaultMessage = validationResult.getResolvableErrors().get(0).getDefaultMessage();
            errorMap.put(parameterName, defaultMessage);
        });

        return R.fail("参数校验失败", errorMap);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Map<String, String>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        HashMap<String, String> map = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            map.put(field, message);
        });

        return R.fail("参数校验错误", map);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> businessException() {
        return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(FeignException.class)
    public R<String> feignException(final FeignException e) {
        return R.fail("Feign调用错误：", e.getMessage());
    }
}
