package net.czming.common.util.constants;

public class ValidationConstants {
    public static final String REGEX_ID_CARD = "^[1-9]\\d{5}(18|19|20|21|22)?\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|[Xx])$";
    public static final String REGEX_PHONE_NUMBER = "^1[3-9]\\d{9}$";
    public static final String REGEX_CODE = "^\\d{6}$";
    public static final String REGEX_LOGIN_TOKEN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String REGEX_USER_ROLE = "^(ADMIN|AUDITOR|USER)$";
    public static final String REGEX_USER_STATUS = "^(ENABLED|DISABLED)$";
    public static final String REGEX_VIOLATION_STATUS = "^(UNAPPEALED|APPEALING|APPEAL_APPROVED|APPEAL_REJECTED)$";
    public static final String REGEX_APPEAL_STATUS = "^(PROCESSING|APPROVED|REJECTED)$";
    public static final String REGEX_NOT_BLANK_AND_NULLABLE = "^\\s*\\S[\\s\\S]*$";
    public static final String REGEX_CAMERA_SECRETE_KEY = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";


    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
