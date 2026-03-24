package net.czming.detection.review.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.czming.common.annotation.Loggable;
import net.czming.common.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogAspect {

    private final ObjectMapper objectMapper;

    public LogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Around("@annotation(net.czming.common.annotation.Loggable) || @within(net.czming.common.annotation.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        String className = proceedingJoinPoint.getTarget().getClass().getName();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();


        boolean includeParams = false;
        if (method.isAnnotationPresent(Loggable.class)) {
            includeParams = method.getAnnotation(Loggable.class).includeParams();
        } else {
            Class<?> targetClass = proceedingJoinPoint.getTarget().getClass();
            includeParams = targetClass.getAnnotation(Loggable.class).includeParams();
        }


        String args = includeParams ? objectMapper.writeValueAsString(proceedingJoinPoint.getArgs()) : "";

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("【日志记录】：方法[{}.{}] 参数：({}) 开始执行 ", className, methodName, args);

        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
            stopWatch.stop();
            log.info("【日志记录】：方法[{}.{}] 参数：({}) 执行结束，耗时{}ms ", className, methodName, args, stopWatch.getTotalTimeMillis());
        } catch (BusinessException e) {
            stopWatch.stop();
            log.error("【异常日志】：方法[{}.{}] 参数：({}) 执行异常，耗时{}ms； 异常类型：{}，异常信息：{}， 异常描述：{}",
                    className, methodName, args, stopWatch.getTotalTimeMillis(), e.getClass().getName(), e.getMessage(), e.getDetailMessage(), e);
            throw e;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("【异常日志】：方法[{}.{}] 参数：({}) 执行异常，耗时{}ms； 异常类型：{}，异常信息：{}",
                    className, methodName, args, stopWatch.getTotalTimeMillis(), e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
        return result;
    }
}
