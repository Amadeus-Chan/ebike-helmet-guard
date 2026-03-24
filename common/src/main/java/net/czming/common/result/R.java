package net.czming.common.result;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String message;
    private T data;

    public R() {
    }

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static R<Void> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return ok("OK", data);
    }

    public static <T> R<T> ok(String message ,T data) {
        return new R<>(200, message, data);
    }

    public static R<Void> fail(String message) {
        return fail(message, null);
    }

    public static <T> R<T> fail(String message, T data) {
        return new R<>(500, message, data);
    }
}
