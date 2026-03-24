package net.czming.common.util.constants;

public class RedisConstants {
    public static final String USER_TOKEN_KEY = "USER:TOKEN:";
    public static final String LOGIN_USER_KEY = "LOGIN:USER:";
    public static final Long TOKEN_TTL = 7200L;

    public static final String LOGIN_CODE_KEY = "CODE:LOGIN:";
    public static final String REGISTER_CODE_KEY = "CODE:REGISTER:";
    public static final String CHANGE_PASSWORD_CODE_KEY = "CODE:PWD:";
    public static final String CHANGE_PHONE_KEY = "CODE:PHONE:";
    public static final Long CODE_TTL = 300L;

    public static final String CAMERA_SECRET_KEY = "CAMERA:SECRET:";
    public static final Long CAMERA_SECRET_TTL = 1200L;

}
