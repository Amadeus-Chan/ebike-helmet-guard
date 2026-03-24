package net.czming.common.exception;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    BIZ_FAILED("10001","业务处理失败"),
    SERVICE_CALL_FAILED("20001", "微服务间调用失败"),
    EXTERNAL_API_FAILED("30001", "调用外部接口失败");

    private final String errorCode;
    private final String message;

    ErrorEnum(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

