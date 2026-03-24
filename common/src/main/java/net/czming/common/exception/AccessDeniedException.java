package net.czming.common.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("权限不足");
    }
}
