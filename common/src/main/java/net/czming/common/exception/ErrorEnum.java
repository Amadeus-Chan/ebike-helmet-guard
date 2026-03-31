package net.czming.common.exception;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    SYSTEM_INIT_FAILED("00001","系统初始化失败"),
    BIZ_FAILED("10001","业务处理失败"),
    INTERNAL_CALL_FAILED("20001", "微服务间调用失败"),
    EXTERNAL_CALL_FAILED("30001", "调用外部接口失败");

    private final String errorCode;
    private final String message;

    ErrorEnum(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

