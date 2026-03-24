package net.czming.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final String detailMessage;


    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.errorCode = errorEnum.getErrorCode();
        this.detailMessage = "No Description Provided";
    }

    public BusinessException(ErrorEnum errorEnum, String detailMessage) {
        super(errorEnum.getMessage());
        this.errorCode = errorEnum.getErrorCode();
        this.detailMessage = detailMessage;
    }
}
