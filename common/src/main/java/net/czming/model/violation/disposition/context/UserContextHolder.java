package net.czming.model.violation.disposition.context;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> userContextHolder = new ThreadLocal<>();

    public static UserContext getUserContext() {
        return userContextHolder.get();
    }

    public static void saveUserContext(UserContext userContext) {
        userContextHolder.set(userContext);
    }

    public static void removeUserContext() {
        userContextHolder.remove();
    }
}
